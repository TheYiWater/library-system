package com.example.librarysystem.mapper;

import com.example.librarysystem.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CategoryMapper {
    Category selectById(Long id);

    List<Category> selectAll();

    int insert(Category category);

    int update(@Param("id") Long id, @Param("name") String name);

    int deleteById(Long id);
}