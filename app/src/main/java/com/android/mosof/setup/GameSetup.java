package com.android.mosof.setup;

import com.android.mosof.R;

import java.util.ArrayList;
import java.util.List;

public class GameSetup {

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
        }
        checkColors(errors);
        if (solution == null) {
            errors.add(R.string.solution_error);
        }
        if (solution.size() != holeCount) {
            errors.add(R.string.solution_error_length);
        }
        return errors;
    }

    private void checkColors(List<Integer> errors) {
        List<Integer> temp = new ArrayList<>();
        for (Integer pin : colors) {
            if (temp.contains(pin)) {
                errors.add(R.string.pin_color_error);
            }
            temp.add(pin);
        }
    }
}
