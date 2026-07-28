package com.example.librarysystem.controller;

import com.example.librarysystem.common.Result;
import com.example.librarysystem.entity.User;
import com.example.librarysystem.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @PostMapping("/login")
    public Result<User> login(@RequestParam String username, @RequestParam String password) {
        User user = userService.login(username, password);
        user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping("/register")
    public Result<Void> register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam(required = false) String realName) {
        userService.register(username, password, realName);
        return Result.success("注册成功", null);
    }
}