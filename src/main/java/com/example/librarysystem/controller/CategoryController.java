package com.example.librarysystem.controller;

import com.example.librarysystem.common.Result;
import com.example.librarysystem.entity.Category;
import com.example.librarysystem.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/{id}")
    public Result<Category> getById(@PathVariable Long id) {
        return Result.success(categoryService.getById(id));
    }

    @GetMapping
    public Result<List<Category>> getAll() {
        return Result.success(categoryService.getAll());
    }

    @PostMapping
    public Result<Void> add(@RequestParam String name) {
        categoryService.add(name);
        return Result.success(null);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestParam String name) {
        categoryService.update(id, name);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success(null);
    }
}