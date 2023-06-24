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




