package com.example.model.dto;

public class Room {
    private int hostId;
    private String roomName;
    private RoomStatus status;
    private int maxPlayers;
    private int nowPlayers;

    private enum RoomStatus{
        WAIT,
        PLAY;
    }

    public Room(int hostId, String roomName, RoomStatus status, int maxPlayers, int nowPlayers) {
        this.hostId = hostId;
        this.roomName = roomName;
        this.status = status;
        this.maxPlayers = maxPlayers;
        this.nowPlayers = nowPlayers;
    }

    public int getHostId() {
        return hostId;
    }

    public void setHostId(int hostId) {
        this.hostId = hostId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getNowPlayers() {
        return nowPlayers;
    }

    public void setNowPlayers(int nowPlayers) {
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
