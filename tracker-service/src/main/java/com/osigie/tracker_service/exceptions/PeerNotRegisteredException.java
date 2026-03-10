package com.osigie.tracker_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PeerNotRegisteredException extends RuntimeException {

    public PeerNotRegisteredException(String peer) {
        super(String.format("Peer with id: %s is not registered", peer));
    }
}
