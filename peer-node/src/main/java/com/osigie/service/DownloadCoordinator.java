package com.osigie.service;

import com.osigie.domain.FilterChunks;
import com.osigie.domain.PeerInfo;
import com.osigie.domain.SongChunkDto;
import com.osigie.domain.SongDto;
import com.osigie.networking.PeerClient;
import com.osigie.service.clients.MetadataClient;
import com.osigie.service.clients.OriginClient;
import com.osigie.service.clients.TrackerClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DownloadCoordinator {
    private final MetadataClient metadataClient;
    private final Scheduler scheduler;
    private final ChunkStore chunkStore;
    private final String peerId;
    private final TrackerClient trackerClient;
    private final int port;

    public DownloadCoordinator(MetadataClient metadataClient, Scheduler scheduler, ChunkStore chunkStore, String peerId, TrackerClient trackerClient, int port) {
        this.metadataClient = metadataClient;
        this.scheduler = scheduler;
        this.chunkStore = chunkStore;
        this.peerId = peerId;
        this.trackerClient = trackerClient;
        this.port = port;
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
                        
                        registerExistingChunks(chunks.getLocalChunks(), songId);
                        
                        if (chunks.getChunksToFetch().isEmpty()) {
                            System.out.println("No chunks to fetch");
                            //I already have everything; download from local peer
                            List<byte[]> data = this.chunkStore.loadChunk(chunks.getLocalChunks(),  songId);

                            //Can now put it together and send it to be used

                            System.out.println("Have full data of length " + data.size());
                            System.out.println("From Local");

                        } else {
                            this.scheduler.start(chunks, songId).thenApply((r) -> {

                                List<byte[]> data = this.chunkStore.loadChunk(r, songId);

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

            if (this.chunkStore.isExist(songId, songChunk.getId().toString())) {
                localChunks.add(songChunk);
            } else {
                result.add(songChunk);
            }
        }
        return new FilterChunks(result, localChunks);
    }

    private void registerExistingChunks(Set<SongChunkDto> localChunks, String songId) {
        if (localChunks.isEmpty()) {
            return;
        }
        
        PeerInfo peerInfo = new PeerInfo(peerId, "localhost", port);
        
        trackerClient.register(peerInfo)
                .thenAccept(registered -> {
                    System.out.println("Registered peer with tracker: " + registered);
                    
                    List<CompletableFuture<Void>> futures = localChunks.stream()
                            .map(chunk -> trackerClient.notifyChuckAcquired(
                                    new com.osigie.domain.ChunkAcquired(peerId, chunk.getId(), UUID.fromString(songId))
                            ).thenAccept(notified -> 
                                System.out.println("Registered existing chunk: " + chunk.getId())
                            ))
                            .toList();
                    
                    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                })
                .exceptionally(e -> {
                    System.err.println("Failed to register existing chunks: " + e.getMessage());
                    return null;
                });
    }

}
