package com.example.javacodedemo.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author HouYC
 * @create 2020-06-21-22:46
 *
 * 通用分页查询对象
 * 用于service层
 */
@Data
public class PageQuery<T> implements Serializable {

    private static final long serialVersionUID = 9212121343853534L;

    /**
     * 当前页
     */
    private Integer pageNo = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 20;

    /**
     * 动态查询条件
     */
    private T query;
}
