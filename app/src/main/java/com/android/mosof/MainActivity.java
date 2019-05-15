package com.android.mosof;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.mosof.game.Game;
import com.android.mosof.setup.GameSetupActivity;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AbstractActivity implements View.OnClickListener {

    /**
     * Key for the background resource as integer in {@link SharedPreferences}.
     */
    public static final String BACKGROUND_KEY = "Background";
    private static final List<Integer> BACKGROUNDS = Arrays.asList(R.string.wood, R.string.morocco, R.string.banana);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);

        Button buttonRules = findViewById(R.id.app_rules);
        Button buttonAbout = findViewById(R.id.app_about);
        Button buttonNewGame = findViewById(R.id.new_game);
        Button buttonHighscore = findViewById(R.id.app_highscore);

        buttonRules.setOnClickListener(this);
        buttonAbout.setOnClickListener(this);
        buttonNewGame.setOnClickListener(this);
        buttonHighscore.setOnClickListener(this);

        if (isSaveGameAvailable()) {
            Button buttonContinueGame = findViewById(R.id.continue_game);
            buttonContinueGame.setVisibility(View.VISIBLE);
            buttonContinueGame.setOnClickListener(this);
        }

        // Background for the app
        ArrayAdapter<String> backgroundAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, getBackgroundNames());
        Spinner backgroundSpinner = findViewById(R.id.background_selector);
        backgroundSpinner.setAdapter(backgroundAdapter);
        backgroundSpinner.setOnItemSelectedListener(backgroundListener());
        // load previous background
        int backgroundId = getSharedPreferences().getInt(BACKGROUND_KEY, R.drawable.wood_background);
        Integer id = getItemId(backgroundSpinner, getBackgroundName(backgroundId));
        if (id != null) {
            backgroundSpinner.setSelection(id);
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected int getMainLayout() {
        return R.id.main_layout;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Button buttonContinueGame = findViewById(R.id.continue_game);
        if (isSaveGameAvailable()) {
            buttonContinueGame.setVisibility(View.VISIBLE);
            buttonContinueGame.setOnClickListener(this);
        } else {
            buttonContinueGame.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.app_rules:
                Intent intentRules = new Intent(this, RulesActivity.class);
                startActivity(intentRules);
                break;
            case R.id.app_about:
                Intent intentAbout = new Intent(this, AboutActivity.class);
                startActivity(intentAbout);
                break;
            case R.id.new_game:
                startActivity(new Intent(this, GameSetupActivity.class));
                break;
            case R.id.continue_game:
                Intent intent = new Intent(this, GameActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean(GameActivity.continueGame, true);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.app_highscore:
                Intent intentHighscore = new Intent(this, HighscoreActivity.class);
                startActivity(intentHighscore);
                break;
            default:
                break;
        }
    }

    /**
     * Checks if there is a saved game available
     *
     * @return true or false
     */
    private boolean isSaveGameAvailable() {
        String json = getSharedPreferences().getString(Game.class.getSimpleName(), null);
        if (json == null) {
            return false;
        }
        try {
            return (new Gson().fromJson(json, Game.class) != null);
        } catch (JsonSyntaxException jse) {
            // json incompatible - reset
            getSharedPreferences().edit().putString(Game.class.getSimpleName(), null).apply();
            return false;
        }
    }

    /**
     * Transform string ids for the backgrounds to a string array.
     */
    private String[] getBackgroundNames() {
        String[] names = new String[BACKGROUNDS.size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = getString(BACKGROUNDS.get(i));
        }
        return names;
    }

    /**
     * Get the matching background name for the drawable id.
     */
    private String getBackgroundName(int resourceId) {
        switch (resourceId) {
            case R.drawable.morocco_background:
                return getString(R.string.morocco);
            case R.drawable.banana_background:
                return getString(R.string.banana);
            default:
                return getString(R.string.wood);
        }
    }

    private AdapterView.OnItemSelectedListener backgroundListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) parent.getItemAtPosition(position);
                int resourceId = R.drawable.wood_background;
                if (name.equals(getString(R.string.morocco))) {
                    resourceId = R.drawable.morocco_background;
                } else if (name.equals(getString(R.string.banana))) {
                    resourceId = R.drawable.banana_background;
                }
                if (getSharedPreferences().edit().putInt(BACKGROUND_KEY, resourceId).commit()) {
                    findViewById(R.id.main_layout).setBackgroundResource(resourceId);
                }
                if (view instanceof TextView) {
                    ((TextView) view).setText(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
    }
}