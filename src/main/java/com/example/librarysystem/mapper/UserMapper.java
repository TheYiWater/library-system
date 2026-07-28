package com.example.librarysystem.mapper;

import com.example.librarysystem.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User selectById(Long id);

    User selectByUsername(@Param("username") String username);

    int insert(User user);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}