package com.example.librarysystem.service.impl;

import com.example.librarysystem.common.BusinessException;
import com.example.librarysystem.entity.Book;
import com.example.librarysystem.entity.BorrowRecord;
import com.example.librarysystem.mapper.BookMapper;
import com.example.librarysystem.mapper.BorrowRecordMapper;
import com.example.librarysystem.service.BorrowService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BorrowServiceImpl implements BorrowService {

    private final BookMapper bookMapper;
    private final BorrowRecordMapper borrowRecordMapper;

    public BorrowServiceImpl(BookMapper bookMapper, BorrowRecordMapper borrowRecordMapper) {
        this.bookMapper = bookMapper;
        this.borrowRecordMapper = borrowRecordMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void borrow(Long userId, Long bookId) {
        // 1. 检查图书是否存在
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new BusinessException(404, "图书不存在");
        }

        // 2. 乐观更新库存（带条件，防超卖）
        int rows = bookMapper.updateStock(bookId, -1);
        if (rows == 0) {
            throw new BusinessException(400, "库存不足，无法借阅");
        }

        // 3. 写借阅记录
        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setDueTime(LocalDateTime.now().plusDays(30));
        record.setStatus(0);
        borrowRecordMapper.insert(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnBook(Long userId, Long bookId, Long recordId) {
        // 1. 更新借阅记录状态
        int rows = borrowRecordMapper.markReturned(recordId, userId);
        if (rows == 0) {
            throw new BusinessException(400, "借阅记录不存在或非本人");
        }

        // 2. 库存 +1
        bookMapper.updateStock(bookId, 1);
    }

    @Override
    public List<BorrowRecord> getUserRecords(Long userId) {
        return borrowRecordMapper.selectByUserId(userId);
    }
}