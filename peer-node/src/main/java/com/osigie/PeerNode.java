package com.osigie;


import com.osigie.domain.ChunkAcquired;
import com.osigie.domain.PeerInfo;
import utils.HttpClientInstance;
import com.osigie.service.TrackerClient;

import java.util.UUID;

public class PeerNode {
    public static void main(String[] args) {
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

        ChunkAcquired chunkAcquired = new ChunkAcquired("peer-001", UUID.randomUUID(), UUID.randomUUID());


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
    }

}
