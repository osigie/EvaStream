package com.osigie.metadata_service.dto;

import lombok.Data;

import java.util.Set;

@Data
public class SongDto {

    private Long id;

    private String title;

    private long fileSize;

    private long chunkSize;

    private long chunkCount;

    private Set<SongChunkDto> songChunks;
}
