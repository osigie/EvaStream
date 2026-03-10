package com.osigie.tracker_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ChunkAcquiredDto {

    @NotBlank(message = "PeerId cannot be null or blank")
    private String peerId;

    @NotNull(message = "SongId cannot be null")
    private UUID songId;

    @NotNull(message = "ChunkId cannot be null")
    private UUID chunkId;
}
