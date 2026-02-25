package com.osigie.origin_server.service;

import com.osigie.origin_server.dto.request.UploadDto;

import java.util.UUID;

public interface PreProcessorService {
    UUID process(UploadDto dto);
}
