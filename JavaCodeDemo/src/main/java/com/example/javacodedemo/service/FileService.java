package com.example.javacodedemo.service;

import java.io.File;
import java.io.InputStream;

/**
 * @author HouYC
 * @create 2020-06-25-14:44
 */
public interface FileService {

    /**
     * 文件上传
     * @param inputStream
     * @param fileName
     */
    void upload(InputStream inputStream, String fileName);

    /**
     * 文件上传
     * @param file
     */
    void upload(File file);
}
