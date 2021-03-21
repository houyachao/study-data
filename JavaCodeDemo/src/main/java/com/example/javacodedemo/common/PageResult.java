package com.example.javacodedemo.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author HouYC
 * @create 2020-06-21-22:11
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 9212121343853534L;

    /**
     * 当前页号
     */
    private Integer pageNo;

    /**
     * 每页行数
     */
    private Integer pageSize;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Long pageNum;

    /**
     * 动态内容
     */
    private T data;

}
