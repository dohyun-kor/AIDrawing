// 파일 경로: src/main/java/com/example/docs/GuestBookControllerDocs.java
package com.example.docs;

import com.example.model.dto.GuestBookDto;
import com.example.model.dto.GuestBookRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface GuestBookControllerDocs {

    /**
     * 방명록 작성 API
     * Request Body에는 작성할 방명록의 userId, writer, content 등의 정보가 포함되어야 합니다.
     */
    @Operation(
            summary = "방명록 등록",
            description = "방명록을 등록합니다.\n\n"
                    + "요청 본문에 작성할 방명록의 정보(userId, writer, content 등)를 포함시켜 요청합니다."
    )
    ResponseEntity<Boolean> createGuestBook(@RequestBody GuestBookRequestDto guestBookRequestDto);

    /**
     * 방명록 수정 API
     * Request Body에는 수정할 방명록의 guestBookId와 함께 수정할 내용(writer, content 등)이 포함되어야 합니다.
     */
    @Operation(
            summary = "방명록 수정",
            description = "방명록의 내용을 수정합니다.\n\n"
                    + "수정할 방명록의 guestBookId와 변경할 값(writer, content 등)을 요청 본문에 포함시켜 요청합니다."
    )
    ResponseEntity<Boolean> updateGuestBook(@RequestBody GuestBookDto guestBookDto);

    /**
     * 방명록 삭제 API
     * Query Parameter로 삭제할 방명록의 guestBookId를 전달합니다.
     */
    @Operation(
            summary = "방명록 삭제",
            description = "방명록을 삭제합니다.\n\n"
                    + "삭제할 방명록의 guestBookId를 쿼리 파라미터로 전달합니다."
    )
    ResponseEntity<Boolean> deleteGuestBook(@RequestParam int guestBookId);
}
