package com.osigie.origin_server.service;

import com.osigie.origin_server.model.Chunk;

public interface OriginServerService {

    Chunk fetchChunk(Long songId, Long chunkId);

}
