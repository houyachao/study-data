package com.example.javacodedemo.domain.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.example.javacodedemo.util.LocalDateTimeStringConverter;
import com.sun.xml.internal.ws.developer.Serialization;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author HouYC
 * @create 2020-06-25-17:09
 *
 * 导出Excel 对象类
 */
@Data
public class UserExportDTO implements Serializable {

    private static final long serialVersionUID = -5512213661829649535L;

    /**
     * String 类型
     */
    @ExcelProperty(value = "用户名")
    private String userName;

    /**
     * Integer 类型
     */
    @ExcelProperty(value = "年龄")
    private Integer age;

    /**
     * LocalDateTime 类型
     */
    @ExcelProperty(value = "创建时间", converter = LocalDateTimeStringConverter.class)
    @DateTimeFormat("yyyy年MM月dd日HH时mm分ss秒SSS毫秒")
    private LocalDateTime createTime;
}
