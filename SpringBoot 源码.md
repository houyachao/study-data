# SpringBoot 源码

## 1、系统初始化加载器

​	ApplicationContextInitializer是Spring框架提供的接口, 该接口的主要功能就是在接口ConfigurableApplicationContext刷新之前，允许用户对ConfigurableApplicationContext实例做一定的操作。该接口只有一个initialize方法。

### 1.1 三种实现

#### ① 在META-INF 文件下新建spring.factories文件

```java
META-INF/spring.factories

org.springframework.context.ApplicationContextInitializer=\
com.hyc.config.FastInitialContext
```

实现application ContextInitializer接口

```java

/**
 * @author HouYC
 * @create 2020-12-27-10:40
 *
 *   方式1：
 *  系统在初始化的时候，向系统中注册自己的bean
 *
 * @Order（1）：表示 系数越小，越先被加载
 */
@Order(1)
public class FastInitialContext implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        ConfigurableEnvironment environment = configurableApplicationContext.getEnvironment();
        Map<String, Object> map = Maps.newConcurrentMap();
        map.put("key1", "values1");
        MapPropertySource fastInitialContext = new MapPropertySource("fastInitialContext", map);

        environment.getPropertySources().addLast(fastInitialContext);

        System.out.println("run fastInitialContext");
    }
}

```

#### ② 在配置文件中（application.properties）配置

```properties
application.properties 文件

context.initializer.classes=com.hyc.config.TwoInitialContext
```

```java

/**
 * @author HouYC
 * @create 2020-12-27-10:40
 *
 *   方式1：
 *  系统在初始化的时候，向系统中注册自己的bean
 *
 * @Order（2）：表示 系数越小，越先被加载
 */
@Order(2)
public class TwoInitialContext implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        ConfigurableEnvironment environment = configurableApplicationContext.getEnvironment();
        Map<String, Object> map = Maps.newConcurrentMap();
        map.put("key2", "values2");
        MapPropertySource fastInitialContext = new MapPropertySource("twoInitialContext", map);

        environment.getPropertySources().addLast(fastInitialContext);

        System.out.println("run twoInitialContext");
    }
}

```

这里 order（2），会最先被加载，因为在项目启动的时候，这个实现类会被DelegatingApplicationContextInitializer加载，因为这个类是从application.properties 读取 “context.initializer.classes”，才可以加载到TwoInitialContext。又因为 DelegatingApplicationContextInitializer 类中 order = 0， 所以，被优先加载。

#### ③ 在启动类 添加要被加载的类

```java
@SpringBootApplication
public class SourceAnalyzeApplication {
    public static void main(String[] args) {
        //SpringApplication.run(SourceAnalyzeApplication.class, args);
        SpringApplication springApplication = new SpringApplication(SourceAnalyzeApplication.class);
        springApplication.addInitializers(new ThriedInitialContext());
        springApplication.run(args);
    }
}
```

```java

/**
 * @author HouYC
 * @create 2020-12-27-10:40
 *
 *   方式1：
 *  系统在初始化的时候，向系统中注册自己的bean
 *
 * @Order（1）：表示 系数越小，越先被加载
 */
@Order(3)
public class ThriedInitialContext implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        ConfigurableEnvironment environment = configurableApplicationContext.getEnvironment();
        Map<String, Object> map = Maps.newConcurrentMap();
        map.put("key3", "values3");
        MapPropertySource fastInitialContext = new MapPropertySource("thriedInitialContext", map);

        environment.getPropertySources().addLast(fastInitialContext);

        System.out.println("run thriedInitialContext");
    }
}

```

### 1.2 加载流程

![1.系统加载器8](C:\Users\Administrator\Desktop\笔记\SpringBoot 源码\1.系统加载器8.png)

![1.系统加载器源码9](C:\Users\Administrator\Desktop\笔记\SpringBoot 源码\1.系统加载器源码9.png)

![1.系统加载器源码7](C:\Users\Administrator\Desktop\笔记\SpringBoot 源码\1.系统加载器源码7.png)

![1.系统加载器源码1](C:\Users\Administrator\Desktop\笔记\SpringBoot 源码\1.系统加载器源码1.png)

