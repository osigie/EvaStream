package com.osigie.metadata_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class SongDto {

    private UUID id;

    @NotBlank(message = "Title can not be null or blank")
    private String title;

    @NotNull(message = "FileSize can not be null")
    @Positive(message = "FileSize must be greater than 0")
    private long fileSize;

    @NotNull(message = "ChunkSize can not be null")
    @Positive(message = "ChunkSize must be greater than 0")
    private long chunkSize;

    @NotNull(message = "ChunkCount can not be null")
    @Positive(message = "ChunkCount must be greater than 0")
    private long chunkCount;

    @Valid
    private Set<SongChunkDto> songChunks;
}
