package com.example.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserDto {
    private int userId;
    private String email;
    private String id;
    private String password;
    private String nickname;
    private int point;
    private int gamesWon;
    private int totalGames;
    private int level;
    private int exp;
    private String createdAt;

    public UserDto(String email, String id, String password, String nickname) {
        this.email = email;
        this.id = id;
        this.password = password;
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", id='" + id + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", point=" + point +
                ", gamesWon=" + gamesWon +
                ", totalGames=" + totalGames +
                ", level=" + level +
                ", exp=" + exp +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
