package com.example.javacodedemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.javacodedemo.common.PageQuery;
import com.example.javacodedemo.common.PageResult;
import com.example.javacodedemo.common.ResponseResult;
import com.example.javacodedemo.domain.dto.UserDTO;
import com.example.javacodedemo.domain.dto.UserQueryDTO;
import com.example.javacodedemo.domain.entity.UserDO;
import com.example.javacodedemo.mapper.UserMapper;
import com.example.javacodedemo.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author HouYC
 * @create 2020-06-22-19:48
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public int save(UserDTO userDTO) {
        UserDO userDO = new UserDO();

        // 浅拷贝 属性名相同才能拷贝
        BeanUtils.copyProperties(userDTO, userDO);

        return userMapper.insert(userDO);
    }

    @Override
    public int update(Long id, UserDTO userDTO) {
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userDTO, userDO);
        userDO.setId(Integer.valueOf(String.valueOf(id)));
        return userMapper.updateById(userDO);
    }

    @Override
    public int delete(Long id) {
        return userMapper.deleteById(id);
    }

    @Override
    public PageResult<List<UserDTO>> query(PageQuery<UserQueryDTO> userDTOPageQuery) {
        //参数构造
        Page page = new Page(userDTOPageQuery.getPageNo(), userDTOPageQuery.getPageSize());

        UserDO query = new UserDO();
        BeanUtils.copyProperties(userDTOPageQuery.getQuery(), query);

        //如果属性不一样，需要做一些特殊处理
        QueryWrapper queryWrapper = new QueryWrapper(query);
        //查询
        IPage<UserDO> userDOIPage = userMapper.selectPage(page, queryWrapper);

        //解析结果
        PageResult pageResult = new PageResult();
        pageResult.setPageNo((int) userDOIPage.getCurrent());
        pageResult.setPageSize((int) userDOIPage.getSize());
        pageResult.setTotal(userDOIPage.getTotal());
        pageResult.setPageNum(userDOIPage.getPages());

        //如果查询数据为null，就new一个
        List<Object> userDTOList = Optional.ofNullable(userDOIPage.getRecords())
                .map(List::stream)
                .orElseGet(Stream::empty)
                .map(userDO -> {
                    UserDTO userDTO = new UserDTO();
                    BeanUtils.copyProperties(userDO, userDTO);
                    return userDTO;
                }).collect(Collectors.toList());
        pageResult.setData(userDTOList);

        return pageResult;
    }
}
