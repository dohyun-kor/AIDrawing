package com.example.model.dto;

import com.example.model.Enum.LEVEL;
import com.example.model.Enum.MODE;
import com.example.model.Enum.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SelectRoomDto {

    private int roomId;
    private int hostId;
    private String roomName;
    private RoomStatus status;
    private int nowPlayers;
    private int maxPlayers;
    private int rounds;
    private MODE mode;
    private LEVEL level;
    private int roundTime;

    // RoomDto로부터 SelectRoomDto를 생성하는 생성자
    public SelectRoomDto(RoomDto roomDto, int nowPlayers) {
        this.roomId = roomDto.getRoomId();
        this.hostId = roomDto.getHostId();
        this.roomName = roomDto.getRoomName();
        this.status = roomDto.getStatus();
        this.maxPlayers = roomDto.getMaxPlayers();
        this.rounds = roomDto.getRounds();
        this.mode = roomDto.getMode();
        this.level = roomDto.getLevel();
        this.roundTime = roomDto.getRoundTime();
        this.nowPlayers = nowPlayers;
    }
}
