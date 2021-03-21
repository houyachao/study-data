SpringBoot 集成Shiro

① 导入相关依赖的jar包

```java
<!-- springboot 整合 shiro 需要用到的依赖 -->
        <dependency>
            <groupId>org.apache.shiro</groupId>
            <artifactId>shiro-spring</artifactId>
            <version>1.4.0</version>
        </dependency>
```

springboot中集成shiro相对简单，只需要两个类：一个是shiroConfig类，一个是CustonRealm类。

ShiroConfig类：
顾名思义就是对shiro的一些配置，相对于之前的xml配置。包括：过滤的文件和权限，密码加密的算法，其用注解等相关功能。

CustomRealm类：
自定义的CustomRealm继承AuthorizingRealm。并且重写父类中的doGetAuthorizationInfo（权限相关）、doGetAuthenticationInfo（身份认证）这两个方法。

```java

@Configuration
public class ShiroConfig {

    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager((org.apache.shiro.mgt.SecurityManager) securityManager);
        shiroFilterFactoryBean.setLoginUrl("/login");
        shiroFilterFactoryBean.setUnauthorizedUrl("/notRole");
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // <!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
        filterChainDefinitionMap.put("/webjars/**", "anon");
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/", "anon");
        filterChainDefinitionMap.put("/front/**", "anon");
        filterChainDefinitionMap.put("/api/**", "anon");

        filterChainDefinitionMap.put("/admin/**", "authc");
        filterChainDefinitionMap.put("/user/**", "authc");
        //主要这行代码必须放在所有权限设置的最后，不然会导致所有 url 都被拦截 剩余的都需要认证
        filterChainDefinitionMap.put("/**", "authc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;

    }

    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager defaultSecurityManager = new DefaultWebSecurityManager();
        defaultSecurityManager.setRealm(customRealm());
        return defaultSecurityManager;
    }

    @Bean
    public CustomRealm customRealm() {
        CustomRealm customRealm = new CustomRealm();
        return customRealm;
    }
}



public class CustomRealm extends AuthorizingRealm {

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        Set<String> stringSet = new HashSet<>();
        stringSet.add("user:show");
        stringSet.add("user:admin");
        info.setStringPermissions(stringSet);
        return info;
    }

    /**
     * 这里可以注入userService,为了方便演示，我就写死了帐号了密码
     * private UserService userService;
     * <p>
     * 获取即将需要认证的信息
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("-------身份认证方法--------");
        String userName = (String) authenticationToken.getPrincipal();
        String userPwd = new String((char[]) authenticationToken.getCredentials());
        //根据用户名从数据库获取密码
        String password = "123";
        if (userName == null) {
            throw new AccountException("用户名不正确");
        } else if (!userPwd.equals(password )) {
            throw new AccountException("密码不正确");
        }
        return new SimpleAuthenticationInfo(userName, password,getName());
    }
}
```

shiroConfig 也不复杂，基本就三个方法。再说这三个方法之前，我想给大家说一下shiro的三个核心概念：

1. Subject： 代表当前正在执行操作的用户，但Subject代表的可以是人，也可以是任何第三方系统帐号。当然每个subject实例都会被绑定到SercurityManger上。

2. SecurityManger:  SecurityManager是Shiro核心，主要协调Shiro内部的各种安全组件，这个我们不需要太关注，只需要知道可以设置自定的Realm。

3. Realm:  用户数据和Shiro数据交互的桥梁。比如需要用户身份认证、权限认证。都是需要通过Realm来读取数据。

   ​

2、利用注解配置权限：
其实，我们完全可以不用注解的形式去配置权限，因为在之前已经加过了：DefaultFilter类中有perms（类似于perms[user:add]）这种形式的。但是试想一下，这种控制的粒度可能会很细，具体到某一个类中的方法，那么如果是配置文件配，是不是每个方法都要加一个perms？但是注解就不一样了，直接写在方法上面，简单快捷。
很简单，主需要在config类中加入如下代码，就能开启注解：

```java
@Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }
    
 /**
     * *
     * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
     * *
     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator(可选)和AuthorizationAttributeSourceAdvisor)即可实现此功能
     * * @return
     */
    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }
    
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
        return authorizationAttributeSourceAdvisor;
    }


//使用方式
@RequestMapping("/user")
@Controller
public class UserController {
    @RequiresPermissions("user:list")
    @ResponseBody
    @RequestMapping("/show")
    public String showUser() {
        return "这是学生信息";
    }
}
```

# 密码采用加密方式进行验证：

其实上面的功能已经基本满足我们的需求了，但是唯一一点美中不足的是，密码都是采用的明文方式进行比对的。那么shiro是否提供给我们一种密码加密的方式呢？答案是肯定。
shiroConfig中加入加密配置：

```java
@Bean(name = "credentialsMatcher")
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        // 散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        // 散列的次数，比如散列两次，相当于 md5(md5(""));
        hashedCredentialsMatcher.setHashIterations(2);
        // storedCredentialsHexEncoded默认是true，此时用的是密码加密用的是Hex编码；false时用Base64编码
        hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);
        return hashedCredentialsMatcher;
    }
customRealm初始化的时候耶需要做一些改变：
@Bean
    public CustomRealm customRealm() {
        CustomRealm customRealm = new CustomRealm();
        // 告诉realm,使用credentialsMatcher加密算法类来验证密文
        customRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        customRealm.setCachingEnabled(false);
        return customRealm;
    }

```

流程是这样的，用户注册的时候，程序将明文通过加密方式加密，存到数据库的是密文，登录时将密文取出来，再通过shiro将用户输入的密码进行加密对比，一样则成功，不一样则失败。
我们可以看到这里的加密采用的是MD5，而且是加密两次（MD5(MD5)）。
shiro提供了SimpleHash类帮助我们快速加密：

