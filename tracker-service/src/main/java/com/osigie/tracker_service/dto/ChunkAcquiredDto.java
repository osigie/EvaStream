package com.osigie.tracker_service.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ChunkAcquiredDto {
    private String peerId;
    private UUID songId;
    private UUID chunkId;
}
