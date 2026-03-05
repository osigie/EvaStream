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

import java.util.ArrayList;
import java.util.List;

public class PeerNode {

    //    TODO: pass through env or variable
    private static final String peerId = "peer-001";
    private static final int port = 9000;
    private static final String songId = "535ef1f1-8592-419f-8e4d-2fb85fcb264c";

    public static void main(String[] args) throws InterruptedException {
        /**
         * 1. Start up upload server
         * 5. I might have to rearrange the file from the chunk or just do nothing
         * */

        ChunkStore chunkStore = new ChunkStore();
        PeerServer server = new PeerServer(port, chunkStore, peerId);
        server.start();

        MetadataClient metadataClient = new MetadataClient(HttpClientInstance.getInstance());
        Scheduler scheduler = new Scheduler();
        OriginClient originClient = new OriginClient(HttpClientInstance.getInstance());
        TrackerClient trackerClient = new TrackerClient(HttpClientInstance.getInstance());

        DownloadCoordinator coordinator = new DownloadCoordinator(metadataClient, scheduler, chunkStore, originClient);

        coordinator.start(List.of(songId));

        trackerClient.heartbeatRunner(peerId);


    }
}
