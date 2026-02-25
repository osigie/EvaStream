package com.osigie.origin_server.service.external;


import com.osigie.origin_server.dto.external.SongDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class MetadataService {

    public RestClient getRestClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:8081")
                .build();
    }

    public SongDto createSong(SongDto songDto) {
        RestClient restClient = getRestClient();
        return restClient.post()
                .uri("/metadata")
                .body(songDto)
                .retrieve()
                .body(SongDto.class);

    }
}
