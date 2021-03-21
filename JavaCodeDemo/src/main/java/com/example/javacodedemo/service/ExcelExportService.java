package com.example.javacodedemo.service;

import com.example.javacodedemo.domain.dto.UserQueryDTO;

/**
 * @author HouYC
 * @create 2020-06-25-16:59
 *  Excel导出服务接口
 */
public interface ExcelExportService {

    /**
     * 导出Excel
     * @param query
     * @param fileName
     */
    void export(UserQueryDTO query, String fileName);

    /**
     * 异步导出服务
     * @param query
     * @param fileName
     */
    void asyncExport(UserQueryDTO query, String fileName);
}
