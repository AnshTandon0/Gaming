package com.gaming.community.flexster.model;

public class CustomeGifModel {

    String timestamp,type,uri;

    public CustomeGifModel() {
    }

    public CustomeGifModel(String timestamp, String type, String uri) {
        this.timestamp = timestamp;
        this.type = type;
        this.uri = uri;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
