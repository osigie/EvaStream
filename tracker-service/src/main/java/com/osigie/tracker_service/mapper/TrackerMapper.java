package com.osigie.tracker_service.mapper;

import com.osigie.tracker_service.domain.PeerInfo;
import com.osigie.tracker_service.dto.RegisterDto;
import org.springframework.stereotype.Component;


@Component
public class TrackerMapper {
    public PeerInfo mapToPeerInfo(RegisterDto registerDto) {
        PeerInfo peerInfo = new PeerInfo();
        peerInfo.setPeerId(registerDto.getPeerId());
        peerInfo.setHost(registerDto.getHost());
        peerInfo.setPort(registerDto.getPort());
        return peerInfo;

    }

}


