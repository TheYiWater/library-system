package com.example.librarysystem.controller;

import com.example.librarysystem.common.PageResult;
import com.example.librarysystem.common.Result;
import com.example.librarysystem.entity.Book;
import com.example.librarysystem.service.BookService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/{id}")
    public Result<Book> getById(@PathVariable Long id) {
        return Result.success(bookService.getById(id));
    }

    @GetMapping
    public Result<PageResult<Book>> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(bookService.search(title, author, page, size));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Book book) {
        bookService.add(book);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return Result.success(null);
    }
}