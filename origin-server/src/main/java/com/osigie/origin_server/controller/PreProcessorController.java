package com.osigie.origin_server.controller;

import com.osigie.origin_server.dto.request.UploadDto;
import com.osigie.origin_server.service.PreProcessorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/preprocess")
public class PreProcessorController {
    private final PreProcessorService preProcessorService;

    public PreProcessorController(PreProcessorService preProcessorService) {
        this.preProcessorService = preProcessorService;
    }


    @PostMapping(value = "upload", consumes = "multipart/form-data")
    public ResponseEntity<String> upload(@ModelAttribute UploadDto dto) {
        UUID id = preProcessorService.process(dto);
        return new ResponseEntity<>("File uploaded and processed successfully with id: " + id.toString(), HttpStatus.OK);
    }
}
