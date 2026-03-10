package com.osigie.service;

import com.osigie.domain.*;
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
    private final String peerId;
    private final int port;

    public Scheduler(TrackerClient trackerClient, OriginClient originClient, PeerClient peerClient, ChunkStore chunkStore, String peerId, int port) {
        this.trackerClient = trackerClient;
        this.originClient = originClient;
        this.peerClient = peerClient;
        this.chunkStore = chunkStore;
        this.peerId = peerId;
        this.port = port;
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
                        return fetchFromOrigin(chunk, songId, localChunks)
                                .thenCompose(fetched -> {
                                    if (fetched) {
                                        return chunkAcquired(new PeerInfo(peerId, "localhost", port), chunk.getId(), songId);
                                    }
                                    return CompletableFuture.completedFuture(null);
                                });
                    }
                    return fetchFromPeers(chunk, songId, localChunks, chunkPeerMap)
                            .thenCompose(downloaded -> {
                                if (downloaded) {
                                    return chunkAcquired(new PeerInfo(peerId, "localhost", port), chunk.getId(), songId);
                                } else {
                                    return fetchFromOrigin(chunk, songId, localChunks)
                                            .thenCompose(fetched -> {
                                                if (fetched) {
                                                    return chunkAcquired(new PeerInfo(peerId, "localhost", port), chunk.getId(), songId);
                                                }
                                                return CompletableFuture.completedFuture(null);
                                            });
                                }
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
                    System.out.printf("Successfully requested chunk %s%n", chunk.getId());

                    if (bytes == null || !validateHash(bytes, chunk.getHash())) return false;
                    chunkStore.saveChunk(songId, new Chunk(bytes, chunk.getHash(), chunk.getIndex(), chunk.getId()));
                    synchronized (localChunks) {
                        localChunks.add(new SongChunkDto(chunk.getId(), chunk.getHash(), chunk.getIndex()));
                    }

                    System.out.println("Chunk acquired from " + peer.peerId());

                    return true;
                })
                .exceptionally(e -> {
                    System.err.printf("Peer %s:%d failed for chunk %s: %s%n",
                            peer.host(), peer.port(), chunk.getId(), e.getMessage());
                    return false;
                });
    }

    private CompletableFuture<Boolean> fetchFromOrigin(SongChunkDto chunk, String songId,
                                                       TreeSet<SongChunkDto> localChunks) {
        return originClient.fetchChunk(chunk.getId().toString(), songId)
                .thenApply(bytes -> {
                    chunkStore.saveChunk(songId, new Chunk(bytes, chunk.getHash(), chunk.getIndex(), chunk.getId()));
                    synchronized (localChunks) {
                        localChunks.add(new SongChunkDto(chunk.getId(), chunk.getHash(), chunk.getIndex()));
                    }
                    return true;
                })
                .exceptionally(e -> {
                    System.err.printf("Origin failed for chunk %s: %s%n", chunk.getId(), e.getMessage());
                    return false;
                });
    }

    private boolean validateHash(byte[] bytes, String expectedHash) {
        // TODO: implement hash validation
        return true;
    }


    private CompletableFuture<Void> chunkAcquired(PeerInfo peerInfo, UUID chunkId, String songId) {
        return trackerClient.register(peerInfo)
                .thenCompose(registered -> {
                    System.out.println("Peer with id " + peerInfo.peerId() + " " + registered);

                    return trackerClient.notifyChuckAcquired(new ChunkAcquired(peerId, chunkId, UUID.fromString(songId)))
                            .thenAccept(notified -> {
                                System.out.println("Notified chunked acquired " + notified);
                            })

                            .exceptionally(e -> {
                                System.err.printf("Failed to notified chunk acquired %s: %s%n", chunkId, e.getMessage());
                                return null;
                            });

                })
                .exceptionally(e -> {
                    System.err.printf("Failed to notify tracker service of chunk acquired for chunk id %s: and peer id %s: %s%n", chunkId, peerInfo.peerId(), e.getMessage());
                    return null;
                });
    }

}
