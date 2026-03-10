package com.osigie.service;

import com.osigie.domain.Chunk;
import com.osigie.domain.FilterChunks;
import com.osigie.domain.PeerInfo;
import com.osigie.domain.SongChunkDto;
import com.osigie.networking.PeerClient;
import com.osigie.service.clients.OriginClient;
import com.osigie.service.clients.TrackerClient;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Scheduler {
    private final TrackerClient trackerClient;
    private final OriginClient originClient;
    private final PeerClient peerClient;
    private final ChunkStore chunkStore;

    public Scheduler(TrackerClient trackerClient, OriginClient originClient, PeerClient peerClient, ChunkStore chunkStore) {
        this.trackerClient = trackerClient;
        this.originClient = originClient;
        this.peerClient = peerClient;
        this.chunkStore = chunkStore;
    }

    public CompletableFuture<TreeSet<SongChunkDto>> start(FilterChunks filter, String songId) {
        TreeSet<SongChunkDto> localChunks = filter.getLocalChunks();
        List<SongChunkDto> chunksToFetch = filter.getChunksToFetch();

        List<CompletableFuture<Void>> futures = chunksToFetch.stream()
                .map(chunk -> fetchChunk(chunk, songId, localChunks))
                .toList();

        return CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply((a) -> localChunks);
    }

    private CompletableFuture<Void> fetchChunk(SongChunkDto chunk, String songId, TreeSet<SongChunkDto> localChunks) {
        return trackerClient.getPeers(songId)
                .thenCompose(peers -> {
                    Map<String, PeerInfo> chunkPeerMap = peers.get(chunk.getId());

                    if (chunkPeerMap == null || chunkPeerMap.isEmpty()) {
                        return fetchFromOrigin(chunk, songId, localChunks);
                    }

                    return fetchFromPeers(chunk, songId, localChunks, chunkPeerMap)
                            .thenCompose(downloaded -> {
                                System.out.println("Downloaded: " + downloaded);
                                if (!downloaded) {
                                    return fetchFromOrigin(chunk, songId, localChunks);
                                }
                                return CompletableFuture.completedFuture(null);
                            });
                })
                .exceptionally(e -> {
                    System.err.printf("Failed to fetch chunk %s: %s%n", chunk.getId(), e.getMessage());
                    return null;
                });
    }

    private CompletableFuture<Boolean> fetchFromPeers(SongChunkDto chunk, String songId,
                                                      TreeSet<SongChunkDto> localChunks,
                                                      Map<String, PeerInfo> chunkPeerMap) {
        CompletableFuture<Boolean> result = CompletableFuture.completedFuture(false);

        for (PeerInfo peer : chunkPeerMap.values()) {
            result = result.thenCompose(downloaded -> {
                if (downloaded) return CompletableFuture.completedFuture(true);

                return requestFromPeer(peer, chunk, songId, localChunks);
            });
        }

        return result;
    }

    private CompletableFuture<Boolean> requestFromPeer(PeerInfo peer, SongChunkDto chunk,
                                                       String songId, TreeSet<SongChunkDto> localChunks) {

        CompletableFuture<byte[]> peerFuture = new CompletableFuture<>();

        peerClient.requestChunk(peer.host(), peer.port(), songId, chunk.getId().toString(), peerFuture::complete);

        return peerFuture.thenApply(bytes -> {
                    if (bytes == null || !validateHash(bytes, chunk.getHash())) return false;

                    chunkStore.saveChunk(songId, new Chunk(bytes, chunk.getHash(), chunk.getIndex(), chunk.getId()));
                    synchronized (localChunks) {
                        localChunks.add(new SongChunkDto(chunk.getId(), chunk.getHash(), chunk.getIndex()));
                    }
                    return true;
                })
                .exceptionally(e -> {
                    System.err.printf("Peer %s:%d failed for chunk %s: %s%n",
                            peer.host(), peer.port(), chunk.getId(), e.getMessage());
                    return false;
                });
    }

    private CompletableFuture<Void> fetchFromOrigin(SongChunkDto chunk, String songId,
                                                    TreeSet<SongChunkDto> localChunks) {
        return originClient.fetchChunk(chunk.getId().toString(), songId)
                .thenAccept(bytes -> {
                    chunkStore.saveChunk(songId, new Chunk(bytes, chunk.getHash(), chunk.getIndex(), chunk.getId()));
                    synchronized (localChunks) {
                        localChunks.add(new SongChunkDto(chunk.getId(), chunk.getHash(), chunk.getIndex()));
                    }
                })
                .exceptionally(e -> {
                    System.err.printf("Origin failed for chunk %s: %s%n", chunk.getId(), e.getMessage());
                    return null;
                });
    }

    private boolean validateHash(byte[] bytes, String expectedHash) {
        // TODO: implement hash validation
        return true;
    }
}
