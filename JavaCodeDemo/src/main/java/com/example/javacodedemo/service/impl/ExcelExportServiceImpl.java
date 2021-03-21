package com.example.javacodedemo.service.impl;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.example.javacodedemo.common.PageQuery;
import com.example.javacodedemo.common.PageResult;
import com.example.javacodedemo.domain.dto.UserDTO;
import com.example.javacodedemo.domain.dto.UserExportDTO;
import com.example.javacodedemo.domain.dto.UserQueryDTO;
import com.example.javacodedemo.service.ExcelExportService;
import com.example.javacodedemo.service.FileService;
import com.example.javacodedemo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author HouYC
 * @create 2020-06-25-17:00
 */
@Slf4j
@Service
public class ExcelExportServiceImpl implements ExcelExportService {

    @Resource(name = "LocalFileServiceImpl")
    private FileService fileService;
    @Autowired
    private UserService userService;

    /**
     * 执行数据库查询和Excel导出，将数据库写入到outputStream中
     * @param outputStream
     * @param query
     */
    private void export(ByteArrayOutputStream outputStream, UserQueryDTO query) {

        //1. 需要创建一个EasyExcel导出对象(即是表格中需要显示的字段)
        ExcelWriter excelWrite = EasyExcelFactory.write(outputStream, UserExportDTO.class).build();

        //2. 分批加载数据
        PageQuery<UserQueryDTO> pageQuery = new PageQuery<>();
        pageQuery.setQuery(query);
        pageQuery.setPageSize(2);
        int pageNo = 0;
        PageResult<List<UserDTO>> pageResult;

        do {
            //先累加，再赋值，要跟pageNo++ 区分
            pageQuery.setPageNo(++pageNo);
            pageResult= userService.query(pageQuery);

            //数据转化：UserDTO转换成UserExportDTO
            List<UserExportDTO> userExportDTOList = Optional.ofNullable(pageResult.getData())
                    .map(List::stream)
                    .orElseGet(Stream::empty)
                    .map(userDTO -> {
                        UserExportDTO userExportDTO = new UserExportDTO();

                        //转换
                        BeanUtils.copyProperties(userDTO, userExportDTO);
                        return userExportDTO;
                    }).collect(Collectors.toList());

            //3. 导出分批加载的数据
            // 将数据写入到不同的sheet页中
            WriteSheet writeSheet = EasyExcelFactory.writerSheet(pageNo, "第" + pageNo + "页").build();

            log.info("结束导出第[{}]页数据", pageNo);

            excelWrite.write(userExportDTOList, writeSheet);
            //总页数 大于 当前页 说明还有数据，需要再次执行
        } while (pageResult.getPageNum() > pageNo);

        //4. 收尾,执行finish，才会关闭Excel文件流
        excelWrite.finish();
        log.info("导出完成");
    }

    @Override
    public void export(UserQueryDTO query, String fileName) {

        //输出流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        //1. 实现数据导出的Excel中
        export(outputStream, query);

        //输入流
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        //2. 实现文件上传
        fileService.upload(inputStream, fileName);
    }

    /**
     * @Async("exportServiceExecutor") 标注 是使用线程池 执行方法
     * 异步导出服务
     * @param query
     * @param fileName
     */
    @Async("exportServiceExecutor")
    @Override
    public void asyncExport(UserQueryDTO query, String fileName) {
        export(query, fileName);
    }
}
