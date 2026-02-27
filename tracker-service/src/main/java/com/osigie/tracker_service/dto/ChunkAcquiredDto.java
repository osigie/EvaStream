package com.osigie.tracker_service.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ChunkAcquiredDto {
    UUID peerId;
    UUID songId;
    UUID chunkId;
}
