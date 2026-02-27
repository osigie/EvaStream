package com.osigie.tracker_service.service.impl;

import com.osigie.tracker_service.domain.PeerInfo;
import com.osigie.tracker_service.dto.ChunkAcquiredDto;
import com.osigie.tracker_service.service.TrackerService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TrackerServiceImpl implements TrackerService {

    private final Map<String, PeerInfo> peerRegistry = new ConcurrentHashMap<>();
    private final Map<UUID, Map<UUID, Set<String>>> songChunkMap = new ConcurrentHashMap<>();

    @Override
    public void register(PeerInfo peerInfo) {
        peerInfo.setLastHeartbeat(System.currentTimeMillis());
        peerRegistry.put(peerInfo.getPeerId(), peerInfo);
    }

    @Override
    public void heartbeat(String peerId) {
        PeerInfo peer = peerRegistry.get(peerId);
        if (peer != null) {
            peer.setLastHeartbeat(System.currentTimeMillis());
        }
    }


    @Override
    public Map<UUID, Map<String, PeerInfo>> getSongPeers(UUID songId) {
        Map<UUID, Set<String>> chunkMap = songChunkMap.get(songId);
        if (chunkMap == null) {
            return Collections.emptyMap();
        }

        Map<UUID, Map<String, PeerInfo>> result = new HashMap<>();
        for (Map.Entry<UUID, Set<String>> entry : chunkMap.entrySet()) {
            UUID chunkId = entry.getKey();
            Set<String> peerIds = entry.getValue();
            Map<String, PeerInfo> peerMap = new HashMap<>();
            for (String peerId : peerIds) {
                PeerInfo peerInfo = peerRegistry.get(peerId);
                if (peerInfo != null) {
                    peerMap.put(peerId, peerInfo);
                }
            }
            result.put(chunkId, peerMap);
        }
        return result;
    }

    @Override
    public void chunkAcquired(ChunkAcquiredDto dto) {
        boolean hasPeerRegistered = peerRegistry.containsKey(dto.getPeerId());

        if (!hasPeerRegistered) {
            throw new RuntimeException("Peer not registered");
        }
        songChunkMap
                .computeIfAbsent(dto.getSongId(), k -> new ConcurrentHashMap<>())
                .computeIfAbsent(dto.getChunkId(), k -> ConcurrentHashMap.newKeySet())
                .add(dto.getPeerId());
    }
}
