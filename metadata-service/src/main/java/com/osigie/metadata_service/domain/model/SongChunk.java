package com.osigie.metadata_service.domain.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "song_chunk")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class SongChunk extends BaseModel {

    private String hash;

    @Builder
    public SongChunk(String hash) {
        this.hash = hash;
    }

}
