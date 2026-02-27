package com.osigie.tracker_service.domain;

import lombok.Data;

import java.util.UUID;

@Data
public class PeerInfo {
    UUID peerId;
    String host;
    int port;
    long lastHeartbeat;
}
