package org.squidmin.java.spring.maven.pubsub.model;

public class PingMessage {

    private String text;
    private String timestamp;

    public PingMessage(String text, String timestamp) {
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getId() {
        return text;
    }

    public void setId(String text) {
        this.text = text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
