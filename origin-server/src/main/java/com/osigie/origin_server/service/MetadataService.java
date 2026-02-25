package com.osigie.metadata_service.service;

import com.osigie.metadata_service.domain.model.Song;

public interface MetadataService {

    Song create(Song song);

    void deleteById(Long id);

    Song findById(Long id);
}
