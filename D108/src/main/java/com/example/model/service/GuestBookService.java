package com.example.model.service;

import com.example.model.dto.GuestBookDto;
import com.example.model.dto.GuestBookRequestDto;

public interface GuestBookService {
    /**
     * 방명록을 작성한다.
     * @param guestBookRequestDto 작성할 방명록 데이터
     * @return 삽입된 행의 수 (정상 처리 시 1)
     */
    int createGuestBook(GuestBookRequestDto guestBookRequestDto);

    /**
     * 방명록 수정
     * @param guestBookDto
     * @return 수정된 행의 수
     */
    int updateGuestBook(GuestBookDto guestBookDto);

    /**
     * 방명록을 삭제한다.
     * @param guestBookId 삭제할 방명록의 고유 아이디
     * @return 삭제된 행의 수 (정상 처리 시 1)
     */
    int deleteGuestBook(int guestBookId);
}
