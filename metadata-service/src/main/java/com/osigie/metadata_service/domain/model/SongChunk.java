package com.osigie.metadata_service.domain.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "song_chunk")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SongChunk extends BaseModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    private String hash;

    private int index;

    @Builder
    public SongChunk(String hash, Song song, int index, UUID id) {
        this.hash = hash;
        this.song = song;
        this.index = index;
        this.setId(id);
    }

}
