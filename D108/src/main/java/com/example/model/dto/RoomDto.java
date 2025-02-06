package com.example.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class RoomDto {
    private int hostId;
    private String roomName;
    private RoomStatus status;
    private int maxPlayers;
    private int nowPlayers;

    private enum RoomStatus{
        WAIT,
        PLAY;
    }

    public RoomDto(int hostId, String roomName, RoomStatus status, int maxPlayers, int nowPlayers) {
        this.hostId = hostId;
        this.roomName = roomName;
        this.status = status;
        this.maxPlayers = maxPlayers;
        this.nowPlayers = nowPlayers;
    }

    @Override
    public String toString() {
        return "Room{" +
                "hostId=" + hostId +
                ", roomName='" + roomName + '\'' +
                ", status=" + status +
                ", maxPlayers=" + maxPlayers +
                ", nowPlayers=" + nowPlayers +
                '}';
    }
}
