package com.osigie.origin_server.dto.external;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class SongDto {

    private UUID id;

    private String title;

    private long fileSize;

    private long chunkSize;

    private long chunkCount;

    private Set<SongChunkDto> songChunks;
}
