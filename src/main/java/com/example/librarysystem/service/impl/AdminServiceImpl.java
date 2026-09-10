package com.example.librarysystem.service.impl;

import com.example.librarysystem.common.BusinessException;
import com.example.librarysystem.mapper.BookMapper;
import com.example.librarysystem.mapper.BorrowRecordMapper;
import com.example.librarysystem.mapper.UserMapper;
import com.example.librarysystem.service.AdminService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    private final BookMapper bookMapper;
    private final UserMapper userMapper;
    private final BorrowRecordMapper borrowRecordMapper;

    public AdminServiceImpl(BookMapper bookMapper, UserMapper userMapper, BorrowRecordMapper borrowRecordMapper) {
        this.bookMapper = bookMapper;
        this.userMapper = userMapper;
        this.borrowRecordMapper = borrowRecordMapper;
    }

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("bookTotal", bookMapper.count());
        stats.put("userTotal", userMapper.count());
        stats.put("borrowTotal", borrowRecordMapper.count());
        stats.put("overdue", borrowRecordMapper.countOverdue());
        return stats;
    }

    @Override
    public List<?> getAllUsers() {
        return userMapper.listAll();
    }

    @Override
    public void updateUserStatus(Long id, Integer status) {
        // 超级管理员保护
        if (id == 1) {
            throw new BusinessException(400, "超级管理员不可封禁");
        }
        userMapper.updateStatus(id, status);
    }

    @Override
    public void updateUserRole(Long targetId, Integer role, Long operatorId) {
        // 超级管理员保护
        if (targetId == 1) {
            throw new BusinessException(400, "超级管理员不可修改");
        }
        // 不能改自己的权限
        if (targetId.equals(operatorId)) {
            throw new BusinessException(400, "不能修改自己的权限");
        }
        // 如果要把管理员降为普通用户，检查系统还剩没有管理员
        if (role == 0) {
            Long adminCount = userMapper.countAdmins();
            if (adminCount <= 1) {
                throw new BusinessException(400, "系统至少需要保留一个管理员");
            }
        }
        userMapper.updateRole(targetId, role);
    }

    @Override
    public List<Map<String, Object>> getAllRecords() {
        return borrowRecordMapper.selectAllWithBook();
    }
}