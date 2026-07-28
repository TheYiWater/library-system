package com.example.librarysystem.mapper;

import com.example.librarysystem.entity.BorrowRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface BorrowRecordMapper {
    BorrowRecord selectById(Long id);

    List<BorrowRecord> selectByUserId(@Param("userId") Long userId);

    List<BorrowRecord> selectByBookId(@Param("bookId") Long bookId);

    int insert(BorrowRecord record);

    int markReturned(@Param("id") Long id, @Param("userId") Long userId);
}