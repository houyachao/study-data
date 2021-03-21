package com.example.javacodedemo.service.impl;

import com.example.javacodedemo.exception.BusinessException;
import com.example.javacodedemo.exception.ErrorCodeEnum;
import com.example.javacodedemo.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * @author HouYC
 * @create 2020-06-25-14:46
   本地文档上传
 */
@Service("LocalFileServiceImpl")
@Slf4j
public class LocalFileServiceImpl implements FileService {

    /**
     * 存储空间
     */
    private static final String BUCKET = "uploads";

    @Override
    public void upload(InputStream inputStream, String fileName) {
        //拼接文件的存储地址
        String storagePath = BUCKET + "/" + fileName;
        try (

            //JDK 8 TWR 不能关闭外部资源的
            InputStream innerInputStream = inputStream;
            FileOutputStream outputStream = new FileOutputStream(new File(storagePath));
        ) {

            //拷贝缓冲区
            byte[] buffer = new byte[1024];
            //读取文件流长度
            int len;

            //循环读取innerInputStream中数据写入到outputStream
            while ((len = innerInputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }

            //冲刷流
            outputStream.flush();
        } catch (Exception e) {

        }
    }

    @Override
    public void upload(File file) {

        try {
            upload(new FileInputStream(file), file.getName());
        } catch (Exception e) {
            log.error("文件上传异常", e);
            throw new BusinessException(ErrorCodeEnum.FILE_UPLOAD_FAILURE, e);
        }
    }
}
