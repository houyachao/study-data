package com.example.javacodedemo.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author HouYC
 * @create 2020-06-21-22:07
 *
 * 数据查询DTO 实体
 */
@Data
public class UserQueryDTO implements Serializable {

    private static final long serialVersionUID = 9212121343853534L;

    @NotBlank(message = "用户名不能为空")
    private String userName;
}
