# google-cloud-storage

## Run tests

```bash
mvn clean test \
  -DskipTests \
  -DPROJECT_ID=${PROJECT_ID}
```

![05_run_tests_1.gif](docs%2Fimg%2F05_run_tests_1.gif)

## Run application

```bash
cd google-cloud-storage
```

### Set environment variables

```bash
export PROJECT_ID=<your-project-id>
export GCS_BUCKET_NAME=<your-gcs-bucket-name>
export IMPERSONATION_TARGET=<your-service-account-email>
export OAUTH_ACCESS_TOKEN=$(gcloud auth print-access-token --impersonate-service-account=${IMPERSONATION_TARGET})
export GKMS_KEY_NAME=<your-gkms-key-name>
```

### Maven CLI

```bash
mvn spring-boot:run \
  -Dspring-boot.run.jvmArguments="\
-XX:+EnableDynamicAgentLoading \
-DPROJECT_ID=$PROJECT_ID \
-DGCS_BUCKET_NAME=$GCS_BUCKET_NAME \
-DIMPERSONATION_TARGET=$IMPERSONATION_TARGET \
-DOAUTH_ACCESS_TOKEN=$(gcloud auth print-access-token \
  --impersonate-service-account=${IMPERSONATION_TARGET}) \
-DGKMS_KEY_NAME=$GKMS_KEY_NAME \
"
```

![04_run-using-maven-cli.gif](docs%2Fimg%2F04_run-using-maven-cli.gif)

### Docker container

#### Build image

```bash
docker build -t google-cloud-storage-ref .
```

#### Run container

```bash
docker run -p 8080:8080 \
  -v "${USER_SVC_ACCOUNT_KEY_PATH}:/sa.json:ro" \
  -e GOOGLE_APPLICATION_CREDENTIALS=/sa.json \
  -e PROJECT_ID \
  -e GCS_BUCKET_NAME \
  -e IMPERSONATION_TARGET \
  -e OAUTH_ACCESS_TOKEN \
  -e GKMS_KEY_NAME \
  google-cloud-storage-ref
```

![06_run_docker_container_1.gif](docs%2Fimg%2F06_run_docker_container_1.gif)
