package com.kaisebhi.kaisebhi.HomeNavigation.home;

import com.google.gson.annotations.SerializedName;

public class AnswersModel {

    @SerializedName("id")
    private String ID;

    @SerializedName("checkOwnQuestion")
    private Boolean checkOwnQuestion;

    @SerializedName("uname")
    private String uname;

    @SerializedName("upro")
    private String upro;

    @SerializedName("likes")
    private String likes;

    @SerializedName("qdesc")
    private String desc;

    @SerializedName("qimg")
    private String qimg;

    @SerializedName("likeCheck")
    private Boolean checkLike;

    @SerializedName("answer")
    private String anser;

    @SerializedName("checkHideAnswer")
    private boolean checkHideAnswer;

    @SerializedName("paidCheck")
    private boolean checkPaid;

    @SerializedName("paidAmount")
    private String paidAmount;

    @SerializedName("selfAnswer")
    private boolean selfAnswer;

    @SerializedName("selfHideAnswer")
    private boolean selfHideAnswer;

    @SerializedName("userReportCheck")
    private boolean userReport;

    public boolean isCheckPaid() {
        return checkPaid;
    }

    public String getID() {
        return ID;
    }


    public String getDesc() {
        return desc;
    }

    public String getQimg() {
        return qimg;
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
        return anser;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public boolean isSelfHideAnswer() {
        return selfHideAnswer;
    }

    public Boolean getCheckLike() {
        return checkLike;
    }

    public Boolean getCheckOwnQuestion() {
        return checkOwnQuestion;
    }

    public Boolean checkHideAnswer() {
        return checkHideAnswer;
    }
    public Boolean isSelfAnswer() {
        return selfAnswer;
    }

    public boolean isUserReport() {
        return userReport;
    }
}




