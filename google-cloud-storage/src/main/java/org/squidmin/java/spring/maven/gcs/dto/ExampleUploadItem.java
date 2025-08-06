package org.squidmin.java.spring.maven.gcs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class ExampleUploadItem {

    private String id;

    @JsonProperty("creation_timestamp")
    private String creationTimestamp;

    @JsonProperty("last_update_timestamp")
    private String lastUpdateTimestamp;

    @JsonProperty("column_a")
    private String columnA;

    @JsonProperty("column_b")
    private String columnB;

}
