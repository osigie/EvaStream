package com.osigie;


import com.osigie.domain.ChunkAcquired;
import com.osigie.domain.PeerInfo;
import com.osigie.networking.PeerClient;
import com.osigie.networking.PeerServer;
import com.osigie.service.ChunkStore;
import com.osigie.service.MetadataClient;
import com.osigie.service.OriginClient;
import utils.HttpClientInstance;
import com.osigie.service.TrackerClient;

import java.util.UUID;

public class PeerNode {

    //    TODO: pass through env or variable
    private static final String peerId = "peer-001";

    public static void main(String[] args) throws InterruptedException {
        //Tracker client
        TrackerClient trackerClient = new TrackerClient(HttpClientInstance.getInstance());

        trackerClient.getPeers(UUID.randomUUID())
                .thenAccept(System.out::println)
                .exceptionally(e -> {
                    System.out.println(e.getMessage());
                    return null;
                }).join();


        PeerInfo peerInfo = new PeerInfo(peerId, "localhost", 8080);

        trackerClient.register(peerInfo)
                .thenAccept(System.out::println)
                .exceptionally(e -> {
                    System.out.println(e.getMessage());
                    return null;
                }).join();


        UUID chunkId = UUID.randomUUID();
        UUID songId = UUID.randomUUID();

        ChunkAcquired chunkAcquired = new ChunkAcquired("peer-001", chunkId, songId);


        trackerClient.notifyChuckAcquired(chunkAcquired)
                .thenAccept(System.out::println)
                .exceptionally(e -> {
                    System.out.println(e.getMessage());
                    return null;
                }).join();


//        trackerClient.heartbeat(peerInfo.peerId())
//                .thenAccept(System.out::println)
//                .exceptionally(e -> {
//                    System.out.println(e.getMessage());
//                    return null;
//                }).join();


////Origin Client
        OriginClient originClient = new OriginClient(HttpClientInstance.getInstance());

//        originClient.fetchChunk(chunkId, songId)
//                .thenAccept(System.out::println)
//                .exceptionally(e -> {
//                    System.out.println(e.getMessage());
//                    return null;
//                }).join();

        String sSongId = "535ef1f1-8592-419f-8e4d-2fb85fcb264c";
        String sChunkId = "7ebefc30-8d77-4295-9684-f1cd283197a4";

        originClient.fetchChunk(sChunkId, sSongId)
                .thenAccept(System.out::println)
                .exceptionally(e -> {
                    System.out.println(e.getMessage());
                    return null;
                }).join();


        MetadataClient metadataClient = new MetadataClient(HttpClientInstance.getInstance());

        metadataClient.getMetadata(songId)
                .thenAccept(System.out::println)
                .exceptionally(e -> {
                    System.out.println(e.getMessage());
                    return null;
                }).join();


        ChunkStore chunkStore = new ChunkStore();
        PeerServer peerServer = new PeerServer(9000, chunkStore, peerId);



        peerServer.start();

        PeerClient peerClient = new PeerClient();

        peerClient.requestChunk("localhost", 9000, "535ef1f1-8592-419f-8e4d-2fb85fcb264c", "7ebefc30-8d77-4295-9684-f1cd283197a4", () -> {
            System.out.println("Chunk received!");
        });

        trackerClient.heartbeatRunner(peerInfo.peerId());

    }

}
