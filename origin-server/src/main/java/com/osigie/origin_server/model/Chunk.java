package com.osigie.origin_server.model;

import lombok.Data;

import java.util.UUID;

@Data
public class Chunk {
    private byte[] chunk;

    private String hash;

    private int index;

    private UUID id;
}
