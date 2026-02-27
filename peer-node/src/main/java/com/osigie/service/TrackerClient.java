package com.osigie.service;

import com.osigie.domain.ChunkAcquired;
import com.osigie.domain.PeerInfo;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;


public class TrackerClient {
    private static final Logger LOG = Logger.getLogger(TrackerClient.class.getName());
    private final HttpClient client;

    //TODO: move to env
    private final String baseURL = "http://localhost:8082/tracker";

    public TrackerClient(HttpClient httpClient) {
        client = httpClient;
    }

    public CompletableFuture<String> register(PeerInfo peerInfo) {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(peerInfo);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseURL + "/register"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();

        CompletableFuture<HttpResponse<String>> response = client
                .sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());

        LOG.info("registration request sent ....");

        return response.thenApply(HttpResponse::body);
    }

    public CompletableFuture<String> heartbeat(String peerId) {
        Map<String, String> peerMap = new HashMap<>();
        peerMap.put("peerId", peerId);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(peerMap);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseURL + "/heartbeat"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();

        CompletableFuture<HttpResponse<String>> response = client
                .sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());

        LOG.info("heartbeat request sent ....");

        return response.thenApply(HttpResponse::body);
    }


    public CompletableFuture<String> getPeers(UUID songId) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseURL + "/peers?songId=" + songId.toString()))
                .GET()
                .build();

        CompletableFuture<HttpResponse<String>> response = client
                .sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());

        LOG.info("get peers request sent ....");

        return response.thenApply(HttpResponse::body);
    }

    public CompletableFuture<String> notifyChuckAcquired(ChunkAcquired chunkAcquired) {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(chunkAcquired);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseURL + "/chunk-acquired"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();

        CompletableFuture<HttpResponse<String>> response = client
                .sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());

        LOG.info("chunk acquired notification request sent ....");

        return response.thenApply(HttpResponse::body);
    }


}
