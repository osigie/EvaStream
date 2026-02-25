package com.osigie.origin_server.controller;

import com.osigie.origin_server.model.Chunk;
import com.osigie.origin_server.service.OriginServerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping
public class OriginServerController {
    private final OriginServerService originServerService;

    public OriginServerController(OriginServerService originServerService) {
        this.originServerService = originServerService;
    }

    @GetMapping(value = "/chunk", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getChunk(@RequestParam(name = "songId") UUID songId, @RequestParam(name = "chunkId") UUID chunkId) {
        return new ResponseEntity<>(this.originServerService.fetchChunk(songId, chunkId), HttpStatus.OK);
    }

}
