package com.osigie.origin_server.service.impl;

import com.osigie.origin_server.model.Chunk;
import com.osigie.origin_server.service.OriginServerService;
import org.springframework.stereotype.Service;

@Service
public class OriginServerServiceImpl implements OriginServerService {
    @Override
    public Chunk fetchChunk(Long songId, Long chunkId) {
        return null;
    }
}
