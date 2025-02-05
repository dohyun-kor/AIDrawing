package com.example.controller;

import com.example.docs.RoomControllerDocs;
import com.example.model.dto.Room;
import com.example.model.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/room")
public class RoomController implements RoomControllerDocs {

    @Autowired
    RoomService roomService;

    @PostMapping("")
    public ResponseEntity<Boolean> createRoom(Room room){
        int result = 0;

        try{
            result = roomService.createRoom(room);
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
            @RequestBody Room room
    ) {
        int result = 0;
        try {
            // roomId와 함께 방 정보를 받아, 해당 ID에 해당하는 방을 업데이트
            result = roomService.updateRoom(roomId, room);
        } catch (Exception e){
            e.printStackTrace();
        }

        if(result == 1){
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
