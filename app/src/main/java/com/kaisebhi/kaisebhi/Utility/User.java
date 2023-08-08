package com.kaisebhi.kaisebhi.Utility;


public class User {

    private String mobile;
    private String name;
    private String uid;
    private String profile;
    private String location;
    private int item;
    private boolean edel;
    private String email;
    private long reward;

    public User(String name,String mobile,String uid,String profile,String location,int item,boolean edel, String email, long reward)
    {
        this.name = name;
        this.mobile = mobile;
        this.uid = uid;
        this.profile = profile;
        this.location = location;
        this.item = item;
        this.edel = edel;
        this.email = email;
        this.reward = reward;
    }

    public long getReward() {
        return reward;
    }

    public void setReward(long reward) {
        this.reward = reward;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

