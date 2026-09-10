package com.example.librarysystem.mapper;

import com.example.librarysystem.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
@Mapper
public interface UserMapper {
    User selectById(Long id);

    User selectByUsername(@Param("username") String username);

    int insert(User user);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    int updatePassword(@Param("id") Long id, @Param("password") String password);

    int updateRole(@Param("id") Long id, @Param("role") Integer role);

    int count();

    Long countAdmins();

    List<User> listAll();
}