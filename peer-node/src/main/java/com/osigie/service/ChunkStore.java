package com.osigie.service;

import com.osigie.domain.Chunk;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class ChunkStore {
    //TODO: think of better way to handle path and relativity
    private static final String tempPath = "./peer-node/origin-storage/";

    void saveChunk(String peerId, UUID songId, Chunk chunk) {
        this.saveOriginStorage(peerId, songId, chunk);
    }

    boolean hasChunk(String peerId, UUID chunkId) {
        Path songDirectoryPath = Path.of(tempPath + peerId + "/" + chunkId.toString() + ".bin");
        return Files.exists(songDirectoryPath);
    }

    byte[] loadChunk(String peerId, UUID chunkId) {

        Path songDirectoryPath = Path.of(tempPath + peerId + "/" + chunkId.toString() + ".bin");
        try {
            return Files.readAllBytes(songDirectoryPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void saveOriginStorage(String peerId, UUID songId, Chunk chunk) {
        //create song directory


        //then I can start saving the chunks in the directory

        //spin multiple threads to save this chunks later
//        chunks.forEach(chunk -> {
//            try {
//                Path chunkFilePath = songDirectoryPath.resolve(chunk.getId().toString() + ".bin");
//                Files.write(chunkFilePath, chunk.getChunk());
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        });

        try {

            Path songDirectoryPath = Files.createDirectories(Path.of(tempPath + peerId));
            Path chunkFilePath = songDirectoryPath.resolve(chunk.getId().toString() + ".bin");

            Files.write(chunkFilePath, chunk.getChunk());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
