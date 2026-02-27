package com.osigie;


import com.osigie.domain.ChunkAcquired;
import com.osigie.domain.PeerInfo;
import com.osigie.service.OriginClient;
import utils.HttpClientInstance;
import com.osigie.service.TrackerClient;

import java.util.UUID;

public class PeerNode {
    public static void main(String[] args) {

        //Tracker client
        TrackerClient trackerClient = new TrackerClient(HttpClientInstance.getInstance());

        trackerClient.getPeers(UUID.randomUUID())
                .thenAccept(System.out::println)
                .exceptionally(e -> {
                    System.out.println(e.getMessage());
                    return null;
                }).join();


        PeerInfo peerInfo = new PeerInfo("peer-001", "localhost", 8080);

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


        trackerClient.heartbeat(peerInfo.peerId())
                .thenAccept(System.out::println)
                .exceptionally(e -> {
                    System.out.println(e.getMessage());
                    return null;
                }).join();


////Origin Client
        OriginClient originClient = new OriginClient(HttpClientInstance.getInstance());

//        originClient.fetchChunk(chunkId, songId)
//                .thenAccept(System.out::println)
//                .exceptionally(e -> {
//                    System.out.println(e.getMessage());
//                    return null;
//                }).join();

        originClient.fetchChunk("e59af557-84c4-4042-97aa-bb9da8795427", "17761071-7c0f-4187-ba40-c21357578838")
                .thenAccept(System.out::println)
                .exceptionally(e -> {
                    System.out.println(e.getMessage());
                    return null;
                }).join();

    }

}
