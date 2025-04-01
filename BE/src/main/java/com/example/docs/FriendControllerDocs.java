package com.example.docs;

import com.example.model.dto.FriendDto;
import com.example.model.dto.FriendRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface FriendControllerDocs {

    @Operation(
            tags = "친구",
            summary = "해당 유저의 친구 목록을 조회합니다.",
            description =
                    "PathVariable에 해당하는 유저의 친구 목록을 조회합니다."

    )
    public ResponseEntity<List<FriendDto>> searchFriend(@PathVariable int userId);


    @Operation(
            summary = "해당 유저에게 친구 추가 요청을 보냅니다.",
            description =
                    "아래는 요청 형식 예시입니다..\n\n" +
                            "```json\n" +
                            "{\n" +
                            "    \"userId\": 224,\n" +
                            "    \"friendId\": 300,\n" +
                            "}\n" +
                            "```\n"
    )
    public ResponseEntity<Boolean> requestFriends(@RequestBody FriendRequestDto friendRequestDto);

    @Operation(
            summary = "해당 친구 요청을 수락 / 거절 합니다.",
            description =
                    "아래는 요청 형식 예시입니다..\n\n" +
                            "```json\n" +
                            "{\n" +
                            "    \"userId\": 224,\n" +
                            "    \"friendId\": 300,\n" +
                            "    \"status\": \"BLOCKED\" or \"ACCEPTED\"\n" +
                            "}\n" +
                            "```\n"
    )
    public ResponseEntity<Boolean> requestUpdate(@RequestBody FriendDto nFriendDto);
}
