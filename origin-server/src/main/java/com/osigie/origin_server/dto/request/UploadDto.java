package com.osigie.origin_server.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadDto {
    private MultipartFile file;
    private String title;
}
