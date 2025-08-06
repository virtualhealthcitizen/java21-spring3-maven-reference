package org.squidmin.java.spring.maven.gcs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ExampleRequest {

    private final String filename;

    private final List<ExampleUploadItem> uploadItems;

}
