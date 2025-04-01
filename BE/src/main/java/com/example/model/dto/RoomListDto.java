package com.example.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoomListDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int roomId;
    private int hostId;
    private String roomName;
    private RoomStatus status;
    private int maxPlayers;
    private int nowPlayers;
    private String createdAt;
    private int rounds;
    private MODE mode;
    private LEVEL level;
    private int roundTime;

    private enum RoomStatus{
        WAIT,
        PLAY;
    }
    private enum MODE{
        USER,
        AI;
    }

    private enum LEVEL{
        EASY,
        NORMAL,
        HARD;
    }

}
