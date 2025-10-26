package org.squidmin.java.spring.maven.gcs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.net.URL;

@AllArgsConstructor
@Data
@Builder
@Getter
public class FileUploadResponse {

    private URL url;

}
