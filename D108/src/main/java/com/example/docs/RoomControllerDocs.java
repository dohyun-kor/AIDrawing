package com.example.docs;

import com.example.model.dto.Friend;
import com.example.model.dto.Room;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface RoomControllerDocs {
    @Operation(
            summary = "방을 생성 합니다.",
            description =
                    "아래는 요청 형식 예시입니다..\n\n" +
                            "```json\n" +
                            "{\n" +
                            "    \"hostId\": 1,\n" +
                            "    \"roomName\": \"피카소의 재림\",\n" +
                            "    \"status\": \"WAIT\",\n" +
                            "    \"maxPlayers\": 4,\n" +
                            "    \"nowPlayers\": 1,\n" +
                            "}\n" +
                            "```\n"
    )
    public ResponseEntity<Boolean> createRoom(@RequestBody Room room);

    @Operation(
            summary = "roomId에 해당하는 방의 정보를 변경 합니다.",
            description =
                    "아래는 요청 형식 예시입니다..\n\n" +
                            "```json\n" +
                            "{\n" +
                            "    \"hostId\": 2,\n" +
                            "    \"roomName\": \"피카소의 재림2\",\n" +
                            "    \"status\": \"PLAY\",\n" +
                            "    \"maxPlayers\": 6,\n" +
                            "    \"nowPlayers\": 3,\n" +
                            "}\n" +
                            "```\n"
    )
    public ResponseEntity<Boolean> updateRoom(@PathVariable int roomId, @RequestBody Room room);
}
