package com.example.librarysystem.service.impl;

import com.example.librarysystem.common.BusinessException;
import com.example.librarysystem.entity.User;
import com.example.librarysystem.mapper.UserMapper;
import com.example.librarysystem.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }

    @Override
    public User login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (!password.equals(user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() != 1) {
            throw new BusinessException(403, "账号已被禁用");
        }
        return user;
    }

    @Override
    public void register(String username, String password, String nickname) {
        User exist = userMapper.selectByUsername(username);
        if (exist != null) {
            throw new BusinessException(400, "用户名已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRealName(nickname);  // 前端叫 nickname,数据库字段叫 realName
        if (user.getRole() == null) {
            user.setRole(0);
        }
        if (user.getStatus() == null) {
            user.setStatus(1); // 1 = 正常
        }
        userMapper.insert(user);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (!oldPassword.equals(user.getPassword())) {
            throw new BusinessException(400, "原密码错误");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new BusinessException(400, "新密码长度不能少于6位");
        }
        userMapper.updatePassword(userId, newPassword);
    }
}