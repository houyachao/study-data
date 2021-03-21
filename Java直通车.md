# 1、事务传播行为

​	事务传播----Propagation

| REQUIRED      | 使用当前的事务，如果当前没有事务，则自己新建一个事务，子方法是必须运行在一个事务中的，如果当前存在事务，则加入这个事务，成为一个整体。   举例：领导没饭吃，我有钱，我会自己买了自己吃；领导有的吃，会分给你一起吃。 |
| ------------- | ---------------------------------------- |
| SUPPORTS      | 如果当前有事务，则使用事务；如果当前么有事务，则不使用事务。  举例：领导没饭吃，我也没饭吃，领导有饭吃，我也有饭吃。 |
| MANDATORY     | 该传播属性强制必须存在一个事务，如果不存在，则抛出异常。  举例： 领导必须管饭，不管饭没饭吃，我就不乐意，就不干了（抛出异常）。 |
|               | 如果当前有事务，则挂起事务，并且自己创建一个新的事务给自己使用；如果当前没有事务，则同REQUIRED .。 举例： 领导有饭吃，我偏不要，我自己买了自己吃。 |
| NOT_SUPPORTED | 如果当前有事务，则把事务挂起，自己不使用事务去运行数据操作。 举例：领导有饭吃，分一点给你，我太忙了，放一边，我不吃。 |
| NEVER         | 如果当前有事务存在，则抛出异常，举例：领导有饭给你吃，我不想吃，我热爱工作，我抛出异常。 |
| NESTED        | 如果当前有事务，则开启子事务（嵌套事务），嵌套事务是独立提交或者回滚； 如果当前没有事务，则同 REQUIRED。 但是如果主事务提交，则会携带子事务一起提交。如果主事务回滚，则子事务会一起回滚，相反，子事务回滚，则父事务可以回滚或不回滚。  举例：领导决策不对，老板怪罪，领导带着小弟一同受罪。小弟出了差错，领导可以推卸责任。 |
|               |                                          |

# 2、SpringBoot 单体继承Swagger2

## ① 导入依赖

```java
<!-- swagger2 配置 -->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>2.4.0</version>
        </dependency>
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>2.4.0</version>
        </dependency>
        <dependency>
            <groupId>com.github.xiaoymin</groupId>
            <artifactId>swagger-bootstrap-ui</artifactId>
            <version>1.6</version>
        </dependency>
```

## ② 配置文件

```java
@Configuration
@EnableSwagger2
public class Swagger2 {

//    http://localhost:8088/swagger-ui.html     原路径
//    http://localhost:8088/doc.html     原路径

    // 配置swagger2核心配置 docket
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)  // 指定api类型为swagger2
                    .apiInfo(apiInfo())                 // 用于定义api文档汇总信息
                    .select()
                    .apis(RequestHandlerSelectors
                            .basePackage("com.hyc.controller"))   // 指定controller包
                    .paths(PathSelectors.any())         // 所有controller
                    .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("天天吃货 电商平台接口api")        // 文档页标题
                .contact(new Contact("hyc",
                        "https://www.yachaozz.cn",
                        "814428354@qq.com"))        // 联系人信息
                .description("专为天天吃货提供的api文档")  // 详细信息
                .version("1.0.1")   // 文档版本号
                .termsOfServiceUrl("https://www.yachaozz.cn") // 网站地址
                .build();
    }

}
```

## ③ 具体使用

```java
@Api(value = "用户注册相关接口", tags = {"用于用户注册的接口"})
@RestController
@RequestMapping("passport")
public class PassportController {

    @Autowired
    private UserService userService;


    /**
     * 判断该用户名是否存在
     * @param username
     * @return
     */
    @ApiOperation(value = "判断用户名是否存在", notes = "判断用户名是否存在", httpMethod = "GET")
    @GetMapping("/usernameIsExist")
    public JSONResult usernameIsExist(@RequestParam String username) {

        if (StringUtils.isBlank(username)) {
            return JSONResult.errorMsg("用户名不能为空");
        }

        boolean result = userService.usernameIsExist(username);

        if (result) {
            return JSONResult.errorMsg("用户名已存在");
        }

        return JSONResult.ok();
    }
}


/**
 * @author HouYC
 * @create 2020-11-21-11:44
 */
@ApiModel(value = "用户对象BO", description = "从客户端，由用户传入的数据封装在此entity")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBo {

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名", name = "username", example = "houyachao", required = true)
    private String username;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码", name = "password", example = "123456", required = true)
    private String password;

    /**
     * 确认密码
     */
    @ApiModelProperty(value = "确认密码", name = "confirmPassword", example = "123456", required = true)
    private String confirmPassword;
}
```

## ④访问

http://localhost:8088/doc.html

# 3、SpringBoot 单体解决跨域

```java
/**
 * @author houyachao
 *
 * 解决跨域
 */
@Configuration
public class CorsConfig {

    public CorsConfig() {
    }

    @Bean
    public CorsFilter corsFilter() {
        // 1. 添加cors配置信息
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:8080");
        config.addAllowedOrigin("http://shop.z.mukewang.com:8080");
        config.addAllowedOrigin("http://center.z.mukewang.com:8080");
        config.addAllowedOrigin("http://shop.z.mukewang.com");
        config.addAllowedOrigin("http://center.z.mukewang.com");
        config.addAllowedOrigin("*");

        // 设置是否发送cookie信息
        config.setAllowCredentials(true);

        // 设置允许请求的方式
        config.addAllowedMethod("*");

        // 设置允许的header
        config.addAllowedHeader("*");

        // 2. 为url添加映射路径
        UrlBasedCorsConfigurationSource corsSource = new UrlBasedCorsConfigurationSource();
        corsSource.registerCorsConfiguration("/**", config);

        // 3. 返回重新定义好的corsSource
        return new CorsFilter(corsSource);
    }
}
```

