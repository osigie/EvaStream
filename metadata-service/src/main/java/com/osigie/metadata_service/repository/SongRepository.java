package com.osigie.metadata_service.repository;

import com.osigie.metadata_service.domain.model.Song;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends CrudRepository<Song, Long> {
}
