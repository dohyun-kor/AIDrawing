package com.example.model.dto;

import java.io.Serializable;
import java.util.Objects;

public class Friend implements Serializable {

    private static final long serialVersionUID = 1L;

    private int userId;
    private int friendId;
    private FriendStatus status;

    // Enum to represent the friend status values
    public enum FriendStatus {
        PENDING,    // 'pending'
        ACCEPTED,   // 'accepted'
        BLOCKED;    // 'blocked'

        // Optional: 메타 데이터를 다루기 위한 문자열 반환 (DB 값과 매핑)
        @Override
        public String toString() {
            return name().toLowerCase();
        }

        public static FriendStatus fromString(String status) {
            if (status != null) {
                for (FriendStatus fs : FriendStatus.values()) {
                    if (status.equalsIgnoreCase(fs.toString())) {
                        return fs;
                    }
                }
            }
            throw new IllegalArgumentException("Unknown status: " + status);
        }
    }

    // 기본 생성자
    public Friend() {
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

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFriendId() {
        return friendId;
    }

    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }

    public FriendStatus getStatus() {
        return status;
    }

    public void setStatus(FriendStatus status) {
        this.status = status;
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
