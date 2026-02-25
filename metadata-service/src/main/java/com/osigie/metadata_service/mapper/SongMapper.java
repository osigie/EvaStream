package com.osigie.metadata_service.mapper;

import com.osigie.metadata_service.domain.model.Song;
import com.osigie.metadata_service.domain.model.SongChunk;
import com.osigie.metadata_service.dto.SongChunkDto;
import com.osigie.metadata_service.dto.SongDto;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SongMapper {
    public SongDto mapToSongDto(Song entity) {
        SongDto dto = new SongDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setFileSize(entity.getFileSize());
        dto.setChunkCount(entity.getChunkCount());
        dto.setChunkSize(entity.getChunkSize());
        Set<SongChunkDto> songChunkDto = entity.getSongChunks().stream().map(ch -> {
            SongChunkDto dtoSongChunk = new SongChunkDto();
            dtoSongChunk.setHash(ch.getHash());
            dtoSongChunk.setIndex(ch.getIndex());
            dtoSongChunk.setId(ch.getId());
            return dtoSongChunk;
        }).collect(Collectors.toSet());
        dto.setSongChunks(songChunkDto);

        return dto;

    }

    public Song mapToSong(SongDto songDto) {

        Song song = Song.builder()
                .title(songDto.getTitle())
                .fileSize(songDto.getFileSize())
                .chunkCount(songDto.getChunkCount())
                .chunkSize(songDto.getChunkSize())
                .id(songDto.getId())
                .build();

        Set<SongChunk> songChunks =
                songDto.getSongChunks().stream().map(ch -> SongChunk.builder()
                        .hash(ch.getHash())
                        .index(ch.getIndex())
                        .song(song)
                        .id(ch.getId())
                        .build()
                ).collect(Collectors.toSet());

        song.setSongChunks(songChunks);

        return song;
    }
}


