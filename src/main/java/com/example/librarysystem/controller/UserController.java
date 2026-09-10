package com.example.librarysystem.controller;

import com.example.librarysystem.common.BusinessException;
import com.example.librarysystem.common.Result;
import com.example.librarysystem.entity.User;
import com.example.librarysystem.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

    /**
     * 接收 JSON 请求体:{"username":"","password":""}
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || password == null) {
            throw new BusinessException(400, "用户名和密码不能为空");
        }
        User user = userService.login(username, password);
        String token = com.example.librarysystem.utils.JwtUtil.generate(user.getId(), user.getUsername(), user.getRole());
        user.setPassword(null);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("role", user.getRole());
        return Result.success(result);
    }

    /**
     * 接收 JSON 请求体:{"username":"","password":"","nickname":""}
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String nickname = body.get("nickname");
        if (username == null || password == null) {
            throw new BusinessException(400, "用户名和密码不能为空");
        }
        userService.register(username, password, nickname);
        return Result.success("注册成功", null);
    }
    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public Result<User> profile(@RequestAttribute("userId") Long userId) {
        return Result.success(userService.getById(userId));
    }
    /**
     * 修改密码
     * 请求体:{"oldPassword":"","newPassword":""}
     */
    @PutMapping("/password")
    public Result<Void> changePassword(
            @RequestAttribute("userId") Long userId,
            @RequestBody Map<String, String> body) {
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        if (oldPassword == null || newPassword == null) {
            throw new BusinessException(400, "原密码和新密码不能为空");
        }
        userService.changePassword(userId, oldPassword, newPassword);
        return Result.success("密码修改成功", null);
    }
}