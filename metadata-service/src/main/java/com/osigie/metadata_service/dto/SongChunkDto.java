package com.osigie.metadata_service.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class SongChunkDto {
    private UUID id;
    private String hash;
    private int index;
}
