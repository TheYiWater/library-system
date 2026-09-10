package com.example.librarysystem.config;

import com.example.librarysystem.mapper.BookMapper;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 布隆过滤器:应用启动时加载所有图书 ID,防止缓存穿透
 */
@Configuration
public class BloomFilterConfig {

    private final BookMapper bookMapper;
    private BloomFilter<Long> bloomFilter;

    public BloomFilterConfig(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    @PostConstruct
    public void init() {
        // 预计 1000 本书,误判率 1%
        bloomFilter = BloomFilter.create(Funnels.longFunnel(), 1000, 0.01);
        List<Long> ids = bookMapper.selectAllIds();
        for (Long id : ids) {
            bloomFilter.put(id);
        }
    }

    public boolean mightContain(Long id) {
        return bloomFilter.mightContain(id);
    }

    public void put(Long id) {
        bloomFilter.put(id);
    }
}