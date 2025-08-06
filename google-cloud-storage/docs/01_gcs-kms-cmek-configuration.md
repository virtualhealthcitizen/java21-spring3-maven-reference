# Configuring GCS with KMS for Customer-Managed Encryption Keys (CMEK)

## Configure GCP resources using Terraform IaC

### In `variables.tf`:

```hcl
variable "cmek_test_bucket_name" {
  description = "The name of the bucket used for testing CMEK"
  default     = "jm_lofty-root-cmek-test"
}
```

### Create a service account

```hcl
resource "google_service_account" "cmek_test_sa" {
  account_id   = "gcs-cmek-test-sa"
  display_name = "GCS CMEK test service account"
  project      = var.project_id
}
```

### Configure service account impersonation target

```hcl
/* This allows a user to impersonate a service account */
resource "google_service_account_iam_member" "cmek_test_sa_allow_user_impersonation" {
  service_account_id = google_service_account.cmek_test_sa.id
  role               = "roles/iam.serviceAccountTokenCreator"
  member            = "user:${var.project_admin_user_email}"
}
```

### Enable the KMS API; create a Key Ring and Crypto Key

```hcl
resource "google_project_service" "kms" {
  project = var.project_id
  service = "cloudkms.googleapis.com"
}

# Create a Key Ring
resource "google_kms_key_ring" "my_key_ring" {
  project = var.project_id
  name     = "my-key-ring"
  location = "us"
}

# Create a Crypto Key (CMK)
resource "google_kms_crypto_key" "my_crypto_key" {
  name            = "my-key-2"
  key_ring        = google_kms_key_ring.my_key_ring.id
  rotation_period = "2592000s" # 30 days
}

/* [START] Grant permission for impersonation target to encrypt */
resource "google_kms_crypto_key_iam_member" "gcs_crypto_user" {
  crypto_key_id = google_kms_crypto_key.my_crypto_key.id
  role          = "roles/cloudkms.cryptoKeyEncrypterDecrypter"
  member        = "serviceAccount:${google_service_account.cmek_test_sa.email}"
}
resource "google_kms_crypto_key_iam_member" "gcs_sa_kms_access" {
  crypto_key_id = google_kms_crypto_key.my_crypto_key.id
  role          = "roles/cloudkms.cryptoKeyEncrypterDecrypter"
  member        = "serviceAccount:service-${data.google_project.project.number}@gs-project-accounts.iam.gserviceaccount.com"
}
/* [END] Grant permission for impersonation target to encrypt */

data "google_project" "project" {
  project_id = var.project_id
}
```

### Create a GCS bucket with CMEK

```hcl
resource "google_storage_bucket" "cmek_test_bucket" {
  project                     = var.project_id
  name                        = var.cmek_test_bucket_name
  location                    = "us"
  storage_class               = "STANDARD"
  uniform_bucket_level_access = true
  encryption {
    default_kms_key_name = google_kms_crypto_key.my_crypto_key.id
  }
}
```

### Grant read / write access to the bucket for the target service account

```hcl
/* [START] GCP storage bucket IAM member */
resource "google_storage_bucket_iam_member" "cmek_test_sa_bucket_reader" {
  bucket = google_storage_bucket.cmek_test_bucket.name
  role   = "roles/storage.legacyBucketReader"
  member = "serviceAccount:${google_service_account.cmek_test_sa.email}"
}

resource "google_storage_bucket_iam_member" "cmek_test_sa_object_creator" {
  bucket = google_storage_bucket.cmek_test_bucket.name
  role   = "roles/storage.objectCreator"
  member = "serviceAccount:${google_service_account.cmek_test_sa.email}"
}
/* [END] GCP storage bucket IAM member */
```

## Verify resource creation using `gcloud` CLI

### Verify the key exists

```bash
gcloud kms keys list \
  --keyring=my-key-ring \
  --location=us
```

![02_list_keys_1.gif](img%2F02_list_keys_1.gif)

### List buckets to confirm the CMEK bucket was created

```bash
gcloud storage buckets list --filter="name:jm_lofty-root-cmek-test"
```

![00_list_buckets_1.gif](img%2F00_list_buckets_1.gif)

### Describe the bucket and check the encryption key

```bash
gcloud storage buckets describe gs://jm_lofty-root-cmek-test
```

![01_describe_buckets_1.gif](img%2F01_describe_buckets_1.gif)

## Submitting a request

### `/gcs/upload` request (see Bruno collection)

![03_submit_request_1.gif](img%2F03_submit_request_1.gif)

---

## Notes and caveats

### Does the `uploadAvro()` method allow you to download the file?

The `uploadAvro()` method in the provided code snippet uploads an Avro file to a Google Cloud Storage (GCS) bucket using a Customer-Managed Encryption Key (CMEK).

The `URL` returned from the `uploadAvro()` method **does allow you to download the file**, but only if you're using the returned URL directly.

![avro-file-download_1.gif](..%2F..%2F..%2F..%2F09_videos%2Fcoding%2Favro-file-download_1.gif)

The method

```java
return storage.signURL(blobInfo, 5, TimeUnit.MINUTES, Storage.SignUrlOption.withV4Signature());
```

Creates a V4 signed URL that is:

- Time-limited (5 minutes)
- Authenticated using impersonated credentials
- Grants temporary access to download the uploaded object

So you can use this URL in a browser or HTTP client (e.g., `curl`, `wget`, Postman) to download the Avro file without writing a separate `downloadAvro()` method.



This signed URL grants GET access to the object only.
No additional decryption step is needed when downloading – GCS will automatically decrypt the file using the CMEK (Customer-Managed Encryption Key) if the IAM roles and key access are properly configured.
This signed URL bypasses the need for your users to authenticate, making it ideal for temporary download links.

### Decryption

The client does not need to perform any additional steps for encryption or decryption when using the signed URL returned by the `uploadAvro()` method, assuming the CMEK is configured correctly on the server side.
When a file is uploaded with a CMEK (Customer-Managed Encryption Key), Google Cloud automatically decrypts it upon download if the key and IAM permissions are valid.
A V4 signed URL authorizes access without requiring the client to authenticate or have IAM roles.
This includes access to encrypted files, as long as:

- the signing credentials (your service account) have **Cloud KMS CryptoKey Decrypter** permission
- the key is still valid and enabled

Encryption happens server-side. The syntax:

```java
Storage.BlobTargetOption.kmsKeyName(gcsConfig.getGkmsKeyName())
```

ensures CMEK is used during the upload.
Server-side CMEK usage can be verified by viewing the Javadocs in the source code for the `Storage` class [here](https://github.com/googleapis/java-storage/blob/main/google-cloud-storage/src/main/java/com/google/cloud/storage/Storage.java#L1546):

![07_kms-key-name-method.png](img%2F07_kms-key-name-method.png)

#### What you need to ensure (on the server-side)

- The impersonated service account:
  - Has access to the GCS bucket (`roles/storage.objectAdmin` or similar)
  - Has access to the CMEK (`roles/cloudkms.cryptoKeyEncrypterDecrypter`)
- The CMEK is:
  - Enabled
  - Not rotated in a way that breaks access
