package com.gaming.community.flexster.model;

public class ModelGroupVsFight {

    String category,content,creatore_id,game_name,group1_id,group1_name,group1_won,group2_id,group2_name,group2_won,pId,photo,total_rounds,Status,chk_main_id;

    public ModelGroupVsFight() {

    }

    public ModelGroupVsFight(String category, String content, String creatore_id, String game_name, String group1_id, String group1_name, String group1_won, String group2_id, String group2_name, String group2_won, String pId, String photo, String total_rounds, String status, String chk_main_id) {
        this.category = category;
        this.content = content;
        this.creatore_id = creatore_id;
        this.game_name = game_name;
        this.group1_id = group1_id;
        this.group1_name = group1_name;
        this.group1_won = group1_won;
        this.group2_id = group2_id;
        this.group2_name = group2_name;
        this.group2_won = group2_won;
        this.pId = pId;
        this.photo = photo;
        this.total_rounds = total_rounds;
        Status = status;
        this.chk_main_id = chk_main_id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatore_id() {
        return creatore_id;
    }

    public void setCreatore_id(String creatore_id) {
        this.creatore_id = creatore_id;
    }

    public String getGame_name() {
        return game_name;
    }

    public void setGame_name(String game_name) {
        this.game_name = game_name;
    }

    public String getGroup1_id() {
        return group1_id;
    }

    public void setGroup1_id(String group1_id) {
        this.group1_id = group1_id;
    }

    public String getGroup1_name() {
        return group1_name;
    }

    public void setGroup1_name(String group1_name) {
        this.group1_name = group1_name;
    }

    public String getGroup1_won() {
        return group1_won;
    }

    public void setGroup1_won(String group1_won) {
        this.group1_won = group1_won;
    }

    public String getGroup2_id() {
        return group2_id;
    }

    public void setGroup2_id(String group2_id) {
        this.group2_id = group2_id;
    }

    public String getGroup2_name() {
        return group2_name;
    }

    public void setGroup2_name(String group2_name) {
        this.group2_name = group2_name;
    }

    public String getGroup2_won() {
        return group2_won;
    }

    public void setGroup2_won(String group2_won) {
        this.group2_won = group2_won;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTotal_rounds() {
        return total_rounds;
    }

    public void setTotal_rounds(String total_rounds) {
        this.total_rounds = total_rounds;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getChk_main_id() {
        return chk_main_id;
    }

    public void setChk_main_id(String chk_main_id) {
        this.chk_main_id = chk_main_id;
    }
}
