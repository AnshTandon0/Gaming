package com.gaming.community.flexster.model;

public class ModelVsFight
{
    String category,creatore_id,creatore_won,game_name,pId,total_rounds,user2_id,user2_won,won_round,content,Status,chk_main_id;

    public ModelVsFight() {

    }

    public ModelVsFight(String category, String creatore_id, String creatore_won, String game_name, String pId, String total_rounds, String user2_id, String user2_won, String won_round, String content, String status, String chk_main_id) {
        this.category = category;
        this.creatore_id = creatore_id;
        this.creatore_won = creatore_won;
        this.game_name = game_name;
        this.pId = pId;
        this.total_rounds = total_rounds;
        this.user2_id = user2_id;
        this.user2_won = user2_won;
        this.won_round = won_round;
        this.content = content;
        Status = status;
        this.chk_main_id = chk_main_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCreatore_id() {
        return creatore_id;
    }

    public void setCreatore_id(String creatore_id) {
        this.creatore_id = creatore_id;
    }

    public String getCreatore_won() {
        return creatore_won;
    }

    public void setCreatore_won(String creatore_won) {
        this.creatore_won = creatore_won;
    }

    public String getGame_name() {
        return game_name;
    }

    public void setGame_name(String game_name) {
        this.game_name = game_name;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getTotal_rounds() {
        return total_rounds;
    }

    public void setTotal_rounds(String total_rounds) {
        this.total_rounds = total_rounds;
    }

    public String getUser2_id() {
        return user2_id;
    }

    public void setUser2_id(String user2_id) {
        this.user2_id = user2_id;
    }

    public String getUser2_won() {
        return user2_won;
    }

    public void setUser2_won(String user2_won) {
        this.user2_won = user2_won;
    }

    public String getWon_round() {
        return won_round;
    }

    public void setWon_round(String won_round) {
        this.won_round = won_round;
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
