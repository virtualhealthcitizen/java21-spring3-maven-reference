package org.squidmin.java.spring.maven.gcs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.CompletionException;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadResult {

    private String filename;      // logical filename from DTO
    private String objectName;    // actual GCS object (if known)
    private String signedUrl;     // present on success
    private String error;         // present on failure

    public static UploadResult success(String filename, String objectName, String signedUrl) {
        return new UploadResult(filename, objectName, signedUrl, null);
    }
    public static UploadResult failure(String filename, Throwable t) {
        String msg = (t instanceof CompletionException && t.getCause() != null) ? t.getCause().toString() : t.toString();
        return new UploadResult(filename, null, null, msg);
    }
    public boolean isSuccess() { return error == null; }

}
