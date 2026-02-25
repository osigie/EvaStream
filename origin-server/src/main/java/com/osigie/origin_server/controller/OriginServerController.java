package com.osigie.origin_server.controller;
import com.osigie.origin_server.model.Chunk;
import com.osigie.origin_server.service.OriginServerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class OriginServerController {
    private final OriginServerService originServerService;

    public OriginServerController(OriginServerService originServerService) {
        this.originServerService = originServerService;
    }

    @GetMapping("/chunk")
    public ResponseEntity<Chunk> getChunk(@RequestParam(name = "songId") Long songId, @RequestParam(name = "chunkId") Long chunkId) {
        return new ResponseEntity<>(this.originServerService.fetchChunk(songId, chunkId), HttpStatus.OK);
    }
}
