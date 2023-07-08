package com.kaisebhi.kaisebhi.room;

import android.content.Context;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.kaisebhi.kaisebhi.HomeNavigation.home.QuestionsModel;

import kotlin.jvm.Volatile;

@Database(entities = QuestionsModel.class, exportSchema = false, version = 1)
@TypeConverters(RoomTypeConverter.class)
public abstract class RoomDb extends RoomDatabase {
    public abstract FavDao getFavDao();
    public static String DB_NAME = "localDb";

    @Volatile
    private static RoomDb instance;
    public static synchronized RoomDb getDbInstance(Context ctx) {
        if(instance == null) {
            Migration migration_1_2 = new Migration(1, 2) {
                @Override
                public void migrate(@NonNull SupportSQLiteDatabase database) {
                    String getTables = "SELECT name sqlite_master WHERE type='table'";
                    String addCol = "ALTER TABLE favTable ADD COLUMN new TEXT DEFAULT null";
                }
            };
            instance = new RoomDatabase.Builder<RoomDb>(ctx, RoomDb.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        } else {
            return instance;
        }

        return instance;
    }
}
