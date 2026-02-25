package com.osigie.origin_server.dto.external;

import lombok.Data;

import java.util.UUID;

@Data
public class SongChunkDto {
    private UUID id;
    private String hash;
    private int index;
}