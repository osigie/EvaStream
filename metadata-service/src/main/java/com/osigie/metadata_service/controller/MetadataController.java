package com.osigie.metadata_service.controller;

import com.osigie.metadata_service.domain.model.Song;
import com.osigie.metadata_service.dto.SongDto;
import com.osigie.metadata_service.mapper.SongMapper;
import com.osigie.metadata_service.service.MetadataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/metadata")
public class MetadataController {
    private final MetadataService songService;

    public MetadataController(MetadataService songService) {
        this.songService = songService;
    }

    @PostMapping
    public ResponseEntity<SongDto> save(SongDto song) {
        Song savedSong = this.songService.create(SongMapper.mapToSong(song, new Song()));
        return new ResponseEntity<>(SongMapper.mapToSongDto(savedSong, new SongDto()), HttpStatus.CREATED);

    }

    @GetMapping("{id}")
    public ResponseEntity<SongDto> findById(@PathVariable Long id) {
        Song song = this.songService.findById(id);
        return new ResponseEntity<>(SongMapper.mapToSongDto(song, new SongDto()), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        this.songService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
