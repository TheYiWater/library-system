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
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BorrowServiceImpl implements BorrowService {

    private static final String BOOK_KEY = "book:detail:";

    private final BookMapper bookMapper;
    private final BorrowRecordMapper borrowRecordMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public BorrowServiceImpl(BookMapper bookMapper,
                             BorrowRecordMapper borrowRecordMapper,
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
            throw new BusinessException(400, "库存不足,无法借阅");
        }
        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setDueTime(LocalDateTime.now().plusDays(30));
        record.setStatus(0);
        borrowRecordMapper.insert(record);
        redisTemplate.delete(BOOK_KEY + bookId);
        redisTemplate.delete("books:hot:ranking");  // ← 加这一行
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnBook(Long userId, Long recordId) {
        // 先查借阅记录,拿到 bookId,同时校验记录归属
        BorrowRecord record = borrowRecordMapper.selectById(recordId);
        if (record == null) {
            throw new BusinessException(404, "借阅记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            throw new BusinessException(400, "无权操作他人借阅记录");
        }
        if (record.getStatus() == 1) {
            throw new BusinessException(400, "该记录已归还");
        }
        Long bookId = record.getBookId();

        // 1. 标记归还
        int rows = borrowRecordMapper.markReturned(recordId, userId);
        if (rows == 0) {
            throw new BusinessException(400, "归还失败");
        }

        // 2. 库存 +1
        bookMapper.updateStock(bookId, 1);

        // 3. 删缓存
        redisTemplate.delete(BOOK_KEY + bookId);
        redisTemplate.delete("books:hot:ranking");  // ← 加这一行
    }

    @Override
    public List<BorrowRecord> getUserRecords(Long userId) {
        List<BorrowRecord> records = borrowRecordMapper.selectByUserId(userId);
        // 补书名到扩展字段
        for (BorrowRecord r : records) {
            if (r.getBookId() == null) continue;
            Book book = bookMapper.selectById(r.getBookId());
            if (book != null) {
                r.setBookTitle(book.getTitle());
            }
        }
        return records;
    }
    @Override
    public Map<String, Long> getUserStats(Long userId) {
        List<BorrowRecord> records = borrowRecordMapper.selectByUserId(userId);
        long total = records.size();
        long borrowing = records.stream().filter(r -> r.getStatus() == 0).count();
        long returned = records.stream().filter(r -> r.getStatus() == 1).count();
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("borrowing", borrowing);
        stats.put("returned", returned);
        return stats;
    }
}