package com.gaming.community.flexster.model;

public class ModelGroupChat {

    String sender,msg,type,timestamp,replayId,replayMsg,replayUserId,creater_win_id,win_log_msg,win_post_id,win_type;

    public ModelGroupChat() {
    }

    public ModelGroupChat(String sender, String msg, String type, String timestamp, String replayId, String replayMsg, String replayUserId, String creater_win_id, String win_log_msg, String win_post_id, String win_type) {
        this.sender = sender;
        this.msg = msg;
        this.type = type;
        this.timestamp = timestamp;
        this.replayId = replayId;
        this.replayMsg = replayMsg;
        this.replayUserId = replayUserId;
        this.creater_win_id = creater_win_id;
        this.win_log_msg = win_log_msg;
        this.win_post_id = win_post_id;
        this.win_type = win_type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
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

    public String getReplayId() {
        return replayId;
    }

    public void setReplayId(String replayId) {
        this.replayId = replayId;
    }

    public String getReplayMsg() {
        return replayMsg;
    }

    public void setReplayMsg(String replayMsg) {
        this.replayMsg = replayMsg;
    }

    public String getReplayUserId() {
        return replayUserId;
    }

    public void setReplayUserId(String replayUserId) {
        this.replayUserId = replayUserId;
    }

    public String getCreater_win_id() {
        return creater_win_id;
    }

    public void setCreater_win_id(String creater_win_id) {
        this.creater_win_id = creater_win_id;
    }

    public String getWin_log_msg() {
        return win_log_msg;
    }

    public void setWin_log_msg(String win_log_msg) {
        this.win_log_msg = win_log_msg;
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
