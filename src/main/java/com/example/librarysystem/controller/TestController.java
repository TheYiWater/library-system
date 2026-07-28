package com.example.librarysystem.controller;

import com.example.librarysystem.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping
    public Result<String> hello() {
        return Result.success("Hello, 图书管理系统启动成功！");
    }
}