package com.osigie.domain;

public enum NetworkType {
    GET_CHUNK(1),
    CHUNK_DATA(2),
    NOT_FOUND(3);
    private final int value;

    NetworkType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
