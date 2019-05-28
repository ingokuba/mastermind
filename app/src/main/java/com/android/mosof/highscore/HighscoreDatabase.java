package com.android.mosof.highscore;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Highscore.class}, version = 1, exportSchema = false)
public abstract class HighscoreDatabase extends RoomDatabase {
    public abstract HighscoreDao highscores();

    public static HighscoreDatabase get(Context context) {
        return Room.databaseBuilder(context, HighscoreDatabase.class, HighscoreDatabase.class.getSimpleName()).allowMainThreadQueries().build();
    }
}
