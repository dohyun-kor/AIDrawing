package com.example.docs;

import com.example.model.dto.Friend;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface FriendControllerDocs {

    @Operation(
            summary = "해당 유저의 친구 목록을 조회합니다.",
            description =
                    "아래는 요청 형식 예시입니다..\n\n" +
                            "```json\n" +
                            "{\n" +
                            "    \"userId\": 224,\n" +
                            "}\n" +
                            "```\n"
    )

    public ResponseEntity<List<Friend>> searchFriend(@RequestParam int userId);

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
    public boolean requestFriends(@RequestParam int userId, @RequestParam int friendId);
}
