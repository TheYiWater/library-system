package com.example.librarysystem.service;

import com.example.librarysystem.entity.BorrowRecord;
import java.util.List;
import java.util.Map;

public interface BorrowService {

    void borrow(Long userId, Long bookId);

    /**
     * 归还图书
     */
    void returnBook(Long userId, Long recordId);

    List<BorrowRecord> getUserRecords(Long userId);

    Map<String, Long> getUserStats(Long userId);
}