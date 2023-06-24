package com.kaisebhi.kaisebhi.Utility;


public class User {

    private String mobile;
    private String name;
    private String uid;
    private String profile;
    private String location;
    private int item;
    private boolean edel;

    public User(String name,String mobile,String uid,String profile,String location,int item,boolean edel)
    {
        this.name = name;
        this.mobile = mobile;
        this.uid = uid;
        this.profile = profile;
        this.location = location;
        this.item = item;
        this.edel = edel;
    }


    public String getName() {
        return name;
    }
    public String getMobile() {return mobile;}
    public String getUid() {return uid;}
    public String getProfile() {return profile;}
    public String getLocation() {return location;}
    public int getItem() {return item;}

    public boolean getEdel() {return edel;}



}

