package com.osigie.origin_server.service.impl;

import com.osigie.origin_server.dto.external.SongChunkDto;
import com.osigie.origin_server.dto.external.SongDto;
import com.osigie.origin_server.dto.request.UploadDto;
import com.osigie.origin_server.model.Chunk;
import com.osigie.origin_server.service.PreProcessorService;
import com.osigie.origin_server.service.external.MetadataService;
import com.osigie.origin_server.util.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.nio.file.Files;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PreProcessorServiceImpl implements PreProcessorService {
    private final MetadataService metadataService;

    public PreProcessorServiceImpl(MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @Override
    public UUID process(UploadDto dto) {
        int chunk_size = 8192; //8kb
        List<Chunk> chunks = new ArrayList<>();

        try (InputStream inputStream = dto.getFile().getInputStream()) {
            byte[] buffer = new byte[chunk_size];
            int bytesRead;
            int chunkIndex = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                Chunk chunk = processChunk(buffer, bytesRead, chunkIndex);

                log.debug("Processing chunk {} of {}", chunkIndex, bytesRead);

                chunks.add(chunk);
                chunkIndex++;
            }

            //create song
            return this.forwardSong(dto.getTitle(), dto.getFile().getSize(), chunk_size, chunks);

        } catch (IOException e) {
            throw new RuntimeException("An error occurred while processing the file");
        }
    }

    private UUID forwardSong(String title, long fileSize, int chunk_size, List<Chunk> chunks) {
// now i am going to make a decision if i will
// save the raw byte in disk first before
// sending to metadata service since i can precompute the id, lemme send to metadata service for now

        Set<SongChunkDto> songChunkDtoSet = chunks.stream().map((chunk -> {
            SongChunkDto preProcessSongChunk = new SongChunkDto();
            preProcessSongChunk.setHash(chunk.getHash());
            preProcessSongChunk.setIndex(chunk.getIndex());
            preProcessSongChunk.setId(chunk.getId());
            return preProcessSongChunk;
        })).collect(Collectors.toSet());

        SongDto preProcessSong = new SongDto();
        preProcessSong.setSongChunks(songChunkDtoSet);
        preProcessSong.setTitle(title);
        preProcessSong.setChunkSize(chunk_size);
        preProcessSong.setChunkCount(songChunkDtoSet.size());
        preProcessSong.setFileSize(fileSize);

        SongDto savedSong = this.metadataService.createSong(preProcessSong);

        //use saved song to store in local file
        try {
            this.saveOriginStorage(savedSong.getId(), chunks);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return savedSong.getId();
    }

    private void saveOriginStorage(UUID songId, List<Chunk> chunks) throws IOException {
        //create song directory

        Path songDirectoryPath = Files.createDirectories(Path.of(Constant.ORIGIN_STORAGE_PATH + songId.toString()));

        //then I can start saving the chunks in the directory

        //spin multiple threads to save this chunks later
        chunks.forEach(chunk -> {
            try {
                Path chunkFilePath = songDirectoryPath.resolve(chunk.getId().toString() + ".bin");
                Files.write(chunkFilePath, chunk.getChunk());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Chunk processChunk(byte[] buffer, int bytesRead, int chunkIndex) {
        //for case where it's at the end
        byte[] actualChunk = new byte[bytesRead];
        System.arraycopy(buffer, 0, actualChunk, 0, bytesRead);

        Chunk chunk = new Chunk();
        chunk.setIndex(chunkIndex);
        chunk.setChunk(actualChunk);
        chunk.setId(UUID.randomUUID());
        chunk.setHash(this.hash(actualChunk));
        return chunk;
    }


    public String hash(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
