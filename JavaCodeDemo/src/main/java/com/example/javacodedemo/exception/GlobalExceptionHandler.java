package com.example.javacodedemo.exception;

import com.example.javacodedemo.common.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author HouYC
 * @create 2020-06-22-22:35
 *
 *  全局统一异常处理
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 拦截业务类异常
     * @param business
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = BusinessException.class)
    public ResponseResult businessException(BusinessException business) {
        log.info("捕获业务类异常", business);
        return ResponseResult.failure(business.getCode(), business.getMessage());
    }

    /**
     *  拦截运行时异常
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseResult runtimeExceptionHandler(RuntimeException e) {

        log.info("捕获运行时异常：", e);

        return ResponseResult.failure(ErrorCodeEnum.UNKNOWN_ERROR.getCode(), e.getMessage());
    }

    /**
     *  拦截系统级别异常
     * @param throwable
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = Throwable.class)
    public ResponseResult throwableHandler(Throwable throwable) {

        log.info("捕获Throwable 异常：",throwable);

        return ResponseResult.failure(ErrorCodeEnum.SYSTEM_ERROR.getCode(), throwable.getMessage());
    }
}
