package com.android.mosof;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.android.mosof.game.Game;
import com.android.mosof.setup.GameSetupActivity;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
     * Gets the shared preferences.
     * @return the preferences
     */
    private SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }
}