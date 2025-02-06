package com.example.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class Friend implements Serializable {

    private static final long serialVersionUID = 1L;

    // Getters and Setters
    private int userId;
    private int friendId;
    private FriendStatus status;

    // Enum to represent the friend status values
    public enum FriendStatus {
        PENDING,    // 'pending'
        ACCEPTED,   // 'accepted'
        BLOCKED;    // 'blocked'
    }

    // 전체 필드 생성자
    public Friend(int userId, int friendId, FriendStatus status) {
        this.userId = userId;
        this.friendId = friendId;
        this.status = status;
    }
    public Friend(int userId, int friendId) {
        this.userId = userId;
        this.friendId = friendId;
        this.status = FriendStatus.PENDING;
    }

    // Optional: equals 및 hashCode 재정의 (Primary Key: userId, friendId)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Friend friendDTO)) return false;
        return userId == friendDTO.userId &&
                friendId == friendDTO.friendId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, friendId);
    }

    @Override
    public String toString() {
        return "Friend{" +
                "userId=" + userId +
                ", friendId=" + friendId +
                ", status=" + status +
                '}';
    }
}
