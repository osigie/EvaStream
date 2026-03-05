package com.osigie.service;

import com.osigie.domain.FilterChunks;
import com.osigie.domain.SongChunkDto;
import com.osigie.domain.SongDto;
import com.osigie.networking.PeerClient;
import com.osigie.service.clients.MetadataClient;
import com.osigie.service.clients.OriginClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class DownloadCoordinator {
    private final MetadataClient metadataClient;
    private final Scheduler scheduler;
    private final ChunkStore chunkStore;
    private final String peerId;

    public DownloadCoordinator(MetadataClient metadataClient, Scheduler scheduler, ChunkStore chunkStore, String peerId) {
        this.metadataClient = metadataClient;
        this.scheduler = scheduler;
        this.chunkStore = chunkStore;
        this.peerId = peerId;
    }

    /**
     * 1. First get song metadata,
     * 2. Then check if the chunk in local is complete and decide the ones to download from other peers or origin server
     * 3. Download the requested song by searching through peers before defaulting to origin if not found
     * 4. Saving the chunks and telling the origin server that you have gotten chunk
     *
     */

    public void start(List<String> songIds) {

        System.out.println("Starting DownloadCoordinator");

        for (String songId : songIds) {
            metadataClient.getMetadata(songId)
                    .thenAccept((songMetadata) -> {
                        FilterChunks chunks = filterChunksToFetch(songMetadata.getSongChunks(), songId);
                        if (chunks.getChunksToFetch().isEmpty()) {
                            //I already have everything; download from local peer
                            List<byte[]> data = this.chunkStore.loadChunk(chunks.getLocalChunks(), peerId, songId);

                            //Can now put it together and send it to be used

                            System.out.println("Have full data of length " + data.size());
                            System.out.println("From Local");

                        } else {
                            this.scheduler.start(chunks, songId).thenApply((r) -> {

                                List<byte[]> data = this.chunkStore.loadChunk(r, peerId, songId);

                                System.out.println("Have full data of length " + data.size());
                                System.out.println("From External");

                                return null;
                            });
                        }

                    })
                    .exceptionally(e -> {
                        System.out.println(e.getMessage());
                        return null;
                    });
        }
    }

    private FilterChunks filterChunksToFetch(Set<SongChunkDto> songChunks, String songId) {
        List<SongChunkDto> result = new ArrayList<>();
        TreeSet<SongChunkDto> localChunks = new TreeSet<>((a, b) -> b.getIndex() - a.getIndex());

        for (SongChunkDto songChunk : songChunks) {

            System.out.println(songChunk.getHash());

            if (this.chunkStore.isExist(songId, songChunk.getId().toString())) {
                localChunks.add(songChunk);
            } else {
                result.add(songChunk);
            }
        }
        return new FilterChunks(result, localChunks);
    }

}
