package com.osigie.origin_server.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadDto {
    @NotNull(message = "File can not be null")
    private MultipartFile file;

    @NotBlank(message = "Title can not be null or blank")
    private String title;
}
