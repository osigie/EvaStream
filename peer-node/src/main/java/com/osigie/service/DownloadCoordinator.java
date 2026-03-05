package com.osigie.service;

import com.osigie.networking.PeerClient;
import com.osigie.service.clients.MetadataClient;
import com.osigie.service.clients.OriginClient;

import java.util.List;

public class DownloadCoordinator {
    private final MetadataClient metadataClient;
    private final Scheduler scheduler;
    private final ChunkStore chunkStore;
    private final OriginClient originClient;

    private PeerClient peerClient;

    public DownloadCoordinator(MetadataClient metadataClient, Scheduler scheduler, ChunkStore chunkStore, OriginClient originClient) {
        this.metadataClient = metadataClient;
        this.scheduler = scheduler;
        this.chunkStore = chunkStore;
        this.originClient = originClient;
    }

    /**
     * 1. First get song metadata,
     * 2. Then check if the chunk in local is complete and decide the ones to download from other peers or origin server
     * 3. Download the requested song by searching through peers before defaulting to origin if not found
     * 4. Saving the chunks and telling the origin server that you have gotten chunk
     *
     */

    public void start(List<String> songId) {

        System.out.println("Starting DownloadCoordinator");

    }

}
