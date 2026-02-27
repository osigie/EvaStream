package com.osigie.tracker_service.service;

import com.osigie.tracker_service.domain.PeerInfo;
import com.osigie.tracker_service.dto.ChunkAcquiredDto;

import java.util.Map;
import java.util.UUID;

public interface TrackerService {

    void register(PeerInfo peerInfo);

    void heartbeat(String peerId);

    Map<UUID, Map<String, PeerInfo>> getSongPeers(UUID songId);

    void chunkAcquired(ChunkAcquiredDto dto);
}
