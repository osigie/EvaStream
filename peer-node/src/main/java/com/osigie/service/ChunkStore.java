package com.osigie.service;

import com.osigie.domain.Chunk;
import com.osigie.domain.SongChunkDto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class ChunkStore {
    //TODO: think of better way to handle path and relativity
    private static final String tempPath = "./peer-node/origin-storage/";
    private final String peerId;

    public ChunkStore(String peerId) {
        this.peerId = peerId;
    }

    public void saveChunk(String songId, Chunk chunk) {
        this.saveOriginStorage(songId, chunk);
    }


    public boolean isExist(String songId, String chunkId) {
        Path songDirectoryPath = Path.of(tempPath + "/" + peerId + "/" + songId + "/" + chunkId + ".bin");
        return Files.exists(songDirectoryPath);
    }

    public byte[] loadChunk(String songId, String chunkId) {

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

    public List<byte[]> loadChunk(TreeSet<SongChunkDto> chunks, String peerId, String songId) {
        //TODO: spin multiple threads
        List<byte[]> result = new ArrayList<>();

        chunks.forEach((chunk) -> result.add(this.loadChunk(songId, chunk.getId().toString())));
        return result;
    }


    private void saveOriginStorage(String songId, Chunk chunk) {
        try {

            Path songDirectoryPath = Files
                    .createDirectories(Path.of(tempPath + peerId + "/" + songId));
            Path chunkFilePath = songDirectoryPath
                    .resolve(chunk.getId().toString() + ".bin");

            Files.write(chunkFilePath, chunk.getChunk());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