# 4、SpringBoot 整合 log4j

## ① 踢出 SpringBoot 自带的日志框架

```java
 <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-logging</artifactId>
                </exclusion>
            </exclusions>
  </dependency>
```

## ② 加入log4j 依赖

```java
<!--引入日志依赖 抽象层 与 实现层-->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.7.21</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
            <version>1.7.21</version>
        </dependency>
```

## ③ 日志配置文件

```properties
log4j.properties----文件名

log4j.rootLogger=DEBUG,stdout,file
log4j.additivity.org.apache=true

log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.threshold=INFO
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%-5p %c{1}:%L - %m%n

log4j.appender.file=org.apache.log4j.DailyRollingFileAppender
log4j.appender.file.layout=org.apache.log4j.PatternLayout
log4j.appender.file.DatePattern='.'yyyy-MM-dd-HH-mm
log4j.appender.file.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n
log4j.appender.file.Threshold=INFO
log4j.appender.file.append=true
log4j.appender.file.File=/Idea_WorkSpace/foodie-dev/foodie-dev-api/foodie-dev.log
```

# 5、SpringBoot单体 整合切面监控SQL执行时间

## ① 导入依赖

```properties
<!--Spring AOP 切面 模块 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aop</artifactId>
        </dependency>
        <!-- SpringBoot 拦截器 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
        </dependency>
```

## ② 配置文件

```java

/**
 * @author HYC
 * 获取每个SQL 执行的时间，并在执行时候打印
 */
@Aspect
@Component
public class ServiceLogAspect {

    private static final Logger log =
            LoggerFactory.getLogger(ServiceLogAspect.class);

    /**
     * AOP通知：
     * 1. 前置通知：在方法调用之前执行
     * 2. 后置通知：在方法正常调用之后执行
     * 3. 环绕通知：在方法调用之前和之后，都分别可以执行的通知
     * 4. 异常通知：如果在方法调用过程中发生异常，则通知
     * 5. 最终通知：在方法调用之后执行
     */

    /**
     * 切面表达式：
     * execution 代表所要执行的表达式主体
     * 第一处 * 代表方法返回类型 *代表所有类型
     * 第二处 包名代表aop监控的类所在的包
     * 第三处 .. 代表该包以及其子包下的所有类方法
     * 第四处 * 代表类名，*代表所有类
     * 第五处 *(..) *代表类中的方法名，(..)表示方法中的任何参数
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.hyc.service.impl..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {

        log.info("====== 开始执行 {}.{} ======",
                        joinPoint.getTarget().getClass(),
                        joinPoint.getSignature().getName());

        // 记录开始时间
        long begin = System.currentTimeMillis();

        // 执行目标 service
        Object result = joinPoint.proceed();

        // 记录结束时间
        long end = System.currentTimeMillis();
        long takeTime = end - begin;

        if (takeTime > 3000) {
            log.error("====== 执行结束，耗时：{} 毫秒 ======", takeTime);
        } else if (takeTime > 2000) {
            log.warn("====== 执行结束，耗时：{} 毫秒 ======", takeTime);
        } else {
            log.info("====== 执行结束，耗时：{} 毫秒 ======", takeTime);
        }
        return result;
    }
}
```

# 6、SpringBoot 整合mybatisPageHelper 分页

## ① 导入依赖

```java
<!--pagehelper -->
        <dependency>
            <groupId>com.github.pagehelper</groupId>
            <artifactId>pagehelper-spring-boot-starter</artifactId>
            <version>1.2.12</version>
        </dependency>
```

## ② 配置文件

```yaml

# 分页插件配置
pagehelper:
  helperDialect: mysql
  supportMethodsArguments: true

```

## ③ 封装共有类

```java
/**
 * 
 * @Title: PagedGridResult.java
 * @Package 
 * @Description: 用来返回分页Grid的数据格式
 * Copyright: Copyright (c) 2020
 */
@Data
public class PagedGridResult {

	/**
	 * 当前页数
	 */
	private int page;
	/**
	 * 总页数
	 */
	private int total;
	/**
	 * 总记录数
	 */
	private long records;
	/**
	 * 每行显示的内容
	 */
	private List<?> rows;

	/**
	 * 封装数据
	 * @param list
	 * @return
	 */
	public PagedGridResult setterPagedGrid(List<?> list) {
		PageInfo<?> pageList = new PageInfo<>(list);
		PagedGridResult grid = new PagedGridResult();
		grid.setPage(page);
		grid.setRows(list);
		grid.setTotal(pageList.getPages());
		grid.setRecords(pageList.getTotal());
		return grid;
	}
}
```

## ④ 使用

**使用分页插件，在查询前使用分页插件，原理：统一拦截sql，为其提供分页功能**

```java
/**
* page: 第几页
* pageSize: 每页显示条数
*/
PageHelper.startPage(page, pageSize);
 
 // SQL 查询

 return new PagedGridResult().setterPagedGrid(查询结果集); 
```

# 7、脱敏工具类

