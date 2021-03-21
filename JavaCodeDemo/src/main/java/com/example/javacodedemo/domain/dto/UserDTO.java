package com.example.javacodedemo.domain.dto;

import com.example.javacodedemo.util.InsertValidationGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author HouYC
 * @create 2020-06-21-21:51
 *
 *  用于数据层传输的DTO 实体类
 */
@Data
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 9212121343853534L;

    @NotBlank(message = "用户名不能为空",groups = InsertValidationGroup.class)
    private String userName;

    @NotBlank(message = "密码不能为空", groups = InsertValidationGroup.class)
    @Length(min = 6, max = 12, message = "密码必须大于6位，小于12位")
    private String password;

    private Integer age;

    private LocalDateTime createTime;

    private Integer sex;
}
