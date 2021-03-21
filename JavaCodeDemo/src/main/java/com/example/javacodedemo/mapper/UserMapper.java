package com.example.javacodedemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.javacodedemo.domain.entity.UserDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author HouYC
 * @create 2020-06-21-22:31
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDO> {
}
