# 一、使用traceId跟踪请求全流程日志

​	当请求来时生成一个traceId放在ThreadLocal里，然后打印时去取就行了。但在不改动原有输出语句的前提下自然需要日志框架的支持了。

MDC 介绍
​	MDC（Mapped Diagnostic Context，映射调试上下文）是 log4j 和 logback 提供的一种方便在多线程条件下记录日志的功能。MDC 可以看成是一个与当前线程绑定的Map，可以往其中添加键值对。MDC 中包含的内容可以被同一线程中执行的代码所访问。当前线程的子线程会继承其父线程中的 MDC 的内容。当需要记录日志时，只需要从 MDC 中获取所需的信息即可。MDC 的内容则由程序在适当的时候保存进去。对于一个 Web 应用来说，通常是在请求被处理的最开始保存这些数据。

简而言之，MDC就是日志框架提供的一个`InheritableThreadLocal`，项目代码中可以将键值对放入其中，然后使用指定方式取出打印即可。

### 1、TraceId 过滤器

```java

/**
 * @author HouYC
 * @create 2020-06-25-13:05
    TraceId 过滤器
 */
@WebFilter(urlPatterns = "/*")
@Order(1)
public class TraceIdFilter implements Filter {

    /**
     * traceId 常量
     */
    private static final String TRACE_ID = "traceId";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        //尝试从请求信息中获取TraceID信息
        String traceId = servletRequest.getParameter(TRACE_ID);

        //为空 设置默认值
        if (StringUtils.isEmpty(traceId)) {
            traceId = UUID.randomUUID().toString();
        }

        //在MDC 中放入traceID
        MDC.put(TRACE_ID, traceId);

        filterChain.doFilter(servletRequest, servletResponse);
    }
}

```

### 2、 在启动类开启

```java
@SpringBootApplication
@ServletComponentScan
public class JavacodedemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavacodedemoApplication.class, args);
    }

}

```

### 3、在配置文件添加logging 输入格式

```java
##日志输出格式
logging.pattern.console=%clr(%d{${LOG_DATEFORMAT_PATTERN:yyyy-MM--dd HH:mm:ss.SSS}}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr([%X{traceId}]) %clr(${PID:-}){magenta} %clr(---) {faint} %clr([%15.15t]) {faint} %clr(%-40.40logger{39}) {cyan} %clr(:) {faint} %m%n

```



以上主要适用于单体项目。



















































