package com.example.librarysystem.mapper;

import com.example.librarysystem.entity.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface BookMapper {
    Book selectById(Long id);

    List<Book> selectByCondition(@Param("title") String title, @Param("author") String author);

    int insert(Book book);

    int update(Book book);

    int updateStock(@Param("id") Long id, @Param("delta") int delta);

    int deleteById(Long id);

    List<Long> selectAllIds();

    int count();

    List<Book> listAll();
}