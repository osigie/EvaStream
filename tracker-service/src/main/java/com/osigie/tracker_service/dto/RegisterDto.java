package com.osigie.tracker_service.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class RegisterDto {
    UUID peerId;
    String host;
    int port;
}
