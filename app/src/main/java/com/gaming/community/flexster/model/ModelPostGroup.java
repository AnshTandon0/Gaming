package com.gaming.community.flexster.model;

public class ModelPostGroup {

    String id, group, pId, text, type, meme, vine, pTime;

    public ModelPostGroup() {
    }

    public ModelPostGroup(String id, String group, String pId, String text, String type, String meme, String vine, String pTime) {
        this.id = id;
        this.group = group;
        this.pId = pId;
        this.text = text;
        this.type = type;
        this.meme = meme;
        this.vine = vine;
        this.pTime = pTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMeme() {
        return meme;
    }

    public void setMeme(String meme) {
        this.meme = meme;
    }

    public String getVine() {
        return vine;
    }

    public void setVine(String vine) {
        this.vine = vine;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }
}
