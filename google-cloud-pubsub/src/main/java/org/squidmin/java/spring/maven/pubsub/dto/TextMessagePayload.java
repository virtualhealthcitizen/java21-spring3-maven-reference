package org.squidmin.java.spring.maven.pubsub.dto;

import lombok.*;
import org.squidmin.java.spring.maven.pubsub.model.TextMessage;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class TextMessagePayload extends ExampleInboundMessage {

    private String messageId;
    private String messageType;

    private List<TextMessage> records;

}
