package com.osigie.metadata_service.mapper;

import com.osigie.metadata_service.domain.model.Song;
import com.osigie.metadata_service.dto.SongDto;

public class SongMapper {
    public static SongDto mapToSongDto(Song entity, SongDto songDto) {
        System.out.println(entity.getTitle());
        songDto.setId(entity.getId());
        songDto.setTitle(entity.getTitle());
        songDto.setFileSize(entity.getFileSize());
        songDto.setChunkCount(entity.getChunkCount());
        songDto.setSongChunks(entity.getSongChunks());
        return songDto;

    }

    public static Song mapToSong(SongDto songDto, Song song) {
        System.out.println(songDto.getTitle());
        song.setChunkCount(songDto.getChunkCount());
        song.setSongChunks(songDto.getSongChunks());
        song.setFileSize(songDto.getFileSize());
        song.setSongChunks(songDto.getSongChunks());
        song.setTitle(songDto.getTitle());
        return song;
    }
}


