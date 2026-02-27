package com.osigie.tracker_service.service.impl;

import com.osigie.tracker_service.domain.PeerInfo;
import com.osigie.tracker_service.dto.ChunkAcquiredDto;
import com.osigie.tracker_service.service.TrackerService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TrackerServiceImpl implements TrackerService {

    private final Map<UUID, PeerInfo> peerRegistry = new ConcurrentHashMap<>();
    private final Map<UUID, Map<UUID, Set<UUID>>> songChunkMap = new ConcurrentHashMap<>();

    @Override
    public void register(PeerInfo peerInfo) {
        peerInfo.setLastHeartbeat(System.currentTimeMillis());
        peerRegistry.put(peerInfo.getPeerId(), peerInfo);
    }

    @Override
    public void heartbeat(UUID peerId) {
        PeerInfo peer = peerRegistry.get(peerId);
        if (peer != null) {
            peer.setLastHeartbeat(System.currentTimeMillis());
        }
    }


    @Override
    public Map<UUID, Set<UUID>> getSongPeers(UUID peerId) {
        return songChunkMap.computeIfAbsent(peerId, k -> new HashMap<>());
    }

    @Override
    public void chunkAcquired(ChunkAcquiredDto dto) {
        songChunkMap
                .computeIfAbsent(dto.getSongId(), k -> new ConcurrentHashMap<>())
                .computeIfAbsent(dto.getChunkId(), k -> ConcurrentHashMap.newKeySet())
                .add(dto.getPeerId());
    }
}
