package com.example.javacodedemo.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author HouYC
 * @create 2020-06-21-21:52

 用于视图层展示的VO 实体类
 */
@Data
public class UserVo implements Serializable {

    private static final long serialVersionUID = 9212121343853534L;

    private String userName;

    private Integer age;
}
