package com.osigie.origin_server.service.impl;

import com.osigie.origin_server.exceptions.FileNotFoundException;
import com.osigie.origin_server.exceptions.FileProcessorExceptions;
import com.osigie.origin_server.service.OriginServerService;
import com.osigie.origin_server.util.Constant;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class OriginServerServiceImpl implements OriginServerService {
    @Override
    public byte[] fetchChunk(UUID songId, UUID chunkId) {

        Path songDirectoryPath = Path.of(Constant.ORIGIN_STORAGE_PATH + songId.toString() + "/" + chunkId.toString() + ".bin");

        boolean isExist = Files.exists(songDirectoryPath);

        if (!isExist) {
            throw new FileNotFoundException(songDirectoryPath.toString());
        }

        try {
            return Files.readAllBytes(songDirectoryPath);
        } catch (IOException e) {
            throw new FileProcessorExceptions("An error occurred while reading file chunk");
        }
    }
}
