package com.example.librarysystem.service;

import com.example.librarysystem.entity.Category;
import java.util.List;

public interface CategoryService {
    Category getById(Long id);

    List<Category> getAll();

    void add(String name);

    void update(Long id, String name);

    void delete(Long id);
}