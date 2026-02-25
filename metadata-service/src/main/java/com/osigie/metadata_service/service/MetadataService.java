package com.osigie.metadata_service.service;

import com.osigie.metadata_service.domain.model.Song;

import java.util.UUID;

public interface MetadataService {

    Song create(Song song);

    void deleteById(UUID id);

    Song findById(UUID id);
}
