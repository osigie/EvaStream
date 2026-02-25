package com.osigie.metadata_service.service.impl;

import com.osigie.metadata_service.domain.model.Song;
import com.osigie.metadata_service.repository.SongRepository;
import com.osigie.metadata_service.service.MetadataService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MetadataServiceImpl implements MetadataService {
    private final SongRepository songRepository;

    public MetadataServiceImpl(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    public Song create(Song song) {
        return songRepository.save(song);
    }

    @Override
    public void deleteById(UUID id) {
        songRepository.deleteById(id);
    }

    @Override
    public Song findById(UUID id) {
        return this.songRepository.findById(id).orElseThrow(() -> new RuntimeException("Song not found"));
    }
}
