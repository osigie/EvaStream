package com.osigie.service.clients;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class OriginClient {
    private static final Logger LOG = Logger.getLogger(OriginClient.class.getName());
    private final HttpClient client;

    //TODO: move to env
    private final String baseURL = "http://localhost:8083";

    public OriginClient(HttpClient httpClient) {
        client = httpClient;
    }


    public CompletableFuture<byte[]> fetchChunk(String chunkId, String songId) {

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseURL + "/chunk?songId=" + songId + "&chunkId=" + chunkId))
                .GET()
                .header("Content-Type", "application/octet-stream")
                .build();

        CompletableFuture<HttpResponse<byte[]>> response = client
                .sendAsync(httpRequest, HttpResponse.BodyHandlers.ofByteArray());

        LOG.info("get chunk request sent ....");

        return response.thenApply((HttpResponse::body));


    }

}