![1.系统初始化加载器源码10](C:\Users\Administrator\Desktop\笔记\SpringBoot 源码\1.系统初始化加载器源码10.png)

![1.系统加载器源码2](C:\Users\Administrator\Desktop\笔记\SpringBoot 源码\1.系统加载器源码2.png)

![1.系统加载器源码3](C:\Users\Administrator\Desktop\笔记\SpringBoot 源码\1.系统加载器源码3.png)

![1.系统加载器源码4](C:\Users\Administrator\Desktop\笔记\SpringBoot 源码\1.系统加载器源码4.png)



DelegatingApplicationContextInitializer：类

![1.系统加载器源码5](C:\Users\Administrator\Desktop\笔记\SpringBoot 源码\1.系统加载器源码5.png)

![1.系统加载器源码6](C:\Users\Administrator\Desktop\笔记\SpringBoot 源码\1.系统加载器源码6.png)

### 1.3 总结

​	① 定义在 spring.factories 文件中被 SpringFactoriesLoader 发现注册。

​	② SpringApplication 初始化完毕后手动添加。

​	③ 定义成环境变量被DelegatingApplicationContextInitializer发现注册。

## 2、监听器

![2.监听器模式1](C:\Users\Administrator\Desktop\笔记\SpringBoot 源码\2.监听器模式1.png)

### 1、监听器模式要素

#### ① 事件

#### ② 监听器

#### ③ 广播器

#### ④ 触发机制

### 2、三种实现

```java
/**
 * @author 侯亚超
 * 自定义监听器， 配置spring.factories
 */
@Order(1)
public class FirstListener implements ApplicationListener<ApplicationStartedEvent> {
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        System.out.println("hello first");
    }
}

spring.factories: 文件

org.springframework.context.ApplicationListener=\
com.hyc.listener.FirstListener
```

```java
![2.监听器模式2](C:\Users\Administrator\Desktop\笔记\SpringBoot 源码\2.监听器模式2.png)application.properties

# 监听器配置
context.listener.classes=com.hyc.listener.ThirdListener

@Order(3)
public class ThirdListener implements ApplicationListener<ApplicationStartedEvent> {
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        System.out.println("hello third");
    }
}
```

```java
@Order(3)
public class FourthListener implements SmartApplicationListener {
    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return ApplicationStartedEvent.class.isAssignableFrom(eventType) || ApplicationPreparedEvent.class.isAssignableFrom(eventType);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        System.out.println("hello fourth");
    }
}
```

![2.监听器模式2](C:\Users\Administrator\Desktop\笔记\SpringBoot 源码\2.监听器模式2.png)

![2.监听器源码4](C:\Users\Administrator\Desktop\笔记\SpringBoot 源码\2.监听器源码4.png)

![3.监听器源码3](C:\Users\Administrator\Desktop\笔记\SpringBoot 源码\3.监听器源码3.png)

## 3、bean 解析

### 1、bean的配置方式

#### ① 基于XML的

​	需要在xml 配置文件中配置bean的信息。

#### ② 基于注解的五种实现方式

##### 1. 基于 @Configuration注解的配置类

```java
@Configuration
public class BeanConfiguration implements SuperConfiguration{

    @Bean("dog")
    Animal getDog() {
        return new Dog();
    }

}

```

##### 2. 基于 @Component 注解配置的类

```java
@Component
public class Teacher {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

##### 3. 基于BeanDefinitionRegistryPostProcessor

```java

@Component
public class MyBeanRegister implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition();
        rootBeanDefinition.setBeanClass(Monkey.class);
        registry.registerBeanDefinition("monkey", rootBeanDefinition);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}

```

##### 4.基于ImportBeanDefinitionRegistrar

```java

public class MyBeanImport implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition();
        rootBeanDefinition.setBeanClass(Bird.class);
        registry.registerBeanDefinition("bird", rootBeanDefinition);
    }
}

```

##### 5. 基于 FactoryBean

```java

@Component
public class MyCat implements FactoryBean<Animal> {
    @Override
    public Animal getObject() throws Exception {
        return new Cat();
    }

    @Override
    public Class<?> getObjectType() {
        return Animal.class;
    }
}

```





















































