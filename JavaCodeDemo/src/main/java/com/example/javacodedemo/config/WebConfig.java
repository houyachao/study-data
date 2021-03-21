package com.example.javacodedemo.config;

import com.example.javacodedemo.interceptor.RateLimitInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import javax.annotation.Resource;

/**
 * @author HouYC
 * @create 2020-06-24-22:39
 *
 * Web 配置类
 */
@Slf4j
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    /**
     * 全局限流拦截器
     */
    @Resource
    private RateLimitInterceptor rateLimitInterceptor;

    /**
     * 向Web中添加拦截器
     * @param registration
     */
    @Override
    public void addInterceptors(InterceptorRegistry registration) {
        //配置拦截器，拦截所有以/api 开头的请求
        registration.addInterceptor(rateLimitInterceptor).addPathPatterns("/api/**");
    }

    /**
     * 静态资源配置
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        //配置本地文件夹目录映射
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("C:\\Users\\Administrator\\Desktop\\JavaCodeDemo\\uploads\\");

        //Swagger2 做的映射
        registry.addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
