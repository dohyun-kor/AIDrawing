// 파일 경로: src/main/java/com/example/controller/GuestBookController.java
package com.example.controller;

import com.example.docs.GuestBookControllerDocs;  // Swagger 문서용 인터페이스 (필요시)
import com.example.model.dto.GuestBookDto;
import com.example.model.dto.GuestBookRequestDto;
import com.example.model.service.GuestBookService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "방명록", description = "방명록 작성 및 삭제 관련 API")
@RestController
@RequestMapping("/guestBook")
public class GuestBookController implements GuestBookControllerDocs {

    private final GuestBookService guestBookService;

    @Autowired
    public GuestBookController(GuestBookService guestBookService) {
        this.guestBookService = guestBookService;
    }

    /**
     * 방명록 작성 API
     * URL: POST /guestBook
     * @param guestBookRequestDto 작성할 방명록 데이터 (userId, writer, content 등 포함)
     * @return 작성 성공 시 true, 실패 시 500 에러 반환
     */
    @PostMapping
    public ResponseEntity<Boolean> createGuestBook(@RequestBody GuestBookRequestDto guestBookRequestDto) {
        try {
            int result = guestBookService.createGuestBook(guestBookRequestDto);
            if (result == 1) {
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     *
     * @param guestBookDto
     * @return
     */
    @PutMapping
    public ResponseEntity<Boolean> updateGuestBook(@RequestBody GuestBookDto guestBookDto) {
        try {
            int result = guestBookService.updateGuestBook(guestBookDto);
            if (result == 1) {
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    /**
     * 방명록 삭제 API
     * URL: DELETE /guestBook?guestBookId={guestBookId}
     * @param guestBookId 삭제할 방명록의 고유 아이디
     * @return 삭제 성공 시 true, 실패 시 500 에러 반환
     */
    @DeleteMapping
    public ResponseEntity<Boolean> deleteGuestBook(@RequestParam int guestBookId) {
        try {
            int result = guestBookService.deleteGuestBook(guestBookId);
            if (result == 1) {
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
