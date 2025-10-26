package org.squidmin.java.spring.maven.gcs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.squidmin.java.spring.maven.gcs.entity.ExampleEntity;

import java.util.List;

@AllArgsConstructor
@Getter
public class FileUploadRequest {

    private final String filename;

    private final List<ExampleEntity> uploadItems;

}
