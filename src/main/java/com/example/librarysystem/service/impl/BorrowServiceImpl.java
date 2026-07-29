package com.example.librarysystem.service.impl;

import com.example.librarysystem.common.BusinessException;
import com.example.librarysystem.entity.Book;
import com.example.librarysystem.entity.BorrowRecord;
import com.example.librarysystem.mapper.BookMapper;
import com.example.librarysystem.mapper.BorrowRecordMapper;
import com.example.librarysystem.service.BorrowService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BorrowServiceImpl implements BorrowService {

    private final BookMapper bookMapper;
    private final BorrowRecordMapper borrowRecordMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String BOOK_KEY = "book:detail:";

    public BorrowServiceImpl(BookMapper bookMapper, BorrowRecordMapper borrowRecordMapper,
                             RedisTemplate<String, Object> redisTemplate) {
        this.bookMapper = bookMapper;
        this.borrowRecordMapper = borrowRecordMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void borrow(Long userId, Long bookId) {
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new BusinessException(404, "图书不存在");
        }
        int rows = bookMapper.updateStock(bookId, -1);
        if (rows == 0) {
            throw new BusinessException(400, "库存不足，无法借阅");
        }
        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setDueTime(LocalDateTime.now().plusDays(30));
        record.setStatus(0);
        borrowRecordMapper.insert(record);

        // 库存变了，删除这本书的缓存（让下一次查 DB 最新值）
        redisTemplate.delete(BOOK_KEY + bookId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnBook(Long userId, Long bookId, Long recordId) {
        int rows = borrowRecordMapper.markReturned(recordId, userId);
        if (rows == 0) {
            throw new BusinessException(400, "借阅记录不存在或非本人");
        }
        bookMapper.updateStock(bookId, 1);

        // 库存变了，删除这本书的缓存
        redisTemplate.delete(BOOK_KEY + bookId);
    }

    @Override
    public List<BorrowRecord> getUserRecords(Long userId) {
        return borrowRecordMapper.selectByUserId(userId);
    }
}