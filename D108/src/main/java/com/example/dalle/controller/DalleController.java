package com.example.dalle.controller;

import com.example.dalle.model.DalleRequest;
import com.example.dalle.model.DalleResponse;
import com.example.dalle.service.DalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dalle")
public class DalleController {

    private final DalleService dalleService;

    @Autowired
    public DalleController(DalleService dalleService) {
        this.dalleService = dalleService;
    }

    @PostMapping("/generate")
    public ResponseEntity<DalleResponse> generateImage(@RequestBody DalleRequest request) {
        DalleResponse response = dalleService.generateImage(request);
        return ResponseEntity.ok(response);
    }
}
