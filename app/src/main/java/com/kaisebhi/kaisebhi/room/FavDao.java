package com.kaisebhi.kaisebhi.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kaisebhi.kaisebhi.HomeNavigation.home.QuestionsModel;

import java.util.List;

@Dao
public interface FavDao {

    @Insert
    public void insertAllFav(List<QuestionsModel> questionsModel);

    @Insert
    public void insertFav(QuestionsModel questionsModel);

    @Update
    public void updateFav(QuestionsModel questionsModel);

    @Delete
    public void deleteFav(QuestionsModel questionsModel);

    @Query("SELECT * FROM favTable WHERE ID LIKE (:favIds)")
    public List<QuestionsModel> getFavById(String[] favIds);

    @Query("SELECT * FROM favTable")
    public List<QuestionsModel> getFav();

    @Query("DELETE FROM favTable")
    public void deleteAllFav();
}
