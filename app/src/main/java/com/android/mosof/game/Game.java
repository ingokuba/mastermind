package com.android.mosof.game;

import com.android.mosof.setup.GameSetup;

import java.util.ArrayList;

public class Game {
    private ArrayList<ArrayList<Integer>> rows;

    private Boolean undo = false;

    private GameSetup setup;

    public Game(ArrayList<ArrayList<Integer>> rows, GameSetup setup) {
        this.rows = rows;
        this.setup = setup;
    }

    public ArrayList<ArrayList<Integer>> getRows() {
        return rows;
    }

    public Game setUndo(Boolean undo) {
        this.undo = undo;
        return this;
    }

    public Boolean getUndo() {
        return undo;
    }

    public GameSetup getSetup() {
        return setup;
    }
}
