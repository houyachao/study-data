package com.example.javacodedemo.controller;

import com.example.javacodedemo.common.PageResult;
import com.example.javacodedemo.common.ResponseResult;
import com.example.javacodedemo.domain.dto.UserDTO;
import com.example.javacodedemo.domain.dto.UserQueryDTO;
import com.example.javacodedemo.service.ExcelExportService;
import com.example.javacodedemo.util.InsertValidationGroup;
import io.swagger.annotations.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * @author HouYC
 * @create 2020-06-21-21:54
 */
@RestController
@RequestMapping("/api/users")
@Slf4j
@Validated
@Api(
        value = "用户管理Controller",
        protocols = "http,https",
        hidden = false
)
public class UserController {

    @Autowired
    private ExcelExportService excelExportService;

    /**
     * 用户导出
     * @param query
     * @param fileName
     * @return
     */
    public ResponseResult<Boolean> export(@Validated UserQueryDTO query, @NotEmpty String fileName) {

        excelExportService.export(query, fileName);
        return ResponseResult.success(Boolean.TRUE);
    }

    /**
     * 新增用户
     * @param userDTO
     * @return
     */
    @PostMapping("/save")
    public ResponseResult save(@Validated(InsertValidationGroup.class) @RequestBody UserDTO userDTO) {

        return ResponseResult.success("新增成功！");
    }

    /**
     * 更新用户信息
     * @param id
     * @param userDTO
     * @return
     */
    @ApiOperation(
            value = "更新用户信息",
            notes = "备注说明信息",
            response = ResponseResult.class,
            httpMethod = "PUT"
    )
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "id",
                    value = "参数说明，用户主键",
                    required = true,
                    paramType = "path",
                    dataType = "Long",
                    example = "1234"
            ),
            @ApiImplicitParam(
                    name = "userDTO",
                    value = "用户信息",
                    required = true,
                    paramType = "body",
                    dataType = "UserDTO",
                    dataTypeClass = UserDTO.class
            )
    })
    @ApiResponses({
            @ApiResponse(code = 0000, message = "执行成功"),
            @ApiResponse(code = 3004, message = "执行失败")
    })
    @PutMapping("/update")
    @CacheEvict(cacheNames = "users-cache", allEntries = true)
    public ResponseResult update(@NotBlank @PathVariable Long id,
                                 @Validated(InsertValidationGroup.class) @RequestBody UserDTO userDTO) {

        return ResponseResult.success("更新成功!");
    }

    /**
     * 删除用户信息
     * @param id
     * @return
     */
    @DeleteMapping("delete")
    public ResponseResult delete(@NotBlank @PathVariable Long id) {

        return ResponseResult.success("删除成功！");
    }

    /**
     *  查询用户信息
     * @param pageNo
     * @param pageSize
     * @param query
     * @return
     */
    @GetMapping("get")
    @Cacheable(cacheNames = "users-cache")
    public ResponseResult<PageResult> query(@NotBlank @RequestParam("pageNo") Integer pageNo,
                                            @NotBlank @RequestParam("pageSize") Integer pageSize,
                                            @Validated @RequestBody UserQueryDTO query) {

        log.info("未使用缓存");
        return ResponseResult.success(new PageResult());
    }

}
