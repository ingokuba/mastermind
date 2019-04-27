package com.android.mosof;

import com.android.mosof.setup.GameSetup;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

public class GameSetupTest {

    @Test
    public void should_succeed_check_for_valid_setup() {
        GameSetup setup = validSetup();

        List<Integer> errors = setup.check();

        assertThat(errors, empty());
    }

    /**
     * Generate a valid {@link GameSetup} object.
     */
    private GameSetup validSetup() {
        int holeCount = GameSetup.HOLE_COUNTS.get(0);
        return new GameSetup().setMaxTries(10)
                .setColors(GameSetup.PIN_COLORS)
                .setHoleCount(holeCount)
                .setSolution(generateSolution(holeCount));
    }

    /**
     * Generate a solution of the desired length.
     *
     * @param holeCount Amount of holes aka length of solution.
     */
    private List<Integer> generateSolution(int holeCount) {
        List<Integer> solution = new ArrayList<>();
        for (int i = 0; i < holeCount; i++) {
            solution.add(GameSetup.PIN_COLORS.get(i));
        }
        return solution;
    }
}
