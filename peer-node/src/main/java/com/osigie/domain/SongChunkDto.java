package com.osigie.domain;


import java.util.UUID;

public class SongChunkDto {
    @Override
    public String toString() {
        return "SongChunkDto{" +
                "id=" + id +
                ", hash='" + hash + '\'' +
                ", index=" + index +
                '}';
    }

    private UUID id;
    private String hash;
    private int index;

    public SongChunkDto() {
    }

    public SongChunkDto(UUID id, String hash, int index) {
        this.id = id;
        this.hash = hash;
        this.index = index;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

}
