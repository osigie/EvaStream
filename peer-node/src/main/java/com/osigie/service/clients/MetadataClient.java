package com.osigie.service.clients;


import com.osigie.domain.SongDto;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class MetadataClient {
    private static final Logger LOG = Logger.getLogger(MetadataClient.class.getName());

    private final String baseURL = "http://localhost:8081/metadata";

    private final HttpClient client;

    public MetadataClient(HttpClient httpClient) {
        client = httpClient;
    }

    //    TODO:change to uuid
    public CompletableFuture<SongDto> getMetadata(UUID songId) {

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseURL + "/" + songId.toString()))
                .GET()
                .build();

        CompletableFuture<HttpResponse<String>> response = client
                .sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());

        LOG.info("get chunk request sent ....");

        ObjectMapper mapper = new ObjectMapper();
        return response
                .thenApply((r) -> mapper.readValue(r.body(), SongDto.class));


    }

}