```java
public static String MD5Pwd(String username, String pwd) {
        // 加密算法MD5
        // salt盐 username + salt
        // 迭代次数
        String md5Pwd = new SimpleHash("MD5", pwd,
                ByteSource.Util.bytes(username + "salt"), 2).toHex();
        return md5Pwd;
    }
也就是说注册的时候调用一下上面的方法得到密文之后，再存入数据库。
在CustomRealm进行身份认证的时候我们也需要作出改变：
System.out.println("-------身份认证方法--------");
        String userName = (String) authenticationToken.getPrincipal();
        String userPwd = new String((char[]) authenticationToken.getCredentials());
        //根据用户名从数据库获取密码
        String password = "2415b95d3203ac901e287b76fcef640b";
        if (userName == null) {
            throw new AccountException("用户名不正确");
        } else if (!userPwd.equals(userPwd)) {
            throw new AccountException("密码不正确");
        }
        //交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配
        return new SimpleAuthenticationInfo(userName, password,
                ByteSource.Util.bytes(userName + "salt"), getName());
```





shiroFilter方法：
这个方法看名字就知道了：

shiro的过滤器，可以设置登录页面（setLoginUrl）、权限不足跳转页面（setUnauthorizedUrl）、具体某些页面的权限控制或者身份认证。

注意：这里是需要设置SecurityManager（setSecurityManager）。
默认的过滤器还有：anno、authc、authcBasic、logout、noSessionCreation、perms、port、rest、roles、ssl、user过滤器。
具体的大家可以查看package org.apache.shiro.web.filter.mgt.DefaultFilter。这个类，常用的也就authc、anno。

说明：
自定义的Realm类继承AuthorizingRealm类，并且重载doGetAuthorizationInfo和doGetAuthenticationInfo两个方法。
doGetAuthorizationInfo： 权限认证，即登录过后，每个身份不一定，对应的所能看的页面也不一样。
doGetAuthenticationInfo：身份认证。即登录通过账号和密码验证登陆人的身份信息。



shiro 提供了与web  集成的支持，其通过一个shiroFilter 入口来拦截需要安全控制的URL，然后进行相应的控制。

shiroFilter 类似于 SpringMVC 这种web 框架的前端控制器，是安全控制的入口点，其负责读取配置（如ini 配置文件），然后URL 是否需要登录、权限工作。

ShiroFilter 的工作原理：

​                        ShiroFilter

浏览器 ---->  filterChainDefintions  ---> 经过认证或不被拦截的页面。

浏览器 ---->  loginUrl -----> 没有经过认证。



② URL 匹配模式：

url模式使用Ant 风格模式

Ant 路径通配符支持 ？、*、**、注意通配符匹配不包括目录分隔符  “/ ”：

-- ？：匹配一个字符，如 /admin？ 将匹配 /admin1， 但不匹配 /admin或 /admin/；

-- *：  匹配零个或多个字符串，如/admin 将匹配 /admin、/admin123，但不匹配 /admin/1；

-- * *： 匹配路径中的零个或多个路径，如 /admin/**  将匹配 /admin/a 或 /admin/a/b

③URL 匹配顺序

URL 权限采取第一次匹配优先的方式，即从头开始使用第一个匹配的url模式对应的连拦截器链。

如： 	

​	---/bb/** = filter1

​	--/bb/aa = filter2

​	--/** =filter3

-如果请求的url是  “/bb/aa”，因为按照声明顺序进行匹配，那么将使用filter1 进行拦截。

③Shiro API 来完成用户的认证

​	1、获取Subject 类型的实例

​		Subject currentUser = SecurityUtils.getSubject();

​	2、 判断用户是否已经登录

​		currentUser.isAuthenticated();

​	3、使用UsernamePasswordToken 对象封装用户名及密码

​		UsernamePasswordToken token = new UsernamePasswordToken("name","password");

​	4、 使用Subject 实例中的login(token)

 		currentUser.login(token);

​	5、 Realm ：从数据库中获取安全数据的。



盐值加密：原有算法加密的基础上。

​	1、前端token 当中获取的密码应该进行盐值加密。

​		realm 方法返回的对象，调用下面这个构造。

​		ByteSource salt = ByteSource.Util.bytes(String);

​		new SimpleAuthenticationInfo(prinicipal, sh, salt, realmName);



④ 授权

 	1、 授权，也叫访问控制，即在应用中控制谁访问那些资源（如访问页面、编辑数据、页面操作等）。在授权中需了解的几个关键对象：主体（Subject），资源（Resource），权限（Permission），角色（Role）、

​	2、  主体（Subject）：访问应用的用户，在Shiro 中使用Subject 代表该用户。用户只有授权后才允许访问相应的资源。

​	3、	资源（Resource）：在应用中用户可以访问的URL，比如访问JSP页面，查看、编辑某些数据，访问某个业务方法，打印文本等等都是资源。用户只要授权后才能访问。

​	4、	权限（Permission）：安全策略中的原子授权单位，通过权限我们可以表示在应用中用户有没有操作某个资源的权利。即权限表示在应用中用户能不能访问某个资源，如：访问用户列表页面查看、新增、删除用户数据（即很多时候都是CRUD 式 权限控制）等，权限代表了用户有没有操作某个资源的权利，即反映在某个资源上的操作允不允许。

​	Shiro 支持粗细度权限（如 用户模块的所有权限）和细粒度权限（操作某个用户的权限，即实例级别的）。

​	5、	角色（Role）：权限的集合，一般情况下会赋予用户角色而不是权限，即这样用户可以拥有一组权限，赋予权限时比较方便。典型的如：项目经理，技术总监，CTO等都是角色，不同的角色拥有一组不同的权限。































