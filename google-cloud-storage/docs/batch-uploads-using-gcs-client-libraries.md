# Batch file uploads using GCS client libraries

The `google-cloud-storage` Java client’s `StorageBatch` only batches metadata ops (update/patch/delete, ACL changes, etc.).
Uploading multiple files in one request using the client library for Java is not supported.

Media uploads (creating objects with content) use the upload protocols (multipart or resumable) and can’t be combined into a single HTTP batch request.
In a Java application, you’ll need to upload each file separately — ideally in parallel for throughput.

### Batch updating metadata

Below is an example of using the GCS client library for Java to update the metadata of five JSON files stored under the `batch_uploads/` prefix.

![Batch update GCS object metadata](./img/12_batch-update-gcs-object-metadata.gif)

### Batch uploading files

To upload multiple files, you can use Java's concurrency features to upload files in parallel.

![Batch upload multiple files in parallel to GCS](./img/13_batch-upload-to-gcs-parallel.gif)
