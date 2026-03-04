package com.osigie.domain;

import java.util.UUID;

public record ChunkAcquired(
        String peerId,
        UUID chunkId,
        UUID songId
) {

}
