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
}
