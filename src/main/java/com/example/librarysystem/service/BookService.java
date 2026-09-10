package com.example.librarysystem.service;

import com.example.librarysystem.common.PageResult;
import com.example.librarysystem.entity.Book;

public interface BookService {
    Book getById(Long id);

    PageResult<Book> search(String title, String author, int page, int size);

    void add(Book book);

    void update(Book book);

    void delete(Long id);
}