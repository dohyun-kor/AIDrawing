package com.example.docs;

import com.example.model.dto.Friend;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface FriendControllerDocs {

    @Operation(summary = "해당 유저의 친구목록을 조회한다."
            , description = "<pre>"
            + "아래는 userId = 224 사용자의 친구목록을 조회하는 샘플코드\n "
            + "{\r\n"
            + "  \"userId\": \"224\",\r\n"
            + "}"
            + "</pre>")
    public ResponseEntity<List<Friend>> searchFriend(@RequestParam int userId);
}
