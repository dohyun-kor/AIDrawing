package com.example.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {
    private int roomId;
    private int hostId;
    private String roomName;
    private RoomStatus status;
    private int maxPlayers;
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

    public RoomDto(int hostId, String roomName, RoomStatus status, int maxPlayers) {
        this.hostId = hostId;
        this.roomName = roomName;
        this.status = status;
        this.maxPlayers = maxPlayers;
    }

    @Override
    public String toString() {
        return "Room{" +
                "hostId=" + hostId +
                ", roomName='" + roomName + '\'' +
                ", status=" + status +
                ", maxPlayers=" + maxPlayers +
                '}';
    }
}
