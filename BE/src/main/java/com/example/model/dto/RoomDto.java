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


    public RoomDto(int hostId, String roomName, RoomStatus status, int maxPlayers) {
        this.hostId = hostId;
        this.roomName = roomName;
        this.status = status;
        this.maxPlayers = maxPlayers;
    }
}
