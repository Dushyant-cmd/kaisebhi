package com.kaisebhi.kaisebhi.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "portalTable")
public class PortalsEntity {
    @PrimaryKey
    public int id;
    @ColumnInfo(name = "portals")
    public String[] portals;
    @ColumnInfo(name = "softTTL")
    public long softTTL;

    public PortalsEntity() {
        //Empty constructor.
    }

    @Ignore
    public PortalsEntity(String[] portals, long softTTL) {
        this.portals = portals;
        this.softTTL = softTTL;
    }
}
