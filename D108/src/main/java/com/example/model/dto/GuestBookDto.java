package com.example.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class GuestBookDto {

    // 방명록 고유 아이디
    private int guestBookId;
    // 마이룸 소유자 사용자 아이디
    private int userId;
    // 방명록 작성자 아이디
    private int writerId;
    // 방명록 내용
    private String content;
    // 별점
    private float score;

    public GuestBookDto(int guestBookId, int userId, int writerId, String content, float score) {
        this.guestBookId = guestBookId;
        this.userId = userId;
        this.writerId = writerId;
        this.content = content;
        this.score = score;
    }
}
