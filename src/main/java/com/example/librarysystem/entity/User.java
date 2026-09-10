package com.example.librarysystem.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Integer role;
    private Long id;
    private String username;
    private String password;
    private String realName;

    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
