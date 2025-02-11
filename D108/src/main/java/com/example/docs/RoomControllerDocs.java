package com.example.docs;

import com.example.model.dto.RoomDto;
import com.example.model.dto.RoomListDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface RoomControllerDocs {
    @Operation(
            summary = "방을 생성 합니다. 반환 값은 생성된 방의 roomId입니다.",
            description =
                    "아래는 요청 형식 예시입니다..\n\n" +
                            "```json\n" +
                            "{\n" +
                            "    \"roomId\": 0, <- 이거는 0값으로 그냥 던져주셔도 됩니다.\n" +
                            "    \"hostId\": 1,\n" +
                            "    \"roomName\": \"피카소의 재림\",\n" +
                            "    \"status\": \"WAIT\",\n" +
                            "    \"maxPlayers\": 4,\n" +
                            "    \"rounds\": 1,\n" +
                            "    \"mode\": USER or AI,\n" +
                            "    \"level\": EASY or NORMAL or HARD,\n" +
                            "    \"roundTime\": 60,\n" +
                            "}\n" +
                            "```\n"
    )
    public ResponseEntity<Integer> createRoom(@RequestBody RoomDto roomDto);

    @Operation(
            summary = "roomId에 해당하는 방의 정보를 변경 합니다.",
            description =
                    "아래는 요청 형식 예시입니다..\n\n" +
                            "```json\n" +
                            "{\n" +
                            "    \"roomId\": 0, <- 이거는 0값으로 그냥 던져주셔도 됩니다.\n" +
                            "    \"hostId\": 2,\n" +
                            "    \"roomName\": \"피카소의 재림2\",\n" +
                            "    \"status\": \"PLAY\",\n" +
                            "    \"maxPlayers\": 6,\n" +
                            "    \"rounds\": 1,\n" +
                            "    \"mode\": USER or AI,\n" +
                            "    \"level\": EASY or NORMAL or HARD,\n" +
                            "    \"roundTime\": 60,\n" +
                            "}\n" +
                            "```\n"
    )
    public ResponseEntity<Boolean> updateRoom(@PathVariable int roomId, @RequestBody RoomDto roomDto);

    @Operation(
            summary = "현재 존재하는 모든 게임 방을 조회합니다",
            description =
                    "모든 방의 정보를 확인할 수 있습니다."
    )
    public ResponseEntity<List<RoomListDto>> searchRoom();

    @Operation(
            summary = "roomId에 대한 게임 방 정보 조회합니다"
    )
    public ResponseEntity<RoomDto> selectRoom(@PathVariable int roomId);
}