```java

/**
 * 通用脱敏工具类
 * 可用于：
 *      用户名
 *      手机号
 *      邮箱
 *      地址等
 * @author HYC     
 */
public class DesensitizationUtil {

    private static final int SIZE = 6;
    private static final String SYMBOL = "*";

    public static void main(String[] args) {
        String name = commonDisplay("侯亚超");
        String mobile = commonDisplay("18738976845");
        String mail = commonDisplay("admin@qq.com");
        String address = commonDisplay("上海市浦东新区高兴镇思学路165弄");

        System.out.println(name);
        System.out.println(mobile);
        System.out.println(mail);
        System.out.println(address);
    }

    /**
     * 通用脱敏方法
     * @param value
     * @return
     */
    public static String commonDisplay(String value) {
        if (null == value || "".equals(value)) {
            return value;
        }
        int len = value.length();
        int pamaone = len / 2;
        int pamatwo = pamaone - 1;
        int pamathree = len % 2;
        StringBuilder stringBuilder = new StringBuilder();
        if (len <= 2) {
            if (pamathree == 1) {
                return SYMBOL;
            }
            stringBuilder.append(SYMBOL);
            stringBuilder.append(value.charAt(len - 1));
        } else {
            if (pamatwo <= 0) {
                stringBuilder.append(value.substring(0, 1));
                stringBuilder.append(SYMBOL);
                stringBuilder.append(value.substring(len - 1, len));

            } else if (pamatwo >= SIZE / 2 && SIZE + 1 != len) {
                int pamafive = (len - SIZE) / 2;
                stringBuilder.append(value.substring(0, pamafive));
                for (int i = 0; i < SIZE; i++) {
                    stringBuilder.append(SYMBOL);
                }
                if ((pamathree == 0 && SIZE / 2 == 0) || (pamathree != 0 && SIZE % 2 != 0)) {
                    stringBuilder.append(value.substring(len - pamafive, len));
                } else {
                    stringBuilder.append(value.substring(len - (pamafive + 1), len));
                }
            } else {
                int pamafour = len - 2;
                stringBuilder.append(value.substring(0, 1));
                for (int i = 0; i < pamafour; i++) {
                    stringBuilder.append(SYMBOL);
                }
                stringBuilder.append(value.substring(len - 1, len));
            }
        }
        return stringBuilder.toString();
    }

}


输出结果：

侯*超
18******845
adm******.com
上海市浦东******学路165弄
```

# 8、购物车功能

## ① 购物车存储形式 -cookie

-  无须登录，无须查库，保存在浏览器端。
- 优点：性能好，访问快，没有和数据库交互。
- 缺点1：换电脑购物车数据会丢失。
- 缺点2：电脑被其他人登录，隐私安全。
- 

## ② 购物车存储形式 - Session

- 用户登录后，购物车数据放入用户会话。
- 优点：初期性能好，访问快。
- 缺点1：Session 基于内存，用户量庞大影响服务器性能。
- 缺点2： 只能存在与当前会话，不适应集群与分布式系统。

## ③ 购物车存储形式 - 数据库

- 用户登录后，购物车数据存入数据库。
- 优点：数据持久化，可在任何地点任何时间访问。
- 缺点：频繁读写数据库，造成数据库压力。 

## ④ 购物车存储形式 - Redis

- 用户登录后，	购物车数据写入Redis缓存
- 优点1 ： 数据持久化，可在任何地点任何时间访问。
- 优点2 ： 频繁读写只基于缓存，不会造成数据库压力。
- 优点3 ： 适用于集群与分布式系统，可扩展性强。

# 9、复杂订单状态

![3.复杂订单状态流程2](C:\Users\Administrator\Desktop\笔记\Java直通车项目\3.复杂订单状态流程2.png)



# 10、项目部署

## ① 大坑

​	1、先将项目部署到服务器上，需要先在阿里云后台 对应的**安全组**中将 对应的端口开放。

​	2、在命令行中 **防火墙** 将对应的 端口开放。

```java
1、登录阿里云服务器，输入firewall-cmd --list-ports命令来查看当前已经开放的端口

2、使用firewall-cmd --zone=public --add-port=8080/tcp --permanent开放8080端口，其中–permanent的作用是使设置永久生效，不加的话机器重启之后失效。

3、添加成功后需要用firewall-cmd --reload 命令重载一下firewall服务才能生效
```

## ② 后端项目部署到Tomcat下，会报 cookie 不兼容问题

需要在Tomcat\conf 目录下的context.xml 文件添加如下配置：

```java
<CookieProcessor className="org.apache.tomcat.util.http.LegacyCookieProessor" />s
```

## ③ Nginx 在启动的时候会遇到两个问题

```
1、 ./nginx -s reload
error: "/var/run/nginx/nginx.pid" failed (2: Nosh file or directory)    即没有这个目录

解决方式： 我们创建这个目录就可以了。   mkdir /var/run/nginx

2、./nginx -s reload 
error ： invalid PID number "" in "/var/run/nginx/nginx.pid"  即无效的pid、

          可以使用 ./nginx -h  帮助命令查询相关参数。
解决方式： ./nginx -c /usr/local/nginx/conf/nginx.conf
		  ./nginx -s reload
```

## ④ 静态资源部署到 Nginx下





## ⑤ Nginx 配置Tomcat集群

```
server {
  listen   80;
  server_name www.hyc.com;
  
  location / {
    	proxy_pass 	http://tomcats;、
    	
    	expires 10s;     # 设置缓存过期时间 10s, 浏览器缓存。
  }
}

# 配置上游服务器集群
upstream tomcats {
  server 192.168.1.173:8080;
  server 192.168.1.174:8080;
  server 192.168.1.175:8080;
  
  keepalive 32;  // 保持连接数， 相当于长连接，可增加吞吐量
 
}
```

# 11、redis

## 1、redis 缓存过期与内存淘汰机制

### ① 已过期的key 如何处理？

​	设置了expire 的key 缓存过期了，但是服务器的内存还是会被占用，这是因为redis 所基于的两种删除策略。

redis 有两种策略：

​	1、（主动）定时删除：  定时随机的检查过期的key，如果过期则清理删除。（每秒检查次数在redis.conf 中的hz 配置）。

​	2、 （被动）惰性删除：当客户端请求一个已经过期的key 的时候，那么redis 会检查这个key 是否过期，如果过期了，则删除，然后返回一个nil， 这种策略对CPU 比较友好，不会有太多的损耗，但是内存占用会比较高。

所以，虽然key 过期了，但是只要么没有被redis 清理，那么其实内存还是被占用着的。

### ② 如果内存被redis 缓存占用满了咋办？

​	内存沾满了，可以使用硬盘，来保存，但是没有太大意义，因为硬盘没有内存块，会影响redis性能。

