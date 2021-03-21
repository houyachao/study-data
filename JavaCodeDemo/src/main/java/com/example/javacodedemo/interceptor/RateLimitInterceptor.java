package com.example.javacodedemo.interceptor;

import com.example.javacodedemo.exception.BusinessException;
import com.example.javacodedemo.exception.ErrorCodeEnum;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author HouYC
 * @create 2020-06-24-22:27
 *
 *  全局限流拦截器
 */
@Slf4j
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    /**
     * 限流器实例（QPS限制为5）： 允许这一秒的请求可以访问5次，这5次可以得到令牌，这一秒5次后不会得到令牌
     */
    private static final RateLimiter rateLimiter = RateLimiter.create(5);

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception{

        //尝试去获取这个令牌，如果没有获取到令牌，抛出一个异常
        if (!rateLimiter.tryAcquire()) {
            log.info("系统被限流了。。。。");
            throw new BusinessException(ErrorCodeEnum.RATE_LIMIT_ERROR);
        }
        return true;
    }
}
