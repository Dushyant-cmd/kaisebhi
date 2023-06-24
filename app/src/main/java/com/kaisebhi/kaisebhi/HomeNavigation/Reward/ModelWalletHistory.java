package com.kaisebhi.kaisebhi.HomeNavigation.Reward;

import com.google.gson.annotations.SerializedName;

public class ModelWalletHistory {

    @SerializedName("date")
    private String date;

    @SerializedName("amount")
    private String amount;

    @SerializedName("type")
    private String type;

    @SerializedName("remark")
    private String remark;


    public String getDate() {
        return date;
    }

    public String getAmount() {
        return amount;
    }


    public String getType() {
        return type;
    }


    public String getRemark() {
        return remark;
    }


}
