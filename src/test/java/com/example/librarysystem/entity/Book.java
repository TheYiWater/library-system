package com.example.librarysystem.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Book {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private Long categoryId;
    private String publisher;
    private Integer stock;
    private Integer total;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}