​	所以，当内存满了以后，redis 提供了一套缓存淘汰机制：MEMORY  MANAGEMENT。

**maxmemory ：当内存已使用率到达，则开始清理缓存。**

```nginx
noeviction:  旧缓存永不过期，新缓存设置不了，返回错误。
allkeys-lru : 清除最少用的旧缓存，然后保存新的缓存（推荐使用）
allkeys-random：在所有的缓存中随机删除（不推荐）
volatile-lru: 在那些设置了expire 过期时间的缓存中，随机删除缓存
Volatile-ttl: 在那些设置了expire 过期时间的缓存中，删除即将过期的。

```

## 2、redis 哨兵模式

​	master 挂了，如何保证可用性，实现继续读写。

### ① 什么是哨兵

​	Sentinel （哨兵）是用于监控redis集群中master状态的工具，是redis 高可用解决方案，哨兵可以监视一个或者多个redis master 服务，以及这些master 服务的所有从服务；当某个master 服务宕机后，会把这个master 下的某个从服务升级为 master来替代已宕机的master 继续工作。

### ② 配置哨兵监控 master

创建并且配置 sentinel.conf

```nginx
# 1、 普通配置
port 26379
pidfile "/usr/local/redis/sentinel/redis-sentinel.pid"
dir "/usr/local/redis/sentinel"
daemonize yes
protected-mode no
logfile "/usr/local/redis/sentinel/redis-sentinel.log"

# 2、核心配置
# 配置哨兵
sentinel monitor mymaster 127.0.0.1 6379 2
# 密码
sentinel auth-pass <master-name> <password>
#master 被sentinel 认定失效的时间间隔
sentinel down-afer-milliseconds mymaster 30000
# 剩余的slaves 重新和新的master 做同步的并行个数
sentinel parallel-syncs mymaster 1
# 主备切换的超时时间，哨兵要去做故障转移，这个时候哨兵也是一个进程，如果他没有去执行，超过这个时间后，会由其他的哨兵来处理
sentinel failover-timeout mymaster 1800000
```

3、启动哨兵

```
redis-sentinel sentinel.conf
```

4、查看 my-master 下的master节点信息

```
sentinel master my-master
```

5、查看 my-master 下的slaves 节点信息

```
sentinel slaves my-master
```

6、查看my-master 下的哨兵节点信息

```
sentinel sentinels my-master
```

### ③ 配置详解

https://blog.csdn.net/miss1181248983/article/details/90056960?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-8.control&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-8.control

### ④ 集成SpringBoot

```properties
![8.cas 单点登录](C:\Users\Administrator\Desktop\笔记\Java直通车项目\8.cas 单点登录.png)
spring:
  datasource:                                           # 数据源的相关配置
    url: jdbc:mysql://localhost:3306/foodie-shop-dev?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true
    password: 123456
  redis:

  # Redis 单机单实例
    database: 0
    host: 39.100.90.93
    port: 6379
    #password: 456654

    # redis 哨兵模式     以上二选一
    #database: 1
    #password: 456654
    #sentinel:
    #  master: my-master   # 是哨兵配置文件配置的名字
    #  nodes: 192.168.1.191:26379,192.168.1.192:26379,192.168.1.193:26379

    # redis 集群模式
    #password: 456654
    #cluster:
     # nodes: 192.168.1.201:6379,192.168.1.202:6379,192.168.1.203:6379,192.168.1.204:6379,192.168.1.205:6379,192.168.1.206:6379,

```

# 12、 CAS 单点登录

https://blog.csdn.net/ban_tang/article/details/80015946

![8.cas 单点登录](C:\Users\Administrator\Desktop\笔记\Java直通车项目\8.cas 单点登录.png)

如果是同级域名，域名都相同的话，可以使用 cookie+redis 来实现单点登录。



针对不同域名下的单点登录，可以使用cas.

![8.不同域名下的单点登录](C:\Users\Administrator\Desktop\笔记\Java直通车项目\8.不同域名下的单点登录.png)

![8.不同域名下的单点登录2](C:\Users\Administrator\Desktop\笔记\Java直通车项目\8.不同域名下的单点登录2.png)

主要代码 见 foodie-sso模块。 里面注释详解

# 13、RabbitMQ

## ① 分布消息队列（MQ）应用场景

### 1、MQ应用场景

#### ①、服务解构

​	分布式服务下，服务之间依赖不是太强，可以使用消息中间件来进行解耦。

#### ②、削峰填谷

​	比如双11，商品促销，可能系统压力很大，可以将商品放入到消息中间件中进行缓冲，让系统进行调取。

#### ③、异步化缓冲

​	将数据放入到消息中间件中，进行异步缓冲，最终保证数据的最终一致性。

### 2、使用MQ 需要考虑的问题

#### ① 生产端可靠性投递

#### ② 消费端幂等

#### ③ 高可用：

#### ④ 低延迟：kafka

#### ⑤ 可靠性：

#### ⑥ 堆积压力：

#### ⑦ 扩展性

### 3、集群模式

#### ① 主备模式

​	一个主/备 （主节点如果挂了，从节点提供服务，和activeMQ利用Zookeeper做主、备一样）。

![9.RabbitMQ 主备模式1](C:\Users\Administrator\Desktop\笔记\Java直通车项目\9.RabbitMQ 主备模式1.png)

#### ② 镜像模式

​	集群模式非常经典的mirror 镜像模式，保证100% 数据不丢失。

![10.RabbitMQ 镜像模式1](C:\Users\Administrator\Desktop\笔记\Java直通车项目\10.RabbitMQ 镜像模式1.png)

#### ③ 多活模式

​	实现异地数据复制的主流模式，因为Shovel 模式配置比较复杂，所以一般来说实现异地集群都是使用这种  双活 或者多活模式来实现的。

​	这种模式需要依赖RabbitMQ 的 federation 插件，可以实现持续的可靠的AMQP数据通信，多活模式实际配置与应用非常简单。

