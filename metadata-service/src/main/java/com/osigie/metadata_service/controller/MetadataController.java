package com.osigie.metadata_service.controller;

import com.osigie.metadata_service.domain.model.Song;
import com.osigie.metadata_service.dto.SongDto;
import com.osigie.metadata_service.mapper.SongMapper;
import com.osigie.metadata_service.service.MetadataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/metadata")
public class MetadataController {
    private final MetadataService songService;
    private final SongMapper songMapper;

    public MetadataController(MetadataService songService, SongMapper songMapper) {
        this.songService = songService;
        this.songMapper = songMapper;
    }

    @PostMapping
    public ResponseEntity<SongDto> save(@RequestBody SongDto song) {
        Song savedSong = this.songService.create(songMapper.mapToSong(song));
        return new ResponseEntity<>(songMapper.mapToSongDto(savedSong), HttpStatus.CREATED);

    }

    @GetMapping("{id}")
    public ResponseEntity<SongDto> findById(@PathVariable UUID id) {
        Song song = this.songService.findById(id);
        return new ResponseEntity<>(songMapper.mapToSongDto(song), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        this.songService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
