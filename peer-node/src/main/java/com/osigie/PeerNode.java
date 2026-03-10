package com.osigie;

import com.osigie.networking.PeerClient;
import com.osigie.networking.PeerServer;
import com.osigie.service.ChunkStore;
import com.osigie.service.DownloadCoordinator;
import com.osigie.service.Scheduler;
import com.osigie.service.clients.MetadataClient;
import com.osigie.service.clients.OriginClient;
import com.osigie.service.clients.TrackerClient;
import utils.HttpClientInstance;

import java.util.List;

public class PeerNode {

    public static void main(String[] args) throws InterruptedException {

        String peerId = System.getProperty("peer.id", "default-peer");
        String songId = System.getProperty("song.id", "");
        int port = Integer.parseInt(System.getProperty("peer.port", "9000"));

        /**
         * 1. Start up upload server
         * 5. I might have to rearrange the file from the chunk or just do nothing
         * */

        ChunkStore chunkStore = new ChunkStore(peerId);
        PeerServer server = new PeerServer(port, chunkStore);
        server.start();

        MetadataClient metadataClient = new MetadataClient(HttpClientInstance.getInstance());
        OriginClient originClient = new OriginClient(HttpClientInstance.getInstance());
        TrackerClient trackerClient = new TrackerClient(HttpClientInstance.getInstance());
        PeerClient peerClient = new PeerClient();

        Scheduler scheduler = new Scheduler(trackerClient, originClient, peerClient, chunkStore, peerId, port);
        DownloadCoordinator coordinator = new DownloadCoordinator(metadataClient, scheduler, chunkStore, peerId, trackerClient, port);

        coordinator.start(List.of(songId));

        trackerClient.heartbeatRunner(peerId);


    }
}
