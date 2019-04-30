package com.android.mosof.highscore;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HighscoreDao {

    @Query("SELECT * FROM highscore  ORDER BY tries ASC")
    List<Highscore> getAll();


    @Query("SELECT * FROM highscore WHERE holes IS :holes AND pins IS :pins AND emptyPins IS :empty AND duplicatePins IS :duplicate ORDER BY tries ASC")
    List<Highscore> findByResult(int holes, int pins, boolean empty, boolean duplicate);

    @Insert
    void insert(Highscore highscore);
}
