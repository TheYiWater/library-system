package com.example.librarysystem.service.impl;

import com.example.librarysystem.mapper.BorrowRecordMapper;
import com.example.librarysystem.service.StatisticsService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 统计服务:定时任务统计热门借阅图书 Top10,结果缓存到 Redis
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    private static final String HOT_BOOKS_KEY = "books:hot:ranking";
    private final BorrowRecordMapper borrowRecordMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public StatisticsServiceImpl(BorrowRecordMapper borrowRecordMapper,
                                 RedisTemplate<String, Object> redisTemplate) {
        this.borrowRecordMapper = borrowRecordMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getHotBooks() {
        Object cached = redisTemplate.opsForValue().get(HOT_BOOKS_KEY);
        if (cached != null) {
            return (List<Map<String, Object>>) cached;
        }
        // 缓存没有,查 DB 并回写
        List<Map<String, Object>> hotBooks = borrowRecordMapper.selectHotBooks();
        redisTemplate.opsForValue().set(HOT_BOOKS_KEY, hotBooks, 1, TimeUnit.HOURS);
        return hotBooks;
    }

    /**
     * 定时任务:每天凌晨 2 点统计热门借阅图书 Top10,结果存 Redis
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void refreshHotBooksRanking() {
        List<Map<String, Object>> hotBooks = borrowRecordMapper.selectHotBooks();
        redisTemplate.opsForValue().set(HOT_BOOKS_KEY, hotBooks, 25, TimeUnit.HOURS);
    }
}