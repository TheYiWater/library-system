package com.example.librarysystem.controller;

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

    @PostMapping
    public Result<Void> borrow(
            @RequestAttribute("userId") Long userId,
            @RequestParam Long bookId) {
        borrowService.borrow(userId, bookId);
        return Result.success("借阅成功", null);
    }

    @PostMapping("/return")
    public Result<Void> returnBook(
            @RequestAttribute("userId") Long userId,
            @RequestParam Long bookId,
            @RequestParam Long recordId) {
        borrowService.returnBook(userId, bookId, recordId);
        return Result.success("归还成功", null);
    }

    @GetMapping("/records")
    public Result<List<BorrowRecord>> getUserRecords(@RequestAttribute("userId") Long userId) {
        return Result.success(borrowService.getUserRecords(userId));
    }
}