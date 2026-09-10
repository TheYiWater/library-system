package com.example.librarysystem.service;

import java.util.List;
import java.util.Map;

public interface AdminService {
    Map<String, Object> getStats();
    List<?> getAllUsers();
    void updateUserStatus(Long id, Integer status);
    void updateUserRole(Long id, Integer role, Long operatorId);

    List<Map<String, Object>> getAllRecords();
}