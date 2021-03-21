package com.example.javacodedemo.test;

import com.example.javacodedemo.domain.dto.UserDTO;
import com.example.javacodedemo.domain.entity.UserDO;
import org.springframework.beans.BeanUtils;

/**
 * @author HouYC
 * @create 2020-06-25-21:59
 */
public class TestDemo {

    public static void main(String[] args) {

        UserDTO userDTO = new UserDTO();
        userDTO.setAge(11);
        userDTO.setPassword("123456");
        userDTO.setUserName("侯亚超");
        userDTO.setSex(1);

        UserDO userDO = new UserDO();

        BeanUtils.copyProperties(userDTO, userDO);

        System.out.println(userDO);
//        UserDO userDO = new UserDO();
//        userDO.setAge(11);
//        userDO.setPassword("123456");
//        userDO.setUserName("侯亚超");
//        userDO.setStatus(0);
//
//        UserDTO userDTO = new UserDTO();
//
//        BeanUtils.copyProperties(userDO, userDTO);
//
//        System.out.println(userDTO);
    }
}
