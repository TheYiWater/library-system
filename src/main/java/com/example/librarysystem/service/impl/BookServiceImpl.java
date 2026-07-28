package com.example.librarysystem.service.impl;

import com.example.librarysystem.common.BusinessException;
import com.example.librarysystem.common.PageResult;
import com.example.librarysystem.entity.Book;
import com.example.librarysystem.mapper.BookMapper;
import com.example.librarysystem.service.BookService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;

    public BookServiceImpl(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    @Override
    public Book getById(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new BusinessException(404, "图书不存在");
        }
        return book;
    }

    @Override
    public PageResult<Book> search(String title, String author, int page, int size) {
        // 手动分页：先查总数，再查当前页数据
        // 这里为了简化，我们直接用 MyBatis 查，后面可以集成 PageHelper 分页插件
        List<Book> list = bookMapper.selectByCondition(title, author);
        long total = list.size();

        // 手动分页截取（简单实现）
        int from = (page - 1) * size;
        int to = Math.min(from + size, list.size());
        if (from >= list.size()) {
            list = java.util.Collections.emptyList();
        } else {
            list = list.subList(from, to);
        }

        return new PageResult<>(total, list, page, size);
    }

    @Override
    public void add(Book book) {
        if (book.getStock() == null) book.setStock(0);
        if (book.getTotal() == null) book.setTotal(book.getStock());
        bookMapper.insert(book);
    }

    @Override
    public void delete(Long id) {
        int rows = bookMapper.deleteById(id);
        if (rows == 0) {
            throw new BusinessException(404, "图书不存在");
        }
    }
}