package com.android.mosof;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.android.mosof.highscore.Highscore;
import com.android.mosof.highscore.HighscoreDao;
import com.android.mosof.highscore.HighscoreDatabase;
import com.android.mosof.setup.GameSetup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class HighscoreDatabaseTest {

    private HighscoreDatabase database;
    private HighscoreDao dao;

    @Before
    public void createDatabase() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), HighscoreDatabase.class).build();
        dao = database.highscores();
    }

    @After
    public void closeDatabase() {
        database.close();
    }

    /**
     * GIVEN: Virtual highscore
     * WHEN: storing it
     * THEN: highscore has id
     */
    @Test
    public void should_store_highscore() {
        Highscore highscore = setupHighscore();

        dao.insert(highscore);

        List<Highscore> highscores = dao.getAll();
        assertThat(highscores, hasSize(1));
        assertThat(highscores.get(0).getId(), greaterThan(0));
    }

    /**
     * GIVEN: Stored highscore
     * WHEN: Searching it with holes and pins
     * THEN: it is found
     */
    @Test
    public void should_find_highscore_by_result() {
        Highscore highscore = setupHighscore();
        dao.insert(highscore);

        List<Highscore> highscores = dao.findByResult(highscore.getHoles(), highscore.getPins(), highscore.getEmptyPins(), highscore.getDuplicatePins());

        assertThat(highscores, hasSize(1));
    }

    /**
     * GIVEN: two stored highscores
     * WHEN: getting all highscores
     * THEN: they are sorted by tries
     */
    @Test
    public void should_sort_by_tries() {
        Highscore highscore1 = setupHighscore();
        highscore1.setTries(2);
        Highscore highscore2 = setupHighscore();
        dao.insert(highscore1);
        dao.insert(highscore2);

        List<Highscore> highscores = dao.getAll();

        assertThat(highscores, hasSize(2));
        assertThat(highscores.get(0).getTries(), lessThan(highscores.get(1).getTries()));
    }

    private Highscore setupHighscore() {
        return new Highscore("Tester", 12, GameSetup.HOLE_COUNTS.get(0), GameSetup.PIN_COUNTS.get(0), true, false);
    }
}
