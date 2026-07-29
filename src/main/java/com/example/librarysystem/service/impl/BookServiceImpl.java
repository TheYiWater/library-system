package com.example.librarysystem.service.impl;

import com.example.librarysystem.common.BusinessException;
import com.example.librarysystem.common.PageResult;
import com.example.librarysystem.entity.Book;
import com.example.librarysystem.mapper.BookMapper;
import com.example.librarysystem.service.BookService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String BOOK_KEY = "book:detail:";
    private static final long CACHE_EXPIRE = 30; // 30 分钟

    public BookServiceImpl(BookMapper bookMapper, RedisTemplate<String, Object> redisTemplate) {
        this.bookMapper = bookMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Book getById(Long id) {
        String key = BOOK_KEY + id;

        // 1. 先查缓存
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj != null) {
            return (Book) obj;
        }

        // 2. 缓存没命中，查数据库
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new BusinessException(404, "图书不存在");
        }

        // 3. 回写缓存（带过期时间）
        redisTemplate.opsForValue().set(key, book, CACHE_EXPIRE, TimeUnit.MINUTES);

        return book;
    }

    @Override
    public PageResult<Book> search(String title, String author, int page, int size) {
        // 搜索场景比较复杂，这里先不缓存，直接查 DB
        List<Book> list = bookMapper.selectByCondition(title, author);
        long total = list.size();
        int from = (page - 1) * size;
        int to = Math.min(from + size, list.size());
        if (from >= list.size()) {
            list = Collections.emptyList();
        } else {
            list = list.subList(from, to);
        }
        return new PageResult<>(total, list, page, size);
    }

    @Override
    public void add(Book book) {
        if (book.getStock() == null) book.setStock(0);
        if (book.getTotal() == null) book.setTotal(book.getStock());
        bookMapper.insert(book);
        // 新增后不用加缓存，等第一次查询时再放，避免浪费内存
    }

    @Override
    public void delete(Long id) {
        int rows = bookMapper.deleteById(id);
        if (rows == 0) {
            throw new BusinessException(404, "图书不存在");
        }
        // 主动删除缓存，防止脏数据（如果还保留旧缓存，用户就会读到过期数据）
        String key = BOOK_KEY + id;
        redisTemplate.delete(key);
    }
}