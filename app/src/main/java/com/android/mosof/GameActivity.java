package com.android.mosof;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.mosof.components.ColorDrawable;
import com.android.mosof.game.Game;
import com.android.mosof.highscore.Highscore;
import com.android.mosof.highscore.HighscoreDatabase;
import com.android.mosof.setup.GameSetup;
import com.android.mosof.setup.GameSetupActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;

import static android.widget.TableLayout.LayoutParams.MATCH_PARENT;
import static android.widget.TableLayout.LayoutParams.WRAP_CONTENT;

public class GameActivity extends AppCompatActivity {

    public static final String continueGame = "continueGame";

    private View.OnTouchListener touchListener = new PinOnTouchListener();
    private View.OnDragListener dragListener = new HoleOnDragListener();
    private View.OnClickListener clickListener = new HoleOnClickListener();

    private GameSetup setup;

    private boolean gameEnded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        int background = getSharedPreferences().getInt(GameSetupActivity.BACKGROUND_KEY, R.drawable.wood_background);
        findViewById(R.id.game_screen_root).setBackgroundResource(background);

        // setup rows and settings depending on if it's a new or loaded game
        Bundle bundle = getIntent().getExtras();
        TableLayout holes = findViewById(R.id.holes_table);
        if (bundle != null && bundle.getBoolean(continueGame)) {
            Game game = loadGame();

            if (game == null) {
                Toast.makeText(this, R.string.load_fail, Toast.LENGTH_SHORT).show();
                bundle.putBoolean(continueGame, false);
                finish();
                return;
            }

            setup = game.getSetup();

            ArrayList<ArrayList<Integer>> rows = game.getRows();
            for (int i = 0; i <= rows.size() - 1; i++) {
                TableRow row;
                // last row needs to have listeners
                if (i == (rows.size() - 1)) {
                    row = createHoleRow(rows.get(i), true);
                } else {
                    row = createHoleRow(rows.get(i), false);
                    evaluateRow(row);
                }
                holes.addView(row);
            }
        } else {
            setup = loadSettings();
            holes.addView(createHoleRow());
        }

        if (setup == null) {
            Toast.makeText(this, R.string.setup_fail, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TableRow row1 = findViewById(R.id.color_row_1);
        TableRow row2 = findViewById(R.id.color_row_2);
        int middleIndex = setup.getColors().size() / 2;

        for (int i = 0; i < setup.getColors().size(); i++) {
            ImageView pin = getPin(setup.getColors().get(i));
            if (i < middleIndex) {
                row1.addView(pin);
            } else {
                row2.addView(pin);
            }
        }

        Button submit = findViewById(R.id.button_submit);
        submit.setOnClickListener(submitListener());
    }

    private ImageView getPin(int color) {
        ImageView pin = new ImageView(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.weight = 1f;
        int margin = toPixels(2);
        params.setMargins(margin, margin, margin, margin);
        pin.setLayoutParams(params);
        pin.setImageDrawable(colorDrawable(color));
        pin.setOnTouchListener(touchListener);
        return pin;
    }

    /**
     * Create a circle with the given color.
     */
    private ColorDrawable colorDrawable(int color) {
        ColorDrawable drawable;
        if (color == android.R.color.transparent) {
            drawable = new ColorDrawable(GradientDrawable.Orientation.TR_BL,
                    new int[]{ContextCompat.getColor(this, R.color.alpha96),
                            ContextCompat.getColor(this, R.color.alpha96)});
        } else {
            drawable = new ColorDrawable(GradientDrawable.Orientation.TR_BL,
                    new int[]{ContextCompat.getColor(this, color),
                            ContextCompat.getColor(this, R.color.nearly_black)});
        }
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColorResource(color);
        drawable.setStroke(toPixels(1), ContextCompat.getColor(this, R.color.alpha96));
        int size = toPixels(40);
        drawable.setSize(size, size);
        return drawable;
    }

    /**
     * Convert density pixels to pixels.
     */
    private int toPixels(int dps) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

    /**
     * Load settings from shared preferences.
     */
    private GameSetup loadSettings() {
        String json = getSharedPreferences().getString(GameSetup.class.getSimpleName(), null);
        if (json == null) {
            return null;
        }
        try {
            return new Gson().fromJson(json, GameSetup.class);
        } catch (JsonSyntaxException jse) {
            // json incompatible - reset
            getSharedPreferences().edit().putString(GameSetup.class.getSimpleName(), null).apply();
            return null;
        }
    }

    /**
     * Load game setup from shared preferences.
     */
    private Game loadGame() {
        String json = getSharedPreferences().getString(Game.class.getSimpleName(), null);
        if (json == null) {
            return null;
        }
        try {
            return new Gson().fromJson(json, Game.class);
        } catch (JsonSyntaxException jse) {
            // json incompatible - reset
            getSharedPreferences().edit().putString(Game.class.getSimpleName(), null).apply();
            return null;
        }
    }

    /**
     * Save the game setup into the shared preferences.
     *
     * @param game the game
     */
    private void saveGame(Game game) {
        String json = new GsonBuilder().create().toJson(game);
        getSharedPreferences().edit().putString(Game.class.getSimpleName(), json).apply();
    }


    /**
     * {@link android.view.View.OnDragListener} for dragging pins on holes.
     */
    private final class HoleOnDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            if (DragEvent.ACTION_DROP == event.getAction()) {
                ImageView hole = (ImageView) v; //get object that the other was dragged on
                ImageView pin = (ImageView) event.getLocalState(); // get object that was dragged
                hole.setImageDrawable(pin.getDrawable());
                hole.setOnClickListener(clickListener);
            }
            return true;
        }
    }

