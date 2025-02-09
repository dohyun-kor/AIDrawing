package com.example.controller;

import com.example.docs.RoomControllerDocs;
import com.example.model.dto.RoomDto;
import com.example.model.dto.RoomListDto;
import com.example.model.service.RoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "게임 방", description = "게임 방 관련 API")
@RestController
@RequestMapping("/room")
public class RoomController implements RoomControllerDocs {

    @Autowired
    RoomService roomService;

    @PostMapping("")
    public ResponseEntity<Boolean> createRoom(RoomDto roomDto){
        int result = 0;

        try{
            result = roomService.createRoom(roomDto);
        }catch(Exception e){
            e.printStackTrace();
        }

        if(result == 1){
            return ResponseEntity.ok(true);
        }else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<Boolean> updateRoom(
            @PathVariable int roomId,
            @RequestBody RoomDto roomDto
    ) {
        int result = 0;
        try {
            // roomId와 함께 방 정보를 받아, 해당 ID에 해당하는 방을 업데이트
            result = roomService.updateRoom(roomId, roomDto);
        } catch (Exception e){
            e.printStackTrace();
        }

        if(result == 1){
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<RoomListDto>> searchRoom() {
        try {
            List<RoomListDto> roomList = roomService.searchRoom();
            return ResponseEntity.ok(roomList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
