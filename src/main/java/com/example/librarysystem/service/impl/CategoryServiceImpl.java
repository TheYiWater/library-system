package com.example.librarysystem.service.impl;

import com.example.librarysystem.common.BusinessException;
import com.example.librarysystem.entity.Category;
import com.example.librarysystem.mapper.CategoryMapper;
import com.example.librarysystem.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public Category getById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }
        return category;
    }

    @Override
    public List<Category> getAll() {
        return categoryMapper.selectAll();
    }

    @Override
    public void add(String name) {
        Category category = new Category();
        category.setName(name);
        categoryMapper.insert(category);
    }

    @Override
    public void update(Long id, String name) {
        int rows = categoryMapper.update(id, name);
        if (rows == 0) {
            throw new BusinessException(404, "分类不存在");
        }
    }

    @Override
    public void delete(Long id) {
        int rows = categoryMapper.deleteById(id);
        if (rows == 0) {
            throw new BusinessException(404, "分类不存在");
        }
    }
}