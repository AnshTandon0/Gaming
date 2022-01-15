package com.gaming.community.flexster.model;

public class ModelChat {

    private String sender;
    private String receiver;
    private String msg;
    private String type;
    private String timestamp;
    private String post_id;
    private String win_post_id;
    private String win_type;
    private boolean isSeen;

    public ModelChat(String sender, String receiver, String msg, String type, String timestamp, String post_id, String win_post_id, String win_type, boolean isSeen) {
        this.sender = sender;
        this.receiver = receiver;
        this.msg = msg;
        this.type = type;
        this.timestamp = timestamp;
        this.post_id = post_id;
        this.win_post_id = win_post_id;
        this.win_type = win_type;
        this.isSeen = isSeen;
    }

    public ModelChat() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getWin_post_id() {
        return win_post_id;
    }

    public void setWin_post_id(String win_post_id) {
        this.win_post_id = win_post_id;
    }

    public String getWin_type() {
        return win_type;
    }

    public void setWin_type(String win_type) {
        this.win_type = win_type;
    }
}