​	RabbitMQ 部署架构采用双中心模式（多中心），那么在两套（或多套）数据中心各部署一套RabbitMQ集群，各中心的RabbitMQ 服务除了需要为业务提供正常的消息服务外，中心之间还需要实现部分队列消息共享。

![11.RabbitMQ多活模式集群](C:\Users\Administrator\Desktop\笔记\Java直通车项目\11.RabbitMQ多活模式集群.png)

​	Federation 插件是一个不需要构建Cluster ，而在Brokers 之间传输消息的高性能插件，Federation 插件可以在Brokers 或者 Cluster 之间传输消息，链接的双方可以使用不同的users 和 virtual hosts，双方也可以使用版本不同的 RabbitMQ 和 Erlang。Federation 插件使用 AMQP协议通信，可以接受不连续的传输。

​	Federation Exchanges ，可以看成 Downstream 从 Upstream 主动拉去消息，但并不是拉去所有消息，必须是在Downstream 上已经明确定义Bindings 关系的Exchange，也就是有实际的物理 Queue 来接受消息，才会从 Upstream 拉取消息到Downstream。使用AMQP 协议实施代理间通信，Downstream 会将绑定关系组合在一起，绑定\解除绑定命令将发送到Upstream 交换机。因此，Federation Exchange 只接收具有订阅的消息。

![11.RabbitMQ 多活模式 federation 插件](C:\Users\Administrator\Desktop\笔记\Java直通车项目\11.RabbitMQ 多活模式 federation 插件.png)

### 4、可靠性投递

![14、RabbitMQ 可靠性投递](C:\Users\Administrator\Desktop\笔记\Java直通车项目\14、RabbitMQ 可靠性投递.png)

```java
// 代码   ---- 详细思路 请看foodie-dev 项目
-- 表 broker_message.broker_message 结构
CREATE TABLE IF NOT EXISTS `broker_message` (
  `message_id` varchar(128) NOT NULL,
  `message` varchar(4000),
  `try_count` int(4) DEFAULT 0,                                   -- 重试次数
  `status` varchar(10) DEFAULT '',                                -- 确认状态 0-未确认 1-已确认 2-已失败
  `next_retry` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',  -- 下一次的确认时间
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `update_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


// 我们使用自定义注解的方式来处理

/**
 *  自定义注解，  参数属性信息 可参考文档：https://shardingsphere.apache.org/elasticjob/current/cn/features/
 * @author HYC
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ElasticJobConfig {

	/**
	 * elasticjob的名称
	 * @return
	 */
	String name();
	
	String cron() default "";
	
	int shardingTotalCount() default 1;
	
	String shardingItemParameters() default "";
	
	String jobParameter() default "";
	
	boolean failover() default false;
	
	boolean misfire() default true;
	
	String description() default "";
	
	boolean overwrite() default false;
	
	boolean streamingProcess() default false;
	
	String scriptCommandLine() default "";
	
	boolean monitorExecution() default false;
	
	public int monitorPort() default -1;	//must

	public int maxTimeDiffSeconds() default -1;	//must

	public String jobShardingStrategyClass() default "";	//must

	public int reconcileIntervalMinutes() default 10;	//must

	public String eventTraceRdbDataSource() default "";	//must

	public String listener() default "";	//must

	public boolean disabled() default false;	//must

	public String distributedListener() default "";

	public long startedTimeoutMilliseconds() default Long.MAX_VALUE;	//must

	public long completedTimeoutMilliseconds() default Long.MAX_VALUE;		//must

	public String jobExceptionHandler() default "com.dangdang.ddframe.job.executor.handler.impl.DefaultJobExceptionHandler";

	public String executorServiceHandler() default "com.dangdang.ddframe.job.executor.handler.impl.DefaultExecutorServiceHandler";
}

/**
 * 自定义注解，开启ElasticJob
 * @author hyc
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(JobParserAutoConfigurartion.class)
public @interface EnableElasticJob {

}

/**
 * 配置zookeeper注册中心信息
 *
 * @author HYC
 * ConditionalOnProperty： 如果 前缀符合prefix 和 name，这将会执行下面的信息，否则不加载
 * EnableConfigurationProperties： 加载JobZookeeperProperties中数据
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "elastic.job.zk", name = {"namespace", "serverLists"}, matchIfMissing = false)
@EnableConfigurationProperties(JobZookeeperProperties.class)
public class JobParserAutoConfigurartion {

	@Bean(initMethod = "init")
	public ZookeeperRegistryCenter zookeeperRegistryCenter(JobZookeeperProperties jobZookeeperProperties) {
		ZookeeperConfiguration zkConfig = new ZookeeperConfiguration(jobZookeeperProperties.getServerLists(),
				jobZookeeperProperties.getNamespace());
		zkConfig.setBaseSleepTimeMilliseconds(jobZookeeperProperties.getBaseSleepTimeMilliseconds());
		zkConfig.setMaxSleepTimeMilliseconds(jobZookeeperProperties.getMaxSleepTimeMilliseconds());
		zkConfig.setConnectionTimeoutMilliseconds(jobZookeeperProperties.getConnectionTimeoutMilliseconds());
		zkConfig.setSessionTimeoutMilliseconds(jobZookeeperProperties.getSessionTimeoutMilliseconds());
		zkConfig.setMaxRetries(jobZookeeperProperties.getMaxRetries());
		zkConfig.setDigest(jobZookeeperProperties.getDigest());
		log.info("初始化job注册中心配置成功, zkaddress : {}, namespace : {}", jobZookeeperProperties.getServerLists(), jobZookeeperProperties.getNamespace());
		return new ZookeeperRegistryCenter(zkConfig);
	}

	/**
	 * 初始化ElasticJobConfParser，使用构造器初始化
	 * @param jobZookeeperProperties
	 * @param zookeeperRegistryCenter
	 * @return
	 */
	@Bean
	public ElasticJobConfParser elasticJobConfParser(JobZookeeperProperties jobZookeeperProperties, ZookeeperRegistryCenter zookeeperRegistryCenter) {
		return new ElasticJobConfParser(jobZookeeperProperties, zookeeperRegistryCenter);
	}
	
}

