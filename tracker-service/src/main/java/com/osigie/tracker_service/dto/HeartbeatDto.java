package com.osigie.tracker_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class HeartbeatDto {
    @NotBlank(message = "PeerId cannot be null or blank")
    private String peerId;
}
