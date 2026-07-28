package com.example.librarysystem.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BorrowRecord {
    private Long id;
    private Long userId;
    private Long bookId;
    private LocalDateTime borrowTime;
    private LocalDateTime dueTime;
    private LocalDateTime returnTime;
    private Integer status;
}