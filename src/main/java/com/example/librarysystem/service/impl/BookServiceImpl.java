package com.example.librarysystem.service.impl;

import com.example.librarysystem.common.BusinessException;
import com.example.librarysystem.common.PageResult;
import com.example.librarysystem.config.BloomFilterConfig;
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
    private final BloomFilterConfig bloomFilterConfig;

    private static final String BOOK_KEY = "book:detail:";
    private static final String NULL_KEY = "book:null:";
    private static final long CACHE_EXPIRE = 30; // 30 分钟
    private static final long NULL_EXPIRE = 5;   // 空值缓存 5 分钟

    public BookServiceImpl(BookMapper bookMapper, RedisTemplate<String, Object> redisTemplate,
                           BloomFilterConfig bloomFilterConfig) {
        this.bookMapper = bookMapper;
        this.redisTemplate = redisTemplate;
        this.bloomFilterConfig = bloomFilterConfig;
    }

    @Override
    public Book getById(Long id) {
        String key = BOOK_KEY + id;
        String nullKey = NULL_KEY + id;

        // 0. 布隆过滤器判断,不存在直接返回(防缓存穿透)
        if (!bloomFilterConfig.mightContain(id)) {
            throw new BusinessException(404, "图书不存在");
        }

        // 1. 先查缓存
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj != null) {
            return (Book) obj;
        }

        // 2. 查空值缓存(防穿透二次保护)
        Object nullObj = redisTemplate.opsForValue().get(nullKey);
        if (nullObj != null) {
            throw new BusinessException(404, "图书不存在");
        }

        // 3. 缓存没命中,查数据库
        Book book = bookMapper.selectById(id);
        if (book == null) {
            // 缓存空值,短 TTL 防穿透
            redisTemplate.opsForValue().set(nullKey, "", NULL_EXPIRE, TimeUnit.MINUTES);
            throw new BusinessException(404, "图书不存在");
        }

        // 4. 回写缓存
        redisTemplate.opsForValue().set(key, book, CACHE_EXPIRE, TimeUnit.MINUTES);
        return book;
    }

    @Override
    public PageResult<Book> search(String title, String author, int page, int size) {
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
        // 新增图书 id 加入布隆过滤器
        bloomFilterConfig.put(book.getId());
    }

    @Override
    public void update(Book book) {
        bookMapper.update(book);
        redisTemplate.delete(BOOK_KEY + book.getId());
    }

    @Override
    public void delete(Long id) {
        int rows = bookMapper.deleteById(id);
        if (rows == 0) {
            throw new BusinessException(404, "图书不存在");
        }
        redisTemplate.delete(BOOK_KEY + id);
        redisTemplate.delete(NULL_KEY + id);
    }
}