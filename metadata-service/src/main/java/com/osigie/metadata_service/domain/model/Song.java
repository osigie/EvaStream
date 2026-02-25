package com.osigie.metadata_service.domain.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "songs")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Song extends BaseModel {

    private String title;

    @Column(name = "file_size")
    private long fileSize;

    @Column(name = "chunk_size")
    private long chunkSize;

    @Column(name = "chunk_count")
    private long chunkCount;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<SongChunk> songChunks = new HashSet<SongChunk>();

    @Builder
    public Song(String title, long fileSize, long chunkSize, long chunkCount) {
        this.title = title;
        this.fileSize = fileSize;
        this.chunkSize = chunkSize;
        this.chunkCount = chunkCount;
    }

}

