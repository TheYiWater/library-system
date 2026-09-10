package com.example.librarysystem.service;

import com.example.librarysystem.entity.User;

public interface UserService {
    User getById(Long id);

    User login(String username, String password);

    void register(String username, String password, String nickname);

    void changePassword(Long userId, String oldPassword, String newPassword);
}