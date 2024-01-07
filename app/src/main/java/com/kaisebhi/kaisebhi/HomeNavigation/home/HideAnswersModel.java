package com.kaisebhi.kaisebhi.HomeNavigation.home;

import com.google.gson.annotations.SerializedName;

public class HideAnswersModel {

    @SerializedName("ques")
    private String ques;


    @SerializedName("qdesc")
    private String desc;

    @SerializedName("qimg")
    private String qimg;

    @SerializedName("ans")
    private String ans;

    @SerializedName("author")
    private String author;

    @SerializedName("thumb")
    private String thumb;
    private String portal, audioUrl;

    public String getPortal() {
        return portal;
    }

    public void setPortal(String portal) {
        this.portal = portal;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public HideAnswersModel(String ques, String desc, String qimg, String ans, String author, String thumb) {
        this.ques = ques;
        this.desc = desc;
        this.qimg = qimg;
        this.ans = ans;
        this.author = author;
        this.thumb = thumb;
    }

    public String getQimg() {
        return qimg;
    }

    public void setQimg(String qimg) {
        this.qimg = qimg;
    }

    public String getQues() {
        return ques;
    }

    public String getDesc() {
        return desc;
    }

    public String getAns() {
        return ans;
    }

    public String getThumb() {
        return thumb;
    }

    public String getAuthor() {
        return author;
    }
}




