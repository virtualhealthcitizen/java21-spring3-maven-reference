package org.squidmin.java.spring.maven.casestudies.casestudy1.dto;

import lombok.*;
import org.squidmin.java.spring.maven.casestudies.casestudy1.domain.Widget;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class WidgetPayload extends ExampleInboundMessage {

    private String eventId;
    private String timestamp;
    private String messageType;

    private List<Widget> records;

}
