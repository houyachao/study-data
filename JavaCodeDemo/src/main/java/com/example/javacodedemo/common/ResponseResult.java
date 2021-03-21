package com.example.javacodedemo.common;

import com.example.javacodedemo.exception.ErrorCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author HouYC
 * @create 2020-06-21-21:55
 *
 * 类描述：通用返回结果模型
 */
@Data
@ApiModel(
        value = "统一封装结果实体",
        description = "封装统一返回结果实体"
)
public class ResponseResult<T> implements Serializable {

    private static final long serialVersionUID = 9212121343853534L;

    /**
     * 是否成功
     */
    @ApiModelProperty(
            name = "success",
            value = "执行成功",
            required = true,
            dataType = "Boolean"
    )
    private Boolean success;

    /**
     * 编码
     */
    @ApiModelProperty(
            value = "编码"
    )
    private String code;

    /**
     * 描述信息
     */
    @ApiModelProperty(
            value = "描述信息"
    )
    private String message;

    /**
     * 结果
     */
    @ApiModelProperty(
            value = "泛型结果T"
    )
    private T result;

    public static <T> ResponseResult<T> success(T result) {
        ResponseResult<T> responseResult = new ResponseResult<>();

        responseResult.setSuccess(Boolean.TRUE);
        responseResult.setResult(result);
        return responseResult;
    }

    public static <T> ResponseResult<T> failure(String code, String message) {
        ResponseResult<T> responseResult = new ResponseResult<>();
        responseResult.setMessage(message);
        responseResult.setCode(code);
        responseResult.setSuccess(Boolean.FALSE);

        return responseResult;
    }

    public static <T> ResponseResult<T> failure(ErrorCodeEnum errorCodeEnum) {
        return failure(errorCodeEnum.getCode(), errorCodeEnum.getMessage());
    }


}