    /**
     * {@link android.view.View.OnTouchListener} for starting the dragging of pins.
     */
    private final class PinOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (MotionEvent.ACTION_DOWN == event.getAction()) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(data, shadowBuilder, v, 0);
            }
            return true;
        }
    }

    /**
     * {@link android.view.View.OnClickListener} to reset holes to empty ones.
     */
    private final class HoleOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            ImageView hole = (ImageView) v;
            hole.setImageDrawable(colorDrawable(android.R.color.transparent));
            hole.setOnClickListener(null);
        }
    }

    /**
     * Creates a {@link android.view.View.OnClickListener} for the submit button to check user input.
     */
    private View.OnClickListener submitListener() {
        final Context context = this;
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TableLayout holes = findViewById(R.id.holes_table);
                TableRow lastRow = (TableRow) holes.getChildAt(holes.getChildCount() - 1);
                if (holes.getChildCount() >= setup.getMaxTries()) {
                    evaluateRow(lastRow);
                    Toast.makeText(context, R.string.max_tries_surpassed, Toast.LENGTH_SHORT).show();
                    gameEnded = true;
                    return;
                }
                if (checkRow(lastRow)) {
                    // remove listeners from lastRow
                    boolean won = evaluateRow(lastRow);
                    if (won) {
                        winDialog(holes.getChildCount(), setup.getHoleCount(), setup.getColors().size(), setup.getEmptyPins(), setup.getDuplicatePins()).show();
                        gameEnded = true;
                    } else {
                        // insert new row
                        TableRow newRow = createHoleRow();
                        holes.addView(newRow);
                    }
                    final ScrollView sv = findViewById(R.id.holes_scroll_view);
                    sv.post(new Runnable() {
                        @Override
                        public void run() {
                            sv.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }
            }
        };
    }

    /**
     * Check whether row is a valid configuration of pins that can be counted.
     */
    private boolean checkRow(TableRow row) {
        List<Integer> configuration = new ArrayList<>();
        for (int i = 0; i < row.getChildCount(); i++) {
            View view = row.getChildAt(i);
            if (view instanceof ImageView) {
                // hole with or without pin
                ImageView hole = (ImageView) view;
                configuration.add(getColor(hole.getDrawable()));
            }
        }
        return checkSolution(configuration);
    }

    /**
     * Get the color resource id from the drawable.
     */
    private Integer getColor(Drawable drawable) {
        if (drawable instanceof ColorDrawable) {
            // it's a pin
            ColorDrawable pin = (ColorDrawable) drawable;
            return pin.getColorResource();
        }
        return android.R.color.transparent;
    }

    /**
     * Check whether the configuration of pins is a valid submission.
     */
    private boolean checkSolution(List<Integer> configuration) {
        if (configuration.size() != setup.getSolution().size()) {
            // should never happen
            return false;
        }
        List<Integer> counter = new ArrayList<>();
        boolean emptyPins = setup.getEmptyPins();
        boolean duplicatePins = setup.getDuplicatePins();
        for (int i = 0; i < configuration.size(); i++) {
            int color = configuration.get(i);
            if (!emptyPins && color == android.R.color.transparent) {
                // empty pins not allowed
                Toast.makeText(this, R.string.pins_missing, Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!duplicatePins && counter.contains(color)) {
                // pin duplicate
                Toast.makeText(this, R.string.pins_duplicate, Toast.LENGTH_SHORT).show();
                return false;
            }
            counter.add(color);
        }
        return true;
    }

    /**
     * Create a new row of holes.
     */
    private TableRow createHoleRow() {
        return createHoleRow(null, true);
    }

    /**
     * Create a new row of holes.
     *
     * @param colors      the colors for the different holes
     * @param addListener boolean if the listeners should be added or not (only for last row)
     * @return a row with holes
     */
    private TableRow createHoleRow(ArrayList<Integer> colors, boolean addListener) {
        TableRow row = new TableRow(this);
        TableLayout.LayoutParams params = new TableLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        row.setLayoutParams(params);
        // holes
        for (int i = 0; i < setup.getHoleCount(); i++) {
            ImageView hole = new ImageView(this);
            int padding = toPixels(2);
            hole.setPadding(padding, padding, padding, padding);
            TableRow.LayoutParams holeParams = new TableRow.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 1f);
            hole.setLayoutParams(holeParams);
            if (colors != null) {
                hole.setImageDrawable(colorDrawable(colors.get(i)));
            } else {
                hole.setImageDrawable(colorDrawable(android.R.color.transparent));
            }
            if (addListener) {
                hole.setOnDragListener(dragListener);
            }
            row.addView(hole);
        }
        // black hint
        TextView blackHint = hintText(android.R.color.black, android.R.color.white);
        blackHint.setId(R.id.black_hint);
        row.addView(blackHint);
        // white hint
        TextView whiteHint = hintText(android.R.color.white, android.R.color.black);
        whiteHint.setId(R.id.white_hint);
        row.addView(whiteHint);
        return row;
    }

    /**
     * Create the {@link TextView} for the solution hints.
     */
    private TextView hintText(int backgroundColor, int textColor) {
        TableRow.LayoutParams params = new TableRow.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, .5f);
        TextView hint = new TextView(this);
        hint.setLayoutParams(params);
        hint.setGravity(Gravity.CENTER);
        hint.setText("-");
        hint.setBackgroundColor(ContextCompat.getColor(this, backgroundColor));
        hint.setTextColor(ContextCompat.getColor(this, textColor));
        hint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        return hint;
    }

    /**
     * Removes all listeners from the rows child views and sets the solution hints.
     */
    private boolean evaluateRow(TableRow row) {
        List<Integer> configuration = new ArrayList<>();
        TextView blackHint = null;
        TextView whiteHint = null;
        for (int i = 0; i < row.getChildCount(); i++) {
            View view = row.getChildAt(i);
            if (view instanceof ImageView) {
                ImageView hole = (ImageView) view;
                configuration.add(getColor(hole.getDrawable()));
                view.setOnClickListener(null);
                view.setOnDragListener(null);
                continue;
            }
            if (view.getId() == R.id.black_hint) {
                blackHint = (TextView) view;
            } else if (view.getId() == R.id.white_hint) {
                whiteHint = (TextView) view;
            }
        }
        if (blackHint == null || whiteHint == null) {
            return false;
        }
        return evaluateResult(configuration, blackHint, whiteHint);
    }

    /**
     * Check how close the given configuration is to the solution and appraise it.
     *
     * @return true if game is won
     */
    private boolean evaluateResult(List<Integer> configuration, TextView blackHint, TextView whiteHint) {
        List<Integer> solution = setup.getSolution();
        List<Integer> solutionCopy = new ArrayList<>(solution);
        List<Integer> configurationCopy = new ArrayList<>(configuration);
        int black = 0;
        int white = 0;
        for (int i = 0; i < configurationCopy.size(); i++) {
            Integer color = configuration.get(i);
            if (color.equals(solutionCopy.get(i))) {
                black++;
                solutionCopy.set(i, -1);
                configurationCopy.set(i, -1);
            }
        }
        for (int i = 0; i < configurationCopy.size(); i++) {
            Integer color = configurationCopy.get(i);
            if (!color.equals(-1)) {
                if (solutionCopy.contains(color)) {
                    white++;
                    solutionCopy.set(solutionCopy.indexOf(color), -1);
                }
            }
        }
        if (black == setup.getSolution().size()) {
            // game won
            return true;
        }
        blackHint.setText(String.valueOf(black));
        whiteHint.setText(String.valueOf(white));
        return false;
    }

    /**
     * Create a dialog where you can input your name.
     */
    private Dialog winDialog(final int tries, final int holes, final int pins, final boolean emptyPins, final boolean duplicatePins) {
        final Context context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.edit_dialog, null);
        final EditText inputField = view.findViewById(R.id.edit_dialog_input);
        builder.setView(view).setTitle(R.string.win_title).setMessage(R.string.win_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = inputField.getText().toString();
                        if (name.isEmpty()) {
                            return;
                        }
                        // store highscore in database
                        Highscore highscore = new Highscore(name.trim(), tries, holes, pins, emptyPins, duplicatePins);
                        HighscoreDatabase database = HighscoreDatabase.get(context);
                        database.highscores().insert(highscore);
                        database.close();
                        // store highscore to preferences
                        String json = new Gson().toJson(highscore);
                        getSharedPreferences().edit().putString(Highscore.class.getSimpleName(), json).apply();
                        // switch to highscore view
                        startActivity(new Intent(context, HighscoreActivity.class));
                        finish();
                    }
                });
        return builder.create();
    }

    private SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    /**
     * Gathers the important data to create a Game object
     *
     * @return the Game object
     */
    private Game getGameData() {
        if (!gameEnded) {
            ArrayList<ArrayList<Integer>> rows = new ArrayList<>();
            TableLayout holes = findViewById(R.id.holes_table);
            for (int i = 0; i < holes.getChildCount(); i++) {
                TableRow row = (TableRow) holes.getChildAt(i);
                //rows.add(row);
                ArrayList<Integer> holeList = new ArrayList<>();
                for (int j = 0; j < row.getChildCount(); j++) {
                    View view = row.getChildAt(j);
                    if (view instanceof ImageView) {
                        holeList.add(((ColorDrawable) ((ImageView) view).getDrawable()).getColorResource());
                    }
                }
                rows.add(holeList);
            }
            return new Game(rows, setup);
        }
        return null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveGame(getGameData());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveGame(getGameData());
    }
}