package com.osigie.domain;

import java.util.List;
import java.util.TreeSet;

public class FilterChunks {
    private List<SongChunkDto> chunksToFetch;
    private TreeSet<SongChunkDto> localChunks;

    public FilterChunks(List<SongChunkDto> chunksToFetch, TreeSet<SongChunkDto> localChunks) {
        this.chunksToFetch = chunksToFetch;
        this.localChunks = localChunks;
    }

    public List<SongChunkDto> getChunksToFetch() {
        return chunksToFetch;
    }

    public void setChunksToFetch(List<SongChunkDto> chunksToFetch) {
        this.chunksToFetch = chunksToFetch;
    }

    public TreeSet<SongChunkDto> getLocalChunks() {
        return localChunks;
    }

    public void setLocalChunks(TreeSet<SongChunkDto> localChunks) {
        this.localChunks = localChunks;
    }
}
