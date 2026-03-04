package com.osigie.domain;


import java.util.UUID;

public class Chunk {
    private byte[] chunk;

    private String hash;

    private int index;

    private UUID id;

    public Chunk(byte[] chunk, String hash, int index, UUID id) {
        this.chunk = chunk;
        this.hash = hash;
        this.index = index;
        this.id = id;
    }

    public byte[] getChunk() {
        return chunk;
    }

    public void setChunk(byte[] chunk) {
        this.chunk = chunk;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
