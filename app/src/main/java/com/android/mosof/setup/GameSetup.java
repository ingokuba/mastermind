package com.android.mosof.setup;

import com.android.mosof.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameSetup {

    public static final List<Integer> HOLE_COUNTS = Arrays.asList(4, 5, 6, 8);
    public static final List<Integer> PIN_COUNTS = Arrays.asList(6, 7, 8);
    public static final List<Integer> PIN_COLORS = Arrays.asList(android.R.color.holo_blue_dark, android.R.color.holo_red_dark,
            android.R.color.holo_purple, android.R.color.holo_green_dark, android.R.color.holo_orange_dark,
            android.R.color.darker_gray, android.R.color.holo_orange_light, android.R.color.holo_red_light);

    /**
     * Amount of holes for the pins.
     */
    private Integer holeCount;

    /**
     * Possible pin colors to put in holes.
     */
    private List<Integer> colors = new ArrayList<>();

    /**
     * Amount of maximum tries to finish the game.
     */
    private Integer maxTries;

    /**
     * Flag to determine whether the solution can contain duplicate pins.
     */
    private Boolean duplicatePins = false;

    /**
     * Flag to determine whether the solution can contain an empty hole.
     */
    private Boolean emptyPins = false;

    /**
     * The configuration of pins to end the game.
     */
    private List<Integer> solution;

    public Integer getHoleCount() {
        return holeCount;
    }

    public GameSetup setHoleCount(Integer holeCount) {
        this.holeCount = holeCount;
        return this;
    }

    public List<Integer> getColors() {
        return colors;
    }

    public GameSetup setColors(List<Integer> colors) {
        this.colors = colors;
        setSolution(null);
        return this;
    }

    public Integer getMaxTries() {
        return maxTries;
    }

    public GameSetup setMaxTries(Integer maxTries) {
        this.maxTries = maxTries;
        return this;
    }

    public Boolean getDuplicatePins() {
        return duplicatePins;
    }

    public GameSetup setDuplicatePins(Boolean duplicatePins) {
        this.duplicatePins = duplicatePins;
        setSolution(null);
        return this;
    }

    public Boolean getEmptyPins() {
        return emptyPins;
    }

    public GameSetup setEmptyPins(Boolean emptyPins) {
        this.emptyPins = emptyPins;
        setSolution(null);
        return this;
    }

    public List<Integer> getSolution() {
        return solution;
    }

    public GameSetup setSolution(List<Integer> solution) {
        this.solution = solution;
        return this;
    }

    public List<Integer> check() {
        List<Integer> errors = new ArrayList<>();
        if (maxTries == null || maxTries < 1) {
            errors.add(R.string.max_tries_error);
        }
        if (holeCount == null) {
            errors.add(R.string.hole_count_error);
        } else if (!HOLE_COUNTS.contains(holeCount)) {
            errors.add(R.string.hole_count_wrong);
        }
        checkColors(errors);
        if (solution == null) {
            errors.add(R.string.solution_error);
        } else if (solution.size() != holeCount) {
            errors.add(R.string.solution_error_length);
        }
        return errors;
    }

    private void checkColors(List<Integer> errors) {
        if (!PIN_COUNTS.contains(colors.size())) {
            errors.add(R.string.pin_count_wrong);
            return;
        }
        List<Integer> temp = new ArrayList<>();
        for (Integer pin : colors) {
            if (!PIN_COLORS.contains(pin)) {
                errors.add(R.string.pin_color_wrong);
                return;
            }
            if (temp.contains(pin)) {
                errors.add(R.string.pin_color_error);
            }
            temp.add(pin);
        }
    }
}
