package com.osigie.tracker_service.controller;


import com.osigie.tracker_service.domain.PeerInfo;
import com.osigie.tracker_service.dto.ChunkAcquiredDto;
import com.osigie.tracker_service.dto.HeartbeatDto;
import com.osigie.tracker_service.dto.RegisterDto;
import com.osigie.tracker_service.mapper.TrackerMapper;
import com.osigie.tracker_service.service.TrackerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping(path = "/tracker")
public class TrackerController {
    private final TrackerService trackerService;
    private final TrackerMapper trackerMapper;

    public TrackerController(TrackerService trackerService, TrackerMapper trackerMapper) {
        this.trackerService = trackerService;
        this.trackerMapper = trackerMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        this.trackerService.register(trackerMapper.mapToPeerInfo(registerDto));
        return new ResponseEntity<>("Success", HttpStatus.CREATED);
    }

    @PostMapping("/heartbeat")
    public ResponseEntity<String> heartbeat(@RequestBody HeartbeatDto heartbeatDto) {
        this.trackerService.heartbeat(heartbeatDto.getPeerId());
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    @GetMapping("/peers")
    public ResponseEntity<Map<UUID, Map<String, PeerInfo>>> getPeers(@RequestParam UUID songId) {
        return new ResponseEntity<>(this.trackerService.getSongPeers(songId), HttpStatus.OK);
    }


    @PostMapping("/chunk-acquired")
    public ResponseEntity<String> chunkAcquired(@RequestBody ChunkAcquiredDto chunkAcquiredDto) {
        this.trackerService.chunkAcquired(chunkAcquiredDto);
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }
}
