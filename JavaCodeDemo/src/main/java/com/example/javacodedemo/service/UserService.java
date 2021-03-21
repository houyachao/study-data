package com.example.javacodedemo.service;

import com.example.javacodedemo.common.PageQuery;
import com.example.javacodedemo.common.PageResult;
import com.example.javacodedemo.common.ResponseResult;
import com.example.javacodedemo.domain.dto.UserDTO;
import com.example.javacodedemo.domain.dto.UserQueryDTO;

import java.util.List;

/**
 * @author HouYC
 * @create 2020-06-21-22:51
 */
public interface UserService {

    /**
     * 新增
     * @param userDTO
     * @return        主要是影响的行数， 返回 1-影响一行 0-（保存失败）  > 1 影响多行
     */
    int save(UserDTO userDTO);

    /**
     * 修改
     * @param id
     * @param userDTO
     * @return
     */
    int update(Long id, UserDTO userDTO);

    /**
     * 删除
     * @param id
     * @return
     */
    int delete(Long id);

    /**
     * 分页查询
     * @param userDTOPageQuery
     * @return
     */
    PageResult<List<UserDTO>> query(PageQuery<UserQueryDTO> userDTOPageQuery);
}

