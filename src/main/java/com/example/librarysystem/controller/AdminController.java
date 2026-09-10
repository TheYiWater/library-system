package com.example.librarysystem.controller;

import com.example.librarysystem.common.Result;
import com.example.librarysystem.service.AdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * 仪表盘统计
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats(@RequestAttribute("userId") Long userId,
                                             @RequestAttribute("role") Integer role) {
        if (role == null || role != 1) {
            return Result.error(403, "无权访问");
        }
        return Result.success(adminService.getStats());
    }

    /**
     * 用户列表
     */
    @GetMapping("/users")
    public Result<?> users(@RequestAttribute("role") Integer role) {
        if (role == null || role != 1) {
            return Result.error(403, "无权访问");
        }
        return Result.success(adminService.getAllUsers());
    }

    /**
     * 封禁/解封用户
     */
    @PutMapping("/users/{id}/status")
    public Result<Void> updateStatus(@RequestAttribute("role") Integer role,
                                  @PathVariable Long id,
                                  @RequestParam Integer status) {
        if (role == null || role != 1) {
            return Result.error(403, "无权访问");
        }
        adminService.updateUserStatus(id, status);
        return Result.success(null);
    }

    /**
     * 切换用户角色
     */
    @PutMapping("/users/{id}/role")
    public Result<Void> updateRole(@RequestAttribute("role") Integer role,
                                   @RequestAttribute("userId") Long operatorId,
                                   @PathVariable Long id,
                                   @RequestParam("role") Integer roleValue) {
        if (role == null || role != 1) {
            return Result.error(403, "无权访问");
        }
        adminService.updateUserRole(id, roleValue, operatorId);
        return Result.success(null);
    }

    /**
     * 所有借阅记录（管理员）
     */
    @GetMapping("/records")
    public Result<List<Map<String, Object>>> allRecords(@RequestAttribute("role") Integer role) {
        if (role == null || role != 1) {
            return Result.error(403, "无权访问");
        }
        return Result.success(adminService.getAllRecords());
    }
}