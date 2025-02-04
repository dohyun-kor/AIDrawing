<<<<<<< PATCH SET (6786b3 fix:입력 변경)
// 최신 버전의 Swagger(OpenAPI 3)를 기준으로 작성합니다.
package com.example.docs;

import com.example.model.dto.User;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserControllerDocs {

    @Operation(
            summary = "회원가입 API",
            description =
                    "회원가입 요청 시에는 아래와 같이 **닉네임, 아이디, 패스워드**를 포함해야 합니다.\n\n" +
                            "```json\n" +
                            "{\n" +
                            "  \"nickname\": \"홍길동\",\n" +
                            "  \"id\": \"user123\",\n" +
                            "  \"password\": \"pass1234\"\n" +
                            "}\n" +
                            "```\n"
    )
    ResponseEntity<Boolean> signup(@RequestBody User user);

    @Operation(
            summary = "로그인 API",
            description =
                    "로그인 요청 시에는 아래와 같이 **아이디와 패스워드**만 포함하면 됩니다.\n\n" +
                            "```json\n" +
                            "{\n" +
                            "  \"id\": \"user123\",\n" +
                            "  \"password\": \"pass1234\"\n" +
                            "}\n" +
                            "```\n"
    )
    ResponseEntity<Integer> login(@RequestBody User user);
}
=======
>>>>>>> BASE      (abb17a A Merge branch 'master' of https://i12d108.p.ssafy.io:8989/a)
