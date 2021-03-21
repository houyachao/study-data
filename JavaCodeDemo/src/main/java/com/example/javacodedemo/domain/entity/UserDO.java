package com.example.javacodedemo.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author HouYC
 * @create 2020-06-21-21:45\
 *
 *  用户DO实体类，和数据库字段一一对应
 */
@Data
@TableName("user")
@ToString
public class UserDO implements Serializable {


    private static final long serialVersionUID = 9212121343853534L;

    private Integer id;

    private String userName;

    private String password;

    private Integer age;

    /**
     * 注解 是 和 CommonMetaObjectHandler 对象，在插入sql语句时起作用
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    private Integer status;

    /**
     * 添加该注解，不用我们自动加1，每次更新或者新增时，会自动添加
     */
    @Version
    private Integer version;
}
