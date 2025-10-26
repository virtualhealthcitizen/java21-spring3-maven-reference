package org.squidmin.java.spring.maven.gcs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BatchFileUploadRequest {

    private List<FileUploadRequest> fileUploadRequests;

}
