package com.example.librarysystem.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Category {
    private Long id;
    private String name;
    private LocalDateTime createTime;
}