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
    public Result<java.util.Map<String, Object>> login(@RequestParam String username, @RequestParam String password) {
        User user = userService.login(username, password);
        // 生成 JWT Token
        String token = com.example.librarysystem.utils.JwtUtil.generate(user.getId(), user.getUsername(), user.getRole());
        user.setPassword(null);

        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("token", token);
        result.put("user", user);
        return Result.success(result);
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