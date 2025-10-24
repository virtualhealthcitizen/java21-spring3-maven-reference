# Pub/Sub overview

### Create a topic

```bash
gcloud pubsub topics create my-topic
```

#### Example

```bash
gcloud pubsub topics create java21-spring3-maven-reference-topic
```

### Delete a topic

```bash
gcloud pubsub topics delete my-topic
```

#### Example

```bash
gcloud pubsub topics delete java21-spring3-maven-reference-topic
```

### Create a subscription to a topic

```bash
gcloud pubsub subscriptions create my-subscription --topic=my-topic
```

#### Example

```bash
gcloud pubsub subscriptions create java21-spring3-maven-reference-subscription \
  --topic=java21-spring3-maven-reference-topic \
  --enable-message-ordering
```

### Delete a subscription

```bash
gcloud pubsub subscriptions delete my-subscription
```

#### Example

```bash
gcloud pubsub subscriptions delete java21-spring3-maven-reference-subscription
```

### Publish a message to a topic

```bash
gcloud pubsub topics publish my-topic --message "Hello, World!"
```

#### Example

```bash
gcloud pubsub topics publish java21-spring3-maven-reference-topic --message "Hello."
```

### Pull messages from a subscription

```bash
gcloud pubsub subscriptions pull my-subscription --auto-ack
```

#### Example (echoing the message)

```bash
gcloud pubsub subscriptions pull java21-spring3-maven-reference-subscription --auto-ack
```
