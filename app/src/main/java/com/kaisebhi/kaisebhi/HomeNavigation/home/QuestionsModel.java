package com.kaisebhi.kaisebhi.HomeNavigation.home;

import com.google.gson.annotations.SerializedName;

public class QuestionsModel {

    @SerializedName("id")
    private String ID;

    @SerializedName("title")
    private String Title;

    @SerializedName("desc")
    private String desc;

    @SerializedName("qpic")
    private String qpic;

    @SerializedName("uname")
    private String uname;

    @SerializedName("upro")
    private String upro;

    @SerializedName("favCheck")
    private Boolean checkFav;

    @SerializedName("likes")
    private String likes;

    @SerializedName("likeCheck")
    private Boolean checkLike;

    @SerializedName("answers")
    private String tansers;

    public QuestionsModel(String ID, String title, String desc, String qpic, String uname, String upro, Boolean checkFav, String likes, Boolean checkLike, String tansers) {
        this.ID = ID;
        Title = title;
        this.desc = desc;
        this.qpic = qpic;
        this.uname = uname;
        this.upro = upro;
        this.checkFav = checkFav;
        this.likes = likes;
        this.checkLike = checkLike;
        this.tansers = tansers;
    }

    public String getID() {
        return ID;
    }

    public String getTitle() {
        return Title;
    }

    public String getDesc() {
        return desc;
    }

    public String getQpic() {
        return qpic;
    }

    public String getUname() {
        return uname;
    }

    public String getUpro() {
        return upro;
    }

    public String getLikes() {
        return likes;
    }

    public String getTansers() {
        return tansers;
    }


    public Boolean getCheckFav() {
        return checkFav;
    }
    public Boolean getCheckLike() {
        return checkLike;
    }

}




