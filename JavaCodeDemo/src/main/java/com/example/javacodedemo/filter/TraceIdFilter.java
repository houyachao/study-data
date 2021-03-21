package com.example.javacodedemo.filter;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.UUID;

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
