# 一、使用Guava令牌桶实现全局限流

## 1、简介

​	令牌桶算法(Token Bucket)和 Leaky Bucket 效果一样但方向相反的算法,更加容易理解.随着时间流逝,系统会按恒定1/QPS时间间隔(如果QPS=100,则间隔是10ms)往桶里加入Token(想象和漏洞漏水相反,有个水龙头在不断的加水),如果桶已经满了就不再加了.新请求来临时,

​	对于一个应用系统来说，我们有时会遇到极限并发的情况，即有一个TPS/QPS阀值，如果超了阀值可能会导致服务器崩溃宕机，因此我们最好进行过载保护，防止大量请求涌入击垮系统。对服务接口进行**限流**可以达到保护系统的效果，一旦达到限制速率则可以拒绝服务、排队或等待、降级等处理。

​	原理：有一个令牌桶，单位时间内令牌会以恒定的数量（即令牌的加入速度）加入到令牌桶中，所有请求都需要获取令牌才可正常访问。当令牌桶中没有令牌可取的时候，则拒绝请求。

　　优点：相比漏桶算法，令牌桶算法允许一定的突发流量，但是又不会让突发流量超过我们给定的限制（单位时间窗口内的令牌数）。即限制了我们所说的 QPS(每秒查询率)。

常用方法：

　　　　**create**（Double permitsPerSecond）方法根据给定的（令牌:单位时间（1s））比例为令牌生成速率
　　　　**tryAcquire**（）方法尝试获取一个令牌，立即返回true/false，不阻塞，重载方法具备设置获取令牌个数、获取最大等待时间等参数
　　　　**acquire**（）方法与tryAcquire类似，但是会阻塞，尝试获取一个令牌，没有时则阻塞直到获取成功



## 2、使用

### ①maven 依赖

```java
<!-- 使用Guava令桶-->
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>20.0</version>
        </dependency>
```

### ②自定义注解

```java

  /**
   * RequestLimiter 自定义注解接口限流
   *
   * @author 
   * @version 1.0
   * @date
  */
 @Target({ElementType.METHOD})
 @Retention(RetentionPolicy.RUNTIME)
 @Documented
 public @interface RequestLimiter {
 
     /**
      * 每秒创建令牌个数，默认:10
      */
     double QPS() default 10D;
 
     /**
      * 获取令牌等待超时时间 默认:500
      */
     long timeout() default 500;
 
     /**
      * 超时时间单位 默认:毫秒
      */
     TimeUnit timeunit() default TimeUnit.MILLISECONDS;
 
     /**
      * 无法获取令牌返回提示信息
      */
     String msg() default "亲，服务器快被挤爆了，请稍后再试！";
 }
```

### ③ 拦截器

```java

 /**
  * 请求限流拦截器
  *
  * @author 
  * @version 1.0
  * @date
  */
 @Component
 public class RequestLimiterInterceptor extends GenericInterceptor {
 
     /**
      * 不同的方法存放不同的令牌桶
      */
     private final Map<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();
 
     @Override
     public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
         try {
             if (handler instanceof HandlerMethod) {
                 HandlerMethod handlerMethod = (HandlerMethod) handler;
                 RequestLimiter rateLimit = handlerMethod.getMethodAnnotation(RequestLimiter.class);
                 //判断是否有注解
                 if (rateLimit != null) {
                     // 获取请求url
                     String url = request.getRequestURI();
                     RateLimiter rateLimiter;
                     // 判断map集合中是否有创建好的令牌桶
                     if (!rateLimiterMap.containsKey(url)) {
                         // 创建令牌桶,以n r/s往桶中放入令牌
                         rateLimiter = RateLimiter.create(rateLimit.QPS());
                         rateLimiterMap.put(url, rateLimiter);
                     }
                     rateLimiter = rateLimiterMap.get(url);
                     // 获取令牌
                     boolean acquire = rateLimiter.tryAcquire(rateLimit.timeout(), rateLimit.timeunit());
                     if (acquire) {
                         //获取令牌成功
                         return super.preHandle(request, response, handler);
                     } else {
                         log.warn("请求被限流,url:{}", request.getServletPath());
                         this.write(response, new GenericResult(StateCode.ERROR_SERVER, rateLimit.msg()));
                         return false;
                     }
                 }
             }
             return true;
         } catch (Exception var6) {
             var6.printStackTrace();
             this.write(response, new GenericResult(StateCode.ERROR, "对不起,请求似乎出现了一些问题,请您稍后重试！"));
             return false;
         }
     }
 
 }
```

### ④ 注册拦截器

```java
@Configuration
@EnableWebMvc
@Slf4j
 public class WebMvcConfig implements WebMvcConfigurer {
 
     /**
      * 请求限流拦截器
      */
     @Autowired
     protected RequestLimiterInterceptor requestLimiterInterceptor;
 
     public WebMvcConfig() {}
 
    /**
     * 向Web中添加拦截器
     * @param registration
     */
     @Override
     public void addInterceptors(InterceptorRegistry registry) {
         // 请求限流  配置拦截器，拦截所有以/ 请求
         registry.addInterceptor(requestLimiterInterceptor).addPathPatterns("/**");
     }
 
 }
```

### ⑤在接口上配置拦截器

```java
@RequestLimiter(QPS = 5D, timeout = 200, timeunit = TimeUnit.MILLISECONDS,msg = "服务器繁忙,请稍后再试")
@GetMapping("/test")
@ResponseBody
public String test(){
      return "";
}
```



以上主要适用于单体项目，不适应用于微服务。

微服务可以借助了Redis来实现。

















































