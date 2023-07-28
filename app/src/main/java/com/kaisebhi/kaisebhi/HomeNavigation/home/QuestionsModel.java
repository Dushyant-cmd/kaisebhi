package com.kaisebhi.kaisebhi.HomeNavigation.home;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "favTable")
public class QuestionsModel {

    @PrimaryKey(autoGenerate = true)
    private int uniqueId;

    @ColumnInfo(name = "ID", defaultValue = "null")
    private String ID;

    @ColumnInfo(name = "Title", defaultValue = "null")
    private String Title;

    @ColumnInfo(name = "desc", defaultValue = "null")
    private String desc;

    @ColumnInfo(name = "qpic", defaultValue = "null")
    private String qpic;

    @ColumnInfo(name = "uname", defaultValue = "null")
    private String uname;

    @ColumnInfo(name = "upro", defaultValue = "null")
    private String upro;

    @ColumnInfo(name = "checkFav", defaultValue = "null")
    private Boolean checkFav;

    @ColumnInfo(name = "likes", defaultValue = "null")
    private String likes;

    @ColumnInfo(name = "checkLike", defaultValue = "null")
    private Boolean checkLike;

    @ColumnInfo(name = "tansers", defaultValue = "null")
    private String tansers;

    @ColumnInfo(name = "likedByUser", defaultValue = "null")
    private String likedByUser;

    private String pathOfImg, userId;

    public QuestionsModel() {
        //mandatory by room db to have empty constructor of entity class
    }

    public QuestionsModel(String ID, String title, String desc, String qpic, String uname, String upro, Boolean checkFav, String likes, Boolean checkLike, String tansers, String likedByUser, String image, String userId) {
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
        this.likedByUser = likedByUser;
        this.pathOfImg = image;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPathOfImg() {
        return pathOfImg;
    }

    public void setPathOfImg(String pathOfImg) {
        this.pathOfImg = pathOfImg;
    }

    public String getLikedByUser() {
        return likedByUser;
    }

    public void setLikedByUser(String likedByUser) {
        this.likedByUser = likedByUser;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setQpic(String qpic) {
        this.qpic = qpic;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public void setUpro(String upro) {
        this.upro = upro;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public void setCheckLike(Boolean checkLike) {
        this.checkLike = checkLike;
    }

    public void setTansers(String tansers) {
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

    public void setCheckFav(Boolean check) {
        checkFav = check;
    }

    public Boolean getCheckLike() {
        return checkLike;
    }

    @Override
    public String toString() {
        return "QuestionsModel{" +
                "uniqueId=" + uniqueId +
                ", ID='" + ID + '\'' +
                ", Title='" + Title + '\'' +
                ", desc='" + desc + '\'' +
                ", qpic='" + qpic + '\'' +
                ", uname='" + uname + '\'' +
                ", upro='" + upro + '\'' +
                ", checkFav=" + checkFav +
                ", likes='" + likes + '\'' +
                ", checkLike=" + checkLike +
                ", tansers='" + tansers + '\'' +
                ", likedByUser='" + likedByUser + '\'' +
                ", pathOfImg='" + pathOfImg + '\'' +
                '}';
    }
}




