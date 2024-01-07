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

    @SerializedName("title")
    private String questionsTitle;
    private String answerDocId;

    private String reportBy, likedBy, userId, portal, audioUrl;

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

    public AnswersModel(String ID, Boolean checkOwnQuestion, String uname, String upro, String likes, String desc, String qimg, Boolean checkLike, String anser, boolean checkHideAnswer, boolean checkPaid, String paidAmount, boolean selfAnswer, boolean selfHideAnswer, boolean userReport, String questionsTitle, String answerDocId, String reportBy, String likedBy, String userId) {
        this.ID = ID;
        this.checkOwnQuestion = checkOwnQuestion;
        this.uname = uname;
        this.upro = upro;
        this.likes = likes;
        this.desc = desc;
        this.qimg = qimg;
        this.checkLike = checkLike;
        this.anser = anser;
        this.checkHideAnswer = checkHideAnswer;
        this.checkPaid = checkPaid;
        this.paidAmount = paidAmount;
        this.selfAnswer = selfAnswer;
        this.selfHideAnswer = selfHideAnswer;
        this.userReport = userReport;
        this.questionsTitle = questionsTitle;
        this.answerDocId = answerDocId;
        this.reportBy = reportBy;
        this.likedBy = likedBy;
        this.userId = userId;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setCheckOwnQuestion(Boolean checkOwnQuestion) {
        this.checkOwnQuestion = checkOwnQuestion;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public void setUpro(String upro) {
        this.upro = upro;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setQimg(String qimg) {
        this.qimg = qimg;
    }

    public void setCheckLike(Boolean checkLike) {
        this.checkLike = checkLike;
    }

    public void setAnser(String anser) {
        this.anser = anser;
    }

    public void setCheckHideAnswer(boolean checkHideAnswer) {
        this.checkHideAnswer = checkHideAnswer;
    }

    public void setCheckPaid(boolean checkPaid) {
        this.checkPaid = checkPaid;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }

    public void setSelfAnswer(boolean selfAnswer) {
        this.selfAnswer = selfAnswer;
    }

    public void setSelfHideAnswer(boolean selfHideAnswer) {
        this.selfHideAnswer = selfHideAnswer;
    }

    public void setUserReport(boolean userReport) {
        this.userReport = userReport;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public void setLikedBy(String likedBy) {
        this.likedBy = likedBy;
    }

    public String getUserId() {
        return userId;
    }

    public String getLikedBy() {
        return likedBy;
    }

    public String getReportBy() {
        return reportBy;
    }

    public void setReportBy(String reportBy) {
        this.reportBy = reportBy;
    }

    public String getAnswerDocId() {
        return answerDocId;
    }

    public void setAnswerDocId(String answerDocId) {
        this.answerDocId = answerDocId;
    }

    public String getQuestionsTitle() {
        return this.questionsTitle;
    }

    public void setQuestionsTitle(String questionsTitle) {
        this.questionsTitle = questionsTitle;
    }

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

    @Override
    public String toString() {
        return "AnswersModel{" +
                "ID='" + ID + '\'' +
                ", checkOwnQuestion=" + checkOwnQuestion +
                ", uname='" + uname + '\'' +
                ", upro='" + upro + '\'' +
                ", likes='" + likes + '\'' +
                ", desc='" + desc + '\'' +
                ", qimg='" + qimg + '\'' +
                ", checkLike=" + checkLike +
                ", anser='" + anser + '\'' +
                ", checkHideAnswer=" + checkHideAnswer +
                ", checkPaid=" + checkPaid +
                ", paidAmount='" + paidAmount + '\'' +
                ", selfAnswer=" + selfAnswer +
                ", selfHideAnswer=" + selfHideAnswer +
                ", userReport=" + userReport +
                ", questionsTitle='" + questionsTitle + '\'' +
                ", answerDocId='" + answerDocId + '\'' +
                ", reportBy='" + reportBy + '\'' +
                ", likedBy='" + likedBy + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}




