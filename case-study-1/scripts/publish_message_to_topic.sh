# Set vars (adjust to your values)
PROJECT_ID="lofty-root-378503"
TOPIC_ID="java21-spring3-maven-reference-topic"

# Payload must be base64. On macOS:
#DATA_BASE64="$(printf '{"id":"9a7b127e-23b5-48a1-93dc-03c5c619c6b3","name":"Full Widget","createdAt":"2023-10-01T12:00:00Z","meta":{"color":"blue","size":"large","features":["waterproof","shockproof"]}}' | base64 | tr -d '\n')"
DATA_BASE64="$(printf '{"eventId":"9a7b127e-23b5-48a1-93dc-03c5c619c6b3","timestamp":"2023-10-01T12:00:00Z","messageType":"WIDGET_CREATED","records":[{"id":"9a7b127e-23b5-48a1-93dc-03c5c619c6b3","name":"Full Widget","createdAt":"2023-10-01T12:00:00Z","meta":{"color":"blue","size":"large","features":["waterproof","shockproof"]}}]}' | base64 | tr -d '\n')"

# Get an OAuth2 access token via gcloud
ACCESS_TOKEN="$(gcloud auth print-access-token)"

# Publish
curl -sS -X POST \
  "https://pubsub.googleapis.com/v1/projects/${PROJECT_ID}/topics/${TOPIC_ID}:publish" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}" \
  -H "Content-Type: application/json; charset=utf-8" \
  -d '{
        "messages": [
          {
            "data": "'"${DATA_BASE64}"'",
            "attributes": {
              "source": "case-study-1",
              "env": "dev"
            }
          }
        ]
      }'
