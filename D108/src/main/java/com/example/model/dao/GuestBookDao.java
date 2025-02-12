package com.example.model.dao;


import com.example.model.dto.GuestBookDto;
import com.example.model.dto.GuestBookRequestDto;
import org.apache.ibatis.annotations.*;

@Mapper
public interface GuestBookDao {
    /**
     * 방명록 작성
     * @param guestBookRequestDto, 작성할 방명록 데이터
     * @return 삽입된 행의 수
     */
    int insertGuestBook(GuestBookRequestDto guestBookRequestDto);

    /**
     * @param guestBookDto, 수정할 방명록 데이터
     * @return 수정한 행의 수
     */
    int updateGuestBook(GuestBookDto guestBookDto);

    /**
     * 방명록 삭제
     * @param guestBookId, 삭제할 방명록의 고유 아이디
     * @return 삭제된 행의 수
     */
    int deleteGuestBook(@Param("guestBookId") int guestBookId);
}

