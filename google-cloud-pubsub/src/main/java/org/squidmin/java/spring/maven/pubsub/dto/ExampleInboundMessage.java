package org.squidmin.java.spring.maven.pubsub.dto;

public sealed abstract class ExampleInboundMessage permits TextMessagePayload, PingMessagePayload, PongMessagePayload {

}
