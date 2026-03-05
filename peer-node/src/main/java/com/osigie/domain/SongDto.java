package com.osigie.domain;


import java.util.Set;
import java.util.UUID;

public class SongDto {

    private UUID id;

    private String title;

    private long fileSize;


    private long chunkSize;

    private long chunkCount;

    private Set<SongChunkDto> songChunks;

    public SongDto() {
    }

    public SongDto(UUID id, String title, long fileSize, long chunkSize, long chunkCount, Set<SongChunkDto> songChunks) {
        this.id = id;
        this.title = title;
        this.fileSize = fileSize;
        this.chunkSize = chunkSize;
        this.chunkCount = chunkCount;
        this.songChunks = songChunks;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(long chunkSize) {
        this.chunkSize = chunkSize;
    }

    public long getChunkCount() {
        return chunkCount;
    }

    public void setChunkCount(long chunkCount) {
        this.chunkCount = chunkCount;
    }

    public Set<SongChunkDto> getSongChunks() {
        return songChunks;
    }

    public void setSongChunks(Set<SongChunkDto> songChunks) {
        this.songChunks = songChunks;
    }

}
