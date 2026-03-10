package com.osigie.tracker_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class RegisterDto {

    @NotBlank(message = "PeerId cannot be null or blank")
    private String peerId;

    @NotBlank(message = "Host cannot be null or blank")
    private String host;

    @Min(value = 1, message = "Port must be greater than 0")
    private int port;
}
