package com.example.librarysystem.service;

import java.util.List;
import java.util.Map;

public interface StatisticsService {
    List<Map<String, Object>> getHotBooks();
}