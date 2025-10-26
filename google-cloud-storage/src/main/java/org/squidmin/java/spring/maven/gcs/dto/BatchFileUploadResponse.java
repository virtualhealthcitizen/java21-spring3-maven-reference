package org.squidmin.java.spring.maven.gcs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchFileUploadResponse {

    private int totalRequested;
    private int successCount;
    private int failureCount;
    private List<UploadResult> results;

}
