package com.kaisebhi.kaisebhi.room;

import androidx.room.TypeConverter;

public class RoomTypeConverter {
    @TypeConverter
    public String fromBooleanToString(Boolean value) {
        return value.toString();
    }

    @TypeConverter
    public Boolean fromStringToBoolean(String value) {
        return Boolean.parseBoolean(value);
    }

    @TypeConverter
    public String fromStringArrToString(String[] strArr) {
        String str = "";
        for (int i = 0; i < strArr.length; i++) {
            String s = strArr[i];
            if (i == strArr.length - 1) {
                str += s;
            } else
                str += s + ",";
        }
        return str;
    }

    @TypeConverter
    public String[] fromStringToStringArr(String str) {
        String[] strArr = str.split(",");
        return strArr;
    }
}
