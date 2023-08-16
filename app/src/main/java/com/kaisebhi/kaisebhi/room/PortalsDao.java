package com.kaisebhi.kaisebhi.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface PortalsDao {

    @Insert
    void insertPortals(PortalsEntity portalsEntity);

    @Query("SELECT * FROM portalTable")
    PortalsEntity getPortals();

    @Delete
    void deletePortals(PortalsEntity portalsEntity);

    @Update
    void updatePortals(PortalsEntity portalsEntity);
}
