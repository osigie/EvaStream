package com.osigie.service;

import com.osigie.domain.Chunk;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ChunkStore {
    //TODO: think of better way to handle path and relativity
    private static final String tempPath = "./peer-node/origin-storage/";

    public void saveChunk(String peerId, String songId, Chunk chunk) {
        this.saveOriginStorage(peerId, songId, chunk);
    }


    public byte[] loadChunk(String peerId, String songId, String chunkId) {

        Path songDirectoryPath = Path.of(tempPath + "/" + peerId + "/" + songId + "/" + chunkId + ".bin");

        if (!Files.exists(songDirectoryPath)) {
            return null;
        }

        try {
            return Files.readAllBytes(songDirectoryPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void saveOriginStorage(String peerId, String songId, Chunk chunk) {
        try {

            Path songDirectoryPath = Files
                    .createDirectories(Path.of(tempPath + peerId + "/" + songId + ".bin"));
            Path chunkFilePath = songDirectoryPath
                    .resolve(chunk.getId().toString() + ".bin");

            Files.write(chunkFilePath, chunk.getChunk());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