/**
 *  配置类信息
 * @author hyc
 */
@ConfigurationProperties(prefix = "elastic.job.zk")
@Data
public class JobZookeeperProperties {

	private String namespace;
	
	private String serverLists;
	
	private int maxRetries = 3;

	private int connectionTimeoutMilliseconds = 15000;
	
	private int sessionTimeoutMilliseconds = 60000;
	
	private int baseSleepTimeMilliseconds = 1000;
	
	private int maxSleepTimeMilliseconds = 3000;
	
	private String digest = "";
	
}

public enum ElasticJobTypeEnum {

	SIMPLE("SimpleJob", "简单类型job"),
	DATAFLOW("DataflowJob", "流式类型job"),
	SCRIPT("ScriptJob", "脚本类型job");
	
	private String type;
	
	private String desc;
	
	private ElasticJobTypeEnum(String type, String desc) {
		this.type = type;
		this.desc = desc;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}


/**
 *  在所有bean 都加载完后，自动配置我们的信息
 * @author hyc
 */
@Slf4j
public class ElasticJobConfParser implements ApplicationListener<ApplicationReadyEvent> {

	private JobZookeeperProperties jobZookeeperProperties;
	
	private ZookeeperRegistryCenter zookeeperRegistryCenter;

    /**
     * 这里使用构造器注入
     * @param jobZookeeperProperties
     * @param zookeeperRegistryCenter
     */
	public ElasticJobConfParser(JobZookeeperProperties jobZookeeperProperties,
			ZookeeperRegistryCenter zookeeperRegistryCenter) {
		this.jobZookeeperProperties = jobZookeeperProperties;
		this.zookeeperRegistryCenter = zookeeperRegistryCenter;
	}

    /**
     * 主要添加逻辑
     * @param event
     */
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		try {
		    // 获取上下文
			ApplicationContext applicationContext = event.getApplicationContext();
			// 获取到类上加 ElasticJobConfig 注解的
			Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(ElasticJobConfig.class);
			for(Iterator<?> it = beanMap.values().iterator(); it.hasNext();) {
				Object confBean = it.next();
				Class<?> clazz = confBean.getClass();
				if(clazz.getName().indexOf("$") > 0) {
					String className = clazz.getName();
					clazz = Class.forName(className.substring(0, className.indexOf("$")));
				}
				// 	获取接口类型 用于判断是什么类型的任务
				String jobTypeName = clazz.getInterfaces()[0].getSimpleName();
				//	获取配置项 ElasticJobConfig
				ElasticJobConfig conf = clazz.getAnnotation(ElasticJobConfig.class);
				
				String jobClass = clazz.getName();
				String jobName = this.jobZookeeperProperties.getNamespace() + "." + conf.name();
				String cron = conf.cron();
				String shardingItemParameters = conf.shardingItemParameters();
				String description = conf.description();
				String jobParameter = conf.jobParameter();
				String jobExceptionHandler = conf.jobExceptionHandler();
				String executorServiceHandler = conf.executorServiceHandler();

				String jobShardingStrategyClass = conf.jobShardingStrategyClass();
				String eventTraceRdbDataSource = conf.eventTraceRdbDataSource();
				String scriptCommandLine = conf.scriptCommandLine();

				boolean failover = conf.failover();
				boolean misfire = conf.misfire();
				boolean overwrite = conf.overwrite();
				boolean disabled = conf.disabled();
				boolean monitorExecution = conf.monitorExecution();
				boolean streamingProcess = conf.streamingProcess();

				int shardingTotalCount = conf.shardingTotalCount();
				int monitorPort = conf.monitorPort();
				int maxTimeDiffSeconds = conf.maxTimeDiffSeconds();
				int reconcileIntervalMinutes = conf.reconcileIntervalMinutes();				
				
				//	先把当当网的esjob的相关configuration
				JobCoreConfiguration coreConfig = JobCoreConfiguration
						.newBuilder(jobName, cron, shardingTotalCount)
						.shardingItemParameters(shardingItemParameters)
						.description(description)
						.failover(failover)
						.jobParameter(jobParameter)
						.misfire(misfire)
						.jobProperties(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(), jobExceptionHandler)
						.jobProperties(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER.getKey(), executorServiceHandler)
						.build();
				
				//	要创建什么样的任务.
				JobTypeConfiguration typeConfig = null;
				if(ElasticJobTypeEnum.SIMPLE.getType().equals(jobTypeName)) {
				    // 简单
					typeConfig = new SimpleJobConfiguration(coreConfig, jobClass);
				}
				
				if(ElasticJobTypeEnum.DATAFLOW.getType().equals(jobTypeName)) {
				    // 流式
					typeConfig = new DataflowJobConfiguration(coreConfig, jobClass, streamingProcess);
				}
				
				if(ElasticJobTypeEnum.SCRIPT.getType().equals(jobTypeName)) {
				    // 脚本
					typeConfig = new ScriptJobConfiguration(coreConfig, scriptCommandLine);
				}
				
				// LiteJobConfiguration
				LiteJobConfiguration jobConfig = LiteJobConfiguration
						.newBuilder(typeConfig)
						.overwrite(overwrite)
						.disabled(disabled)
						.monitorPort(monitorPort)
						.monitorExecution(monitorExecution)
						.maxTimeDiffSeconds(maxTimeDiffSeconds)
						.jobShardingStrategyClass(jobShardingStrategyClass)
						.reconcileIntervalMinutes(reconcileIntervalMinutes)
						.build();
				
				// 	创建一个Spring的beanDefinition
				BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(SpringJobScheduler.class);
				factory.setInitMethodName("init");
				factory.setScope("prototype");
				
				//	1.添加bean构造参数，相当于添加自己的真实的任务实现类
				if (!ElasticJobTypeEnum.SCRIPT.getType().equals(jobTypeName)) {
					factory.addConstructorArgValue(confBean);
				}
				//	2.添加注册中心
				factory.addConstructorArgValue(this.zookeeperRegistryCenter);
				//	3.添加LiteJobConfiguration
				factory.addConstructorArgValue(jobConfig);

				//	4.如果有eventTraceRdbDataSource 则也进行添加
				if (StringUtils.hasText(eventTraceRdbDataSource)) {
					BeanDefinitionBuilder rdbFactory = BeanDefinitionBuilder.rootBeanDefinition(JobEventRdbConfiguration.class);
					rdbFactory.addConstructorArgReference(eventTraceRdbDataSource);
					factory.addConstructorArgValue(rdbFactory.getBeanDefinition());
				}
				
				//  5.添加监听
				List<?> elasticJobListeners = getTargetElasticJobListeners(conf);
				factory.addConstructorArgValue(elasticJobListeners);
				
				// 	接下来就是把factory 也就是 SpringJobScheduler注入到Spring容器中
				DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

				String registerBeanName = conf.name() + "SpringJobScheduler";
				defaultListableBeanFactory.registerBeanDefinition(registerBeanName, factory.getBeanDefinition());
				SpringJobScheduler scheduler = (SpringJobScheduler)applicationContext.getBean(registerBeanName);
				scheduler.init();
				log.info("启动elastic-job作业: " + jobName);
			}
			log.info("共计启动elastic-job作业数量为: {} 个", beanMap.values().size());
			
		} catch (Exception e) {
			log.error("elasticjob 启动异常, 系统强制退出", e);
			System.exit(1);
		}
	}

