package com.example.javacodedemo.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author HouYC
 * @create 2020-06-22-21:23
 */
@Getter
@AllArgsConstructor
public enum ErrorCodeEnum {

    // 0******成功
    SUCCESS("0000", "操作成功"),

    //1-----参数异常
    PARAM_ERROR("1001", "参数异常"),
    PARAM_NULL("1002", "参数为空"),
    PARAM_FORMAT_ERROR("1003", "参数格式不正确"),
    PARAM_VALUE_ERROR("1004", "参数值不正确"),

    //2-----系统异常
    SYSTEM_ERROR("2001", "服务异常"),
    UNKNOWN_ERROR("2002", "未知异常"),

    //3-----业务异常
    XXX("3001", "业务异常"),
    INSERT_FAILURE("3002", "新增异常"),
    UPDATE_FAILURE("3003", "更新异常"),
    DELETE_FAILURE("3004", "删除异常"),

    RATE_LIMIT_ERROR("3005", "限流异常"),
    FILE_UPLOAD_FAILURE("3006", "文件上传异常");


    /**
     * 错误码
     */
    private String code;

    /**
     *  描述信息
     */
    private String message;
}
