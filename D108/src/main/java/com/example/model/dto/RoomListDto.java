package com.example.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;


@Setter
@Getter
@NoArgsConstructor
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

    public enum RoomStatus{
        WAIT,
        PLAY;
    }

    public RoomListDto(int roomId, int hostId, String roomName, RoomStatus status, int maxPlayers, int nowPlayers, String createdAt) {
        this.roomId = roomId;
        this.hostId = hostId;
        this.roomName = roomName;
        this.status = status;
        this.maxPlayers = maxPlayers;
        this.nowPlayers = nowPlayers;
        this.createdAt = createdAt;
    }
}
