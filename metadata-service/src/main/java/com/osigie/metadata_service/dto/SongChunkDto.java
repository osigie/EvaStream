package com.osigie.metadata_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.util.UUID;

@Data
public class SongChunkDto {
    private UUID id;

    @NotBlank(message = "Hash can not be null or blank")
    private String hash;

    @PositiveOrZero(message = "Index must be 0 or greater")
    private int index;
}
