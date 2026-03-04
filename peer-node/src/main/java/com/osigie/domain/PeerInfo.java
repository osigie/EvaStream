package com.osigie.domain;

public record PeerInfo(
        String peerId,
        String host,
        int port
) {
}

