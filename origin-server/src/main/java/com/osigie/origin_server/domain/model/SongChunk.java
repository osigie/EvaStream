package com.osigie.metadata_service.domain.model;


import jakarta.persistence.*;
import lombok.*;

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

    @Builder
    public SongChunk(String hash, Song song) {
        this.hash = hash;
        this.song = song;
    }

}