	/**
	 * 添加监听
	 *
	 * @param conf
	 * @return
	 */
	private List<BeanDefinition> getTargetElasticJobListeners(ElasticJobConfig conf) {
		List<BeanDefinition> result = new ManagedList<BeanDefinition>(2);
		String listeners = conf.listener();
		if (StringUtils.hasText(listeners)) {
			BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(listeners);
			factory.setScope("prototype");
			result.add(factory.getBeanDefinition());
		}

		String distributedListeners = conf.distributedListener();
		long startedTimeoutMilliseconds = conf.startedTimeoutMilliseconds();
		long completedTimeoutMilliseconds = conf.completedTimeoutMilliseconds();

		if (StringUtils.hasText(distributedListeners)) {
			BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(distributedListeners);
			factory.setScope("prototype");
			factory.addConstructorArgValue(Long.valueOf(startedTimeoutMilliseconds));
			factory.addConstructorArgValue(Long.valueOf(completedTimeoutMilliseconds));
			result.add(factory.getBeanDefinition());
		}
		return result;
	}
}


@Component
@ElasticJobConfig(
		name= "com.hyc.rabbit.product.task.RetryMessageDataflowJob",
		cron= "0/10 * * * * ?",
		description = "可靠性投递消息补偿任务",
		overwrite = true,
		shardingTotalCount = 1
		)
@Slf4j
public class RetryMessageDataflowJob implements DataflowJob<BrokerMessage>{

	@Autowired
	private MessageStoreService messageStoreService;
	
	@Autowired
	private RabbitBroker rabbitBroker;
	
	private static final int MAX_RETRY_COUNT = 3;

	/**
	 * 在指定的时间内 收集数据
	 * @param shardingContext
	 * @return
	 */
	@Override
	public List<BrokerMessage> fetchData(ShardingContext shardingContext) {
		List<BrokerMessage> list = messageStoreService.fetchTimeOutMessage4Retry(BrokerMessageStatus.SENDING);
		log.info("--------@@@@@ 抓取数据集合, 数量：	{} 	@@@@@@-----------" , list.size());
		return list;
	}

	/**
	 * 通过上面收集到数据进行处理
	 * @param shardingContext
	 * @param dataList
	 */
	@Override
	public void processData(ShardingContext shardingContext, List<BrokerMessage> dataList) {
		
		dataList.forEach( brokerMessage -> {
			
			String messageId = brokerMessage.getMessageId();
			// 如果重试次数大于等于3次，将这条消息标记为失败
			if(brokerMessage.getTryCount() >= MAX_RETRY_COUNT) {
				this.messageStoreService.failure(messageId);
				log.warn(" -----消息设置为最终失败，消息ID: {} -------", messageId);
			} else {
				//	每次重发的时候要更新一下try count字段
				this.messageStoreService.updateTryCount(messageId);
				// 	重发消息
				this.rabbitBroker.reliantSend(brokerMessage.getMessage());
			}
			
		});
	}
}

@EnableElasticJob
@Configuration
@ComponentScan({"com.hyc.rabbit.product.*"})
public class RabbitProducerAutoConfiguration {
}

