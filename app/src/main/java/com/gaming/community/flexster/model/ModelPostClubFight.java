package com.gaming.community.flexster.model;

public class ModelPostClubFight {

    String category,content,creatore_id,game_name,group_id,group_won,pId,scrim_type,status,total_rounds,user_won;

    ModelPostClubFight(){

    }

    public ModelPostClubFight(String category, String content, String creatore_id, String game_name, String group_id, String group_won, String pId, String scrim_type, String status, String total_rounds, String user_won) {
        this.category = category;
        this.content = content;
        this.creatore_id = creatore_id;
        this.game_name = game_name;
        this.group_id = group_id;
        this.group_won = group_won;
        this.pId = pId;
        this.scrim_type = scrim_type;
        this.status = status;
        this.total_rounds = total_rounds;
        this.user_won = user_won;
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

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getGroup_won() {
        return group_won;
    }

    public void setGroup_won(String group_won) {
        this.group_won = group_won;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getScrim_type() {
        return scrim_type;
    }

    public void setScrim_type(String scrim_type) {
        this.scrim_type = scrim_type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotal_rounds() {
        return total_rounds;
    }

    public void setTotal_rounds(String total_rounds) {
        this.total_rounds = total_rounds;
    }

    public String getUser_won() {
        return user_won;
    }

    public void setUser_won(String user_won) {
        this.user_won = user_won;
    }
}
