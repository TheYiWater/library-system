package com.example.librarysystem.controller;
import java.util.Map;
import com.example.librarysystem.common.Result;
import com.example.librarysystem.entity.BorrowRecord;
import com.example.librarysystem.service.BorrowService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow")
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    /**
     * 借阅图书
     * POST /api/borrow/{bookId}
     * bookId 从路径取,userId 从 JWT 拦截器 request 属性取
     */
    @PostMapping("/{bookId}")
    public Result<Void> borrow(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long bookId) {
        borrowService.borrow(userId, bookId);
        return Result.success("借阅成功", null);
    }

    /**
     * 归还图书
     * POST /api/borrow/return/{recordId}
     * 因为还书只需要 recordId 和 userId 就能定位,不再强制传 bookId
     */
    @PostMapping("/return/{recordId}")
    public Result<Void> returnBook(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long recordId) {
        // 从借阅记录里查 bookId,传进 service
        borrowService.returnBook(userId, recordId);
        return Result.success("归还成功", null);
    }

    /**
     * 我的借阅记录
     * GET /api/borrow/my
     */
    @GetMapping("/my")
    public Result<List<BorrowRecord>> getUserRecords(@RequestAttribute("userId") Long userId) {
        return Result.success(borrowService.getUserRecords(userId));
    }
    /**
     * 我的借阅统计
     */
    @GetMapping("/stats")
    public Result<Map<String, Long>> stats(@RequestAttribute("userId") Long userId) {
        return Result.success(borrowService.getUserStats(userId));
    }
}