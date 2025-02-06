package com.example.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class FriendRequestDto {

    private int userId;
    private int friendId;

    public FriendRequestDto(int userId, int friendId) {
        this.userId = userId;
        this.friendId = friendId;
    }

    @Override
    public String toString() {
        return "FriendRequest{" +
                "userId='" + userId + '\'' +
                ", friendId='" + friendId + '\'' +
                '}';
    }
}
