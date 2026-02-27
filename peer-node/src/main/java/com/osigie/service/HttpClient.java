package com.osigie.service;

import java.time.Duration;

public class HttpClient {

    private static volatile java.net.http.HttpClient instance;

    public static java.net.http.HttpClient getInstance() {
        if (instance == null) {
            synchronized (HttpClient.class) {
                if (instance == null) {
                    instance = java.net.http.HttpClient.newBuilder()
                            .connectTimeout(Duration.ofMinutes(2))
                            .build();
                }
            }
        }
        return instance;
    }
}
