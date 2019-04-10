package com.android.mosof.setup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.mosof.ColorAdapter;
import com.android.mosof.GameActivity;
import com.android.mosof.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GameSetupActivity extends AppCompatActivity {

    public static final String GAME_PREFERENCES = "GamePreferences";

    private GameSetup setup = new GameSetup();

    private static final Integer[] HOLE_COUNTS = {4, 5, 6, 8};
    private static final Integer[] PIN_COUNTS = {6, 7, 8};
    private static final List<Integer> PIN_COLORS = Arrays.asList(android.R.color.holo_blue_dark, android.R.color.holo_red_dark,
            android.R.color.holo_purple, android.R.color.holo_green_dark, android.R.color.holo_orange_dark,
            android.R.color.darker_gray, android.R.color.holo_orange_light, android.R.color.holo_red_light);

    private Spinner holeCountSpinner;
    private Spinner pinCountSpinner;
    private CheckBox duplicatePins;
    private CheckBox emptyPins;

    private boolean computerMode = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        // Amount of holes
        ArrayAdapter<Integer> holeAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, HOLE_COUNTS);
        holeCountSpinner = findViewById(R.id.hole_count);
        holeCountSpinner.setAdapter(holeAdapter);
        holeCountSpinner.setOnItemSelectedListener(holeCountListener());
        // Amount of colors
        ArrayAdapter<Integer> pinAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, PIN_COUNTS);
        pinCountSpinner = findViewById(R.id.pin_count);
        pinCountSpinner.setAdapter(pinAdapter);
        pinCountSpinner.setOnItemSelectedListener(pinCountListener());

        duplicatePins = findViewById(R.id.allow_duplicate_pins);
        duplicatePins.setOnClickListener(duplicatePinsListener());

        emptyPins = findViewById(R.id.allow_empty_pins);
        emptyPins.setOnClickListener(emptyPinsListener());

        Button setSolution = findViewById(R.id.setup_solution);
        setSolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solutionDialog().show();
            }
        });

        RadioGroup gameModeSelection = findViewById(R.id.game_mode);
        gameModeSelection.setOnCheckedChangeListener(gameModeListener(setSolution));

        loadPreviousSetup();

        Button startGame = findViewById(R.id.start_game);
        startGame.setOnClickListener(startGameListener(this));
    }

    /**
     * Loads the setup previously stored in shared preferences by the user.
     */
    private void loadPreviousSetup() {
        String json = getSharedPreferences(GAME_PREFERENCES, 0).getString(GameSetup.class.getSimpleName(), null);
        if (json == null) {
            return;
        }
        GameSetup prevSetup;
        try {
            prevSetup = new Gson().fromJson(json, GameSetup.class);
        } catch (JsonSyntaxException jse) {
            // json incompatible - reset
            getSharedPreferences(GAME_PREFERENCES, 0).edit().putString(GameSetup.class.getSimpleName(), null).apply();
            return;
        }
        // max tries
        EditText maxTriesInput = findViewById(R.id.max_tries_input);
        maxTriesInput.setText(String.valueOf(prevSetup.getMaxTries()));
        // hole count
        Integer id = getItemId(holeCountSpinner, prevSetup.getHoleCount());
        if (id != null) {
            holeCountSpinner.setSelection(id);
        }
        // color count
        id = getItemId(pinCountSpinner, prevSetup.getColors().size());
        if (id != null) {
            pinCountSpinner.setSelection(id);
        }
        // duplicate pins
        duplicatePins.setChecked(prevSetup.getDuplicatePins());
        // empty pins
        emptyPins.setChecked(prevSetup.getEmptyPins());
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

    private AdapterView.OnItemSelectedListener holeCountListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Integer holeCount = (Integer) parent.getItemAtPosition(position);
                setup.setHoleCount(holeCount);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
    }

    /**
     * {@link GameSetup#setDuplicatePins(Boolean)} to value selected in check box.
     */
    private View.OnClickListener duplicatePinsListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    setup.setDuplicatePins(true);
                } else {
                    setup.setDuplicatePins(false);
                }
            }
        };
    }

    /**
     * {@link GameSetup#setEmptyPins(Boolean)} to value selected in check box.
     */
    private View.OnClickListener emptyPinsListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    setup.setEmptyPins(true);
                } else {
                    setup.setEmptyPins(false);
                }
            }
        };
    }

    /**
     * Generates a custom solution selection dialog
     */
    private Dialog solutionDialog() {
        final int holeCount = (int) holeCountSpinner.getSelectedItem();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.solution_dialog, null);
        final LinearLayout pinSelection = view.findViewById(R.id.solution_dialog_selection);
        ColorAdapter adapter = new ColorAdapter(this, getSelectedColors());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = (int) getResources().getDimension(R.dimen.fab_margin);
        params.setMargins(margin, margin, margin, 0);
        for (int i = 0; i < holeCount; i++) {
            Spinner pinSpinner = new Spinner(this);
            pinSpinner.setAdapter(adapter);
            pinSpinner.setLayoutParams(params);
            pinSelection.addView(pinSpinner);
        }
        final Context context = this;
        builder.setView(view).setTitle(R.string.setup_solution)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // cancelled
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (computerMode) {
                            // when the dialog was opened before computer mode is selected.
                            Toast.makeText(context, "Du feiner Klicker!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            return;
                        }
                        boolean duplicate = duplicatePins.isChecked();
                        List<Integer> solution = new ArrayList<>();

                        for (int i = 0; i < pinSelection.getChildCount(); i++) {
                            Spinner pinSpinner = (Spinner) pinSelection.getChildAt(i);
                            int selectedColor = (int) pinSpinner.getSelectedItem();
                            if (!duplicate && selectedColor != android.R.color.transparent && solution.contains(selectedColor)) {
                                Toast.makeText(context, R.string.solution_error_duplicate, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            solution.add(selectedColor);
                        }
                        setup.setSolution(solution);
                        Toast.makeText(context, R.string.solution_success, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });
        return dialog;
    }

    /**
     * Get all selected colors from the color array.
     */
    private List<Integer> getSelectedColors() {
        List<Integer> colors = new ArrayList<>();
        if (emptyPins.isChecked()) {
            colors.add(android.R.color.transparent);
        }
        for (int i = 0; i < setup.getColors().size(); i++) {
            int color = setup.getColors().get(i);
            if (!colors.contains(color)) {
                colors.add(color);
            }
        }
        return colors;
    }

    /**
     * Hide solution button for player vs computer mode and display it for player vs player mode.
     */
    private RadioGroup.OnCheckedChangeListener gameModeListener(final Button button) {
        return new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.mode_computer) {
                    // hide button
                    button.setVisibility(View.GONE);
                    computerMode = true;
                } else if (checkedId == R.id.mode_player) {
                    // display button
                    button.setVisibility(View.VISIBLE);
                    computerMode = false;
                    setup.setSolution(null);
                }
            }
        };
    }

    /**
     * Checks the game setup object:
     * <ul>
     * <li><b>Success:</b> Store it in shared preferences and switch to game activity</li>
     * <li><b>Failed:</b> Display error messages</li>
     * </ul>
     */
    private View.OnClickListener startGameListener(final Context context) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setup.setMaxTries(getMaxTries());
                if (!setup.getDuplicatePins() && !setup.getEmptyPins() && setup.getColors().size() < setup.getHoleCount()) {
                    Toast.makeText(context, R.string.hole_count_toobig_error, Toast.LENGTH_LONG).show();
                    return;
                }
                if (computerMode) {
                    setup.setSolution(randomSolution());
                }
                // check game setup
                List<Integer> errorIds = setup.check();
                if (!errorIds.isEmpty()) {
                    for (Integer errorId : errorIds) {
                        Toast.makeText(context, errorId, Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                String json = new GsonBuilder().setPrettyPrinting().create().toJson(setup);
                if (getSharedPreferences(GAME_PREFERENCES, 0).edit().putString(GameSetup.class.getSimpleName(), json).commit()) {
                    startActivity(new Intent(context, GameActivity.class));
                } else {
                    Toast.makeText(context, R.string.setup_fail, Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    /**
     * Generates a random solution for the game.
     */
    private List<Integer> randomSolution() {
        int holeCount = (int) holeCountSpinner.getSelectedItem();
        List<Integer> solution = new ArrayList<>();
        List<Integer> availableColors = getSelectedColors();
        for (int i = 0; i < holeCount; i++) {
            Random random = new Random();
            int randomColor = availableColors.get(random.nextInt(availableColors.size()));
            while (!duplicatePins.isChecked() && randomColor != android.R.color.transparent && solution.contains(randomColor)) {
                randomColor = availableColors.get(random.nextInt(availableColors.size()));
            }
            solution.add(randomColor);
        }
        return solution;
    }

    /**
     * Retrieve max tries from input field.
     *
     * @return 0 if input cannot be parsed to a number.
     */
    private Integer getMaxTries() {
        EditText maxTries = findViewById(R.id.max_tries_input);
        try {
            return Integer.valueOf(maxTries.getText().toString());
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    /**
     * Inflates the pin color selection spinners everytime a new pin count is selected.
     */
    private AdapterView.OnItemSelectedListener pinCountListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Integer pinCount = (Integer) parent.getItemAtPosition(position);
                setup.setColors(PIN_COLORS.subList(0, pinCount));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
    }
}

