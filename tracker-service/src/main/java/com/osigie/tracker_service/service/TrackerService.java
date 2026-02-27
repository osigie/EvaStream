package com.osigie.tracker_service.service;

import com.osigie.tracker_service.domain.PeerInfo;
import com.osigie.tracker_service.dto.ChunkAcquiredDto;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface TrackerService {

    void register(PeerInfo peerInfo);

    void heartbeat(UUID peerId);

    Map<UUID, Set<UUID>> getSongPeers(UUID songId);

    void chunkAcquired(ChunkAcquiredDto dto);
}
