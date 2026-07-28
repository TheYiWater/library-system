package com.example.librarysystem.service;

import com.example.librarysystem.entity.BorrowRecord;

import java.util.List;

public interface BorrowService {
    void borrow(Long userId, Long bookId);

    void returnBook(Long userId, Long bookId, Long recordId);

    List<BorrowRecord> getUserRecords(Long userId);
}