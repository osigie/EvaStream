package com.osigie.tracker_service.domain;

import lombok.Data;

import java.util.UUID;

@Data
public class PeerInfo {
    private String peerId;
    private String host;
    private int port;
    private long lastHeartbeat;
}
