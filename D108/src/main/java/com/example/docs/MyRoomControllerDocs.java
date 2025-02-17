package com.example.docs;

import com.example.model.dto.PictureDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface MyRoomControllerDocs {
    @Operation(
            summary = "마이룸에 전시된 그림과 가구를 조회합니다.",
            description =
                    "주어진 `userId`에 해당하는 마이룸에 전시된 그림과 가구를 조회합니다."
    )
    public ResponseEntity<List<PictureDto>> getDisplayedItems(@PathVariable int userId);
}
