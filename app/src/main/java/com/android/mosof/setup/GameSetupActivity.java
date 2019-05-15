package com.android.mosof.setup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.mosof.AbstractActivity;
import com.android.mosof.GameActivity;
import com.android.mosof.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.android.mosof.setup.GameSetup.HOLE_COUNTS;
import static com.android.mosof.setup.GameSetup.PIN_COLORS;
import static com.android.mosof.setup.GameSetup.PIN_COUNTS;

public class GameSetupActivity extends AbstractActivity {

    private GameSetup setup = new GameSetup();

    private Spinner holeCountSpinner;
    private Spinner pinCountSpinner;
    private CheckBox duplicatePins;
    private CheckBox emptyPins;

    private boolean computerMode = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        String json = getSharedPreferences().getString(GameSetup.class.getSimpleName(), null);
        if (json == null) {
            return;
        }
        try {
            setup = new Gson().fromJson(json, GameSetup.class);
        } catch (JsonSyntaxException jse) {
            // json incompatible - reset
            setup = new GameSetup();
            getSharedPreferences().edit().putString(GameSetup.class.getSimpleName(), null).apply();
            return;
        }
        // max tries
        EditText maxTriesInput = findViewById(R.id.max_tries_input);
        maxTriesInput.setText(String.valueOf(setup.getMaxTries()));
        // hole count
        Integer id = getItemId(holeCountSpinner, setup.getHoleCount());
        if (id != null) {
            holeCountSpinner.setSelection(id);
        }
        // color count
        id = getItemId(pinCountSpinner, setup.getColors().size());
        if (id != null) {
            pinCountSpinner.setSelection(id);
        }
        // duplicate pins
        duplicatePins.setChecked(setup.getDuplicatePins());
        // empty pins
        emptyPins.setChecked(setup.getEmptyPins());
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
        view.getRootView().setOnDragListener(removePinDragListener);
        // holes
        final LinearLayout solutionHoles = view.findViewById(R.id.solution_dialog_selection);
        int padding = toPixels(2);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 1f);
        for (int i = 0; i < holeCount; i++) {
            ImageView hole = new ImageView(this);
            hole.setPadding(padding, padding, padding, padding);
            hole.setLayoutParams(params);
            hole.setImageDrawable(colorDrawable(android.R.color.transparent));
            hole.setOnDragListener(dragListener);
            solutionHoles.addView(hole);
        }
        // colors
        final LinearLayout solutionColors = view.findViewById(R.id.solution_dialog_colors);
        for (int i = 0; i < setup.getColors().size(); i++) {
            ImageView pin = new ImageView(this);
            pin.setPadding(padding, padding, padding, padding);
            pin.setLayoutParams(params);
            pin.setImageDrawable(colorDrawable(setup.getColors().get(i)));
            pin.setOnTouchListener(touchListener);
            solutionColors.addView(pin);
        }
        final Context context = this;
        builder.setView(view).setTitle(R.string.setup_solution)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
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
                        boolean empty = emptyPins.isChecked();
                        boolean duplicate = duplicatePins.isChecked();
                        List<Integer> solution = new ArrayList<>();

                        for (int i = 0; i < solutionHoles.getChildCount(); i++) {
                            ImageView pin = (ImageView) solutionHoles.getChildAt(i);
                            int selectedColor = getColor(pin.getDrawable());
                            if (!empty && android.R.color.transparent == selectedColor) {
                                Toast.makeText(context, R.string.pins_missing, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (!duplicate && solution.contains(selectedColor)) {
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
                if (getSharedPreferences().edit().putString(GameSetup.class.getSimpleName(), json).commit()) {
                    startActivity(new Intent(context, GameActivity.class));
                    finish();
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
        boolean duplicate = duplicatePins.isChecked();
        for (int i = 0; i < holeCount; i++) {
            Random random = new Random();
            int randomColor = availableColors.get(random.nextInt(availableColors.size()));
            while (!duplicate && solution.contains(randomColor)) {
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

    @Override
    protected int getContentView() {
        return R.layout.activity_setup;
    }

    @Override
    protected int getMainLayout() {
        return R.id.setup_screen_root;
    }
}

