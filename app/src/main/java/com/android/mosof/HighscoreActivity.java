package com.android.mosof;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.android.mosof.highscore.Highscore;
import com.android.mosof.highscore.HighscoreAdapter;
import com.android.mosof.highscore.HighscoreDatabase;
import com.android.mosof.setup.GameSetup;
import com.google.gson.Gson;

import java.util.List;

public class HighscoreActivity extends AppCompatActivity {

    private HighscoreDatabase database;

    private ListView highscoreList;
    private Spinner holesSpinner;
    private Spinner pinsSpinner;
    private CheckBox emptyCheck;
    private CheckBox duplicateCheck;

    private SortItemListener sortItemListener = new SortItemListener();
    private SortCheckedListener sortCheckedListener = new SortCheckedListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);
        highscoreList = findViewById(R.id.highscore);

        database = HighscoreDatabase.get(this);

        ArrayAdapter<Integer> holesAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, GameSetup.HOLE_COUNTS);
        holesSpinner = findViewById(R.id.highscore_holes_spinner);
        holesSpinner.setAdapter(holesAdapter);
        holesSpinner.setOnItemSelectedListener(sortItemListener);

        ArrayAdapter<Integer> pinsAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, GameSetup.PIN_COUNTS);
        pinsSpinner = findViewById(R.id.highscore_pins_spinner);
        pinsSpinner.setAdapter(pinsAdapter);
        pinsSpinner.setOnItemSelectedListener(sortItemListener);

        emptyCheck = findViewById(R.id.highscore_empty_check);
        duplicateCheck = findViewById(R.id.highscore_duplicate_check);

        emptyCheck.setOnClickListener(sortCheckedListener);
        duplicateCheck.setOnClickListener(sortCheckedListener);

        loadLatestHighscore();
    }

    private void loadLatestHighscore() {
        String json = getSharedPreferences().getString(Highscore.class.getSimpleName(), null);
        if (json == null) {
            return;
        }
        Highscore highscore = new Gson().fromJson(json, Highscore.class);

        holesSpinner.setSelection(getItemId(holesSpinner, highscore.getHoles()));
        pinsSpinner.setSelection(getItemId(pinsSpinner, highscore.getPins()));
        emptyCheck.setChecked(highscore.getEmptyPins());
        duplicateCheck.setChecked(highscore.getDuplicatePins());
    }

    /**
     * Look for a value in a spinner component.
     *
     * @return the id of the item or null.
     */
    private Integer getItemId(Spinner spinner, Object value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(value)) {
                return i;
            }
        }
        return null;
    }

    private void searchHighscores() {
        int holes = (int) holesSpinner.getItemAtPosition(holesSpinner.getSelectedItemPosition());
        int pins = (int) pinsSpinner.getItemAtPosition(pinsSpinner.getSelectedItemPosition());
        boolean empty = emptyCheck.isChecked();
        boolean duplicate = duplicateCheck.isChecked();
        List<Highscore> highscores = database.highscores().findByResult(holes, pins, empty, duplicate);
        HighscoreAdapter highscoreAdapter = new HighscoreAdapter(this, highscores);
        highscoreList.setAdapter(highscoreAdapter);
    }

    private class SortCheckedListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            searchHighscores();
        }
    }

    private class SortItemListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            searchHighscores();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }

    private SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }
}
