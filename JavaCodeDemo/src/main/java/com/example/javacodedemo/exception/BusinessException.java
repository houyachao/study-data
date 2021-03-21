package com.example.javacodedemo.exception;

import lombok.Getter;

/**
 * @author HouYC
 * @create 2020-06-22-22:41
 *
 *  业务类异常
 */
public class BusinessException extends RuntimeException {

    /**
     * 异常编号
     */
    @Getter
    private final String code;

    /**
     * 根据枚举类构造业务类异常
     */
    public BusinessException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getMessage());
        this.code = errorCodeEnum.getCode();
    }

    /**
     * 自定义消息体构造函数异常
     * @param errorCodeEnum
     * @param message
     */
    public BusinessException(ErrorCodeEnum errorCodeEnum, String message) {
        super(message);
        this.code = errorCodeEnum.getCode();
    }

    /**
     * 根据异常构造业务类异常
     * @param errorCodeEnum
     * @param throwable
     */
    public BusinessException(ErrorCodeEnum errorCodeEnum, Throwable throwable) {
        super(throwable);
        this.code = errorCodeEnum.getCode();
    }
}
