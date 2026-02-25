package com.osigie.origin_server.service;


import java.util.UUID;

public interface OriginServerService {

    byte[] fetchChunk(UUID songId, UUID chunkId);

}