```



# 14、KafKa

### ① kafka 有哪些特点

#### 1、 跨平台

#### 2、分布式

#### 3、伸缩性

#### 4、实时性

### ② kafka 高性能的原因

1、顺序写，Page Cache 空中接力，高效读写

![12.kafka 零拷贝](C:\Users\Administrator\Desktop\笔记\Java直通车项目\12.kafka 零拷贝.png)

2、 后台异步，主动Flush

3、 高性能，高吞吐

4、 预读策略，IO调度

### ③ kafka集群模式

![13.kafka及期末数](C:\Users\Administrator\Desktop\笔记\Java直通车项目\13.kafka及期末数.png)



# 15、接口幂等性

## ① 接口幂等性

​	**幂等性的核心思想：通过唯一的业务单号保证幂等性。**

​	并发情况下，通过加锁。

​	非并发情况下，查询业务单号有没有操作过，没有执行操作。

# 16、限流

## ① 基于 guava 限流

​	令牌桶算法(Token Bucket)和 Leaky Bucket 效果一样但方向相反的算法,更加容易理解.随着时间流逝,系统会按恒定1/QPS时间间隔(如果QPS=100,则间隔是10ms)往桶里加入Token(想象和漏洞漏水相反,有个水龙头在不断的加水),如果桶已经满了就不再加了.新请求来临时,

## ② 基于nginx 分布式限流

主要nginx配置：

```nginx
http {
 
# 根据IP地址限制速度
# 1） 第一个参数 $binary_remote_addr
#    binary_目的是缩写内存占用，remote_addr表示通过IP地址来限流
# 2） 第二个参数 zone=iplimit:20m
#    iplimit是一块内存区域（记录访问频率信息），20m是指这块内存区域的大小
# 3） 第三个参数 rate=1r/s
#    比如100r/m，标识访问的限流频率
limit_req_zone $binary_remote_addr zone=iplimit:20m rate=1r/s;

# 根据服务器级别做限流
limit_req_zone $server_name zone=serverlimit:10m rate=100r/s;

# 基于连接数的配置
limit_conn_zone $binary_remote_addr zone=perip:20m;
limit_conn_zone $server_name zone=perserver:20m;
    
    server {
        server_name www.imooc-training.com;
        location /access-limit/ {
            proxy_pass http://127.0.0.1:10086/;

            # 基于IP地址的限制
            # 1） 第一个参数zone=iplimit => 引用limit_req_zone中的zone变量
            # 2） 第二个参数burst=2，设置一个大小为2的缓冲区域，当大量请求到来。
            #     请求数量超过限流频率时，将其放入缓冲区域
            # 3) 第三个参数nodelay=> 缓冲区满了以后，直接返回503异常
            limit_req zone=iplimit burst=2 nodelay;

            # 基于服务器级别的限制
            # 通常情况下，server级别的限流速率是最大的
            limit_req zone=serverlimit burst=100 nodelay;

            # 每个server最多保持100个连接
            limit_conn perserver 100;
            # 每个IP地址最多保持1个连接
            limit_conn perip 5;

            # 异常情况，返回504（默认是503）
            limit_req_status 504;
            limit_conn_status 504;
        }

        # 限制下载速度， limit_rate_after：表示下载过了100M后，就开始限速
        location /download/ {
            limit_rate_after 100m;
            limit_rate 256k;
        }
    }
    
}
```

## ③ 基于 redis + lua 实现分布式限流

```lua 
lua 脚本

--
-- Created by IntelliJ IDEA.
-- User: hyc
--

-- 获取方法签名特征
local methodKey = KEYS[1]
redis.log(redis.LOG_DEBUG, 'key is', methodKey)

-- 调用脚本传入的限流大小
local limit = tonumber(ARGV[1])

-- 获取当前流量大小
local count = tonumber(redis.call('get', methodKey) or "0")

-- 是否超出限流阈值
if count + 1 > limit then
    -- 拒绝服务访问
    return false
else
    -- 没有超过阈值
    -- 设置当前访问的数量+1
    redis.call("INCRBY", methodKey, 1)
    -- 设置过期时间
    redis.call("EXPIRE", methodKey, 1)
    -- 放行
    return true
end
```

 

基于注解实现分布式限流

```java

/**
 * @author hyc
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessLimiter {

    int limit();

    String methodKey() default "";

}

/**
   注解主要实现类，，
 * @author hyc
 */
@Slf4j
@Aspect
@Component
public class AccessLimiterAspect {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisScript<Boolean> rateLimitLua;

    @Pointcut("@annotation(com.hyc.springcloud.annotation.AccessLimiter)")
    public void cut() {
        log.info("cut");
    }

    @Before("cut()")
    public void before(JoinPoint joinPoint) {
        // 1. 获得方法签名，作为method Key
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 2、获取直接注解的方法
        AccessLimiter annotation = method.getAnnotation(AccessLimiter.class);
        if (annotation == null) {
            return;
        }

        String key = annotation.methodKey();
        Integer limit = annotation.limit();

        // 如果没设置methodkey, 从调用方法签名生成自动一个key
        if (StringUtils.isEmpty(key)) {
            Class[] type = method.getParameterTypes();
            key = method.getClass() + method.getName();

            if (type != null) {
                String paramTypes = Arrays.stream(type)
                        .map(Class::getName)
                        .collect(Collectors.joining(","));
                log.info("param types: " + paramTypes);
                key += "#" + paramTypes;
            }
        }

        // 2. 调用Redis
        boolean acquired = stringRedisTemplate.execute(
                rateLimitLua, // Lua script的真身
                Lists.newArrayList(key), // Lua脚本中的Key列表
                limit.toString() // Lua脚本Value列表
        );

        if (!acquired) {
            log.error("your access is blocked, key={}", key);
            throw new RuntimeException("Your access is blocked");
        }
    }
}


/**
 * @author HYC
  一些配置类
 */
@Configuration
public class RedisConfiguration {

    // 如果本地也配置了StringRedisTemplate，可能会产生冲突
    // 可以指定@Primary，或者指定加载特定的@Qualifier
    @Bean
    public RedisTemplate<String, String> redisTemplate(
            RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    @Bean
    public DefaultRedisScript loadRedisScript() {
        DefaultRedisScript redisScript = new DefaultRedisScript();
        redisScript.setLocation(new ClassPathResource("ratelimiter.lua"));
        redisScript.setResultType(java.lang.Boolean.class);
        return redisScript;
    }

}


@RestController
@Slf4j
public class Controller {

    // 提醒！ 注意配置扫包路径（com.hyc.springcloud路径不同）
    @GetMapping("test-annotation")
    @AccessLimiter(limit = 1)
    public String testAnnotation() {
        return "success";
    }

}

```







































