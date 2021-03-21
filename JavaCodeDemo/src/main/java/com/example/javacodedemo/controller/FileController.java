package com.example.javacodedemo.controller;

import com.example.javacodedemo.common.ResponseResult;
import com.example.javacodedemo.exception.BusinessException;
import com.example.javacodedemo.exception.ErrorCodeEnum;
import com.example.javacodedemo.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

/**
 * @author HouYC
 * @create 2020-06-25-14:41
 * 文件上传
 */
@RequestMapping("/api/files")
@Slf4j
@RestController
public class FileController {

    @Resource(name = "LocalFileServiceImpl")
    private FileService fileService;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public ResponseResult<String> upload(@NotNull MultipartFile file) {

        //文件上传
        try {
            fileService.upload(file.getInputStream(), file.getOriginalFilename());
        } catch (Exception e) {
            log.error("文件上传失败！", e);
            throw new BusinessException(ErrorCodeEnum.FILE_UPLOAD_FAILURE, e);
        }


        return ResponseResult.success(file.getOriginalFilename());
    }
}
