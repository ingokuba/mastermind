package com.android.mosof;

import android.app.Activity;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.android.mosof.setup.GameSetup;
import com.android.mosof.setup.GameSetupActivity;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static java.lang.String.valueOf;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Every.everyItem;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class GameSetupActivityTest {

    @Rule
    public ActivityScenarioRule<GameSetupActivity> gameSetupActivity = new ActivityScenarioRule<>(GameSetupActivity.class);

    /**
     * Reset game setup object in preferences before each test.
     */
    @Before
    public void resetGameSetup() {
        PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
                .edit()
                .putString(GameSetup.class.getSimpleName(), null)
                .commit();
        gameSetupActivity.getScenario().recreate();
        onView(withId(R.id.max_tries_input)).perform(closeSoftKeyboard());
    }

    /**
     * Set all possible values.
     * Click start game button.
     * Expected: setup is created and stored in shared preferences.
     */
    @Test
    public void should_save_valid_configuration() {
        int maxTries = 1337;
        int holeCount = 5;
        int colorCount = 7;

        // input max tries in EditText
        onView(withId(R.id.max_tries_input))
                .perform(replaceText(valueOf(maxTries)), closeSoftKeyboard());
        // set hole count in spinner
        onView(withId(R.id.hole_count)).perform(click());
        onData(allOf(instanceOf(Integer.class), is(holeCount))).perform(click());
        // set color count in spinner
        onView(withId(R.id.pin_count)).perform(click());
        onData(allOf(instanceOf(Integer.class), is(colorCount))).perform(click());
        // check duplicate pins
        onView(withId(R.id.allow_duplicate_pins)).perform(click());
        // check empty pins
        onView(withId(R.id.allow_empty_pins)).perform(click());
        // press button
        onView(withId(R.id.start_game)).perform(click());

        GameSetup setup = getGameSetup();
        assertThat(setup, notNullValue());
        assertThat(setup.getMaxTries(), equalTo(maxTries));
        assertThat(setup.getHoleCount(), equalTo(holeCount));
        assertThat(setup.getColors().size(), equalTo(colorCount));
        assertThat(setup.getDuplicatePins(), is(true));
        assertThat(setup.getEmptyPins(), is(true));
        assertThat(setup.getSolution().size(), equalTo(holeCount));
    }

    /**
     * Input zero in maxTries EditText.
     * Click start game button.
     * Expected: setup not created.
     */
    @Test
    public void should_not_allow_0_max_tries() {
        onView(withId(R.id.max_tries_input))
                .perform(replaceText(valueOf(0)), closeSoftKeyboard());

        onView(withId(R.id.start_game)).perform(click());

        onView(withText(R.string.max_tries_error)).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
        assertThat(getGameSetup(), nullValue());
    }

    /**
     * Select more holes than pins.
     * Click start game button.
     * Expected: setup not created.
     */
    @Test
    public void should_not_allow_more_holes_than_pins() {
        // hole count = 8
        onView(withId(R.id.hole_count)).perform(click());
        onData(allOf(instanceOf(Integer.class), is(8))).perform(click());
        // pin count = 7
        onView(withId(R.id.pin_count)).perform(click());
        onData(allOf(instanceOf(Integer.class), is(7))).perform(click());

        onView(withId(R.id.start_game)).perform(click());

        onView(withText(R.string.hole_count_toobig_error)).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
        assertThat(getGameSetup(), nullValue());
    }

    /**
     * Select more holes than pins.
     * Set duplicate pins to true.
     * Click start game button.
     * Expected: setup is created and stored in shared preferences.
     */
    @Test
    public void should_allow_more_holes_than_pins_when_duplicate_allowed() {
        // hole count = 8
        onView(withId(R.id.hole_count)).perform(click());
        onData(allOf(instanceOf(Integer.class), is(8))).perform(click());
        // pin count = 7
        onView(withId(R.id.pin_count)).perform(click());
        onData(allOf(instanceOf(Integer.class), is(7))).perform(click());
        // duplicate pins = true
        onView(withId(R.id.allow_duplicate_pins)).perform(click());

        onView(withId(R.id.start_game)).perform(click());

        assertThat(getGameSetup(), notNullValue());
    }

    /**
     * Select more holes than pins.
     * Set empty pins to true.
     * Click start game button.
     * Expected: setup is created and stored in shared preferences.
     */
    @Test
    public void should_allow_more_holes_than_pins_when_empty_allowed() {
        // hole count = 8
        onView(withId(R.id.hole_count)).perform(click());
        onData(allOf(instanceOf(Integer.class), is(8))).perform(click());
        // pin count = 7
        onView(withId(R.id.pin_count)).perform(click());
        onData(allOf(instanceOf(Integer.class), is(7))).perform(click());
        // empty pins = true
        onView(withId(R.id.allow_empty_pins)).perform(click());

        onView(withId(R.id.start_game)).perform(click());

        assertThat(getGameSetup(), notNullValue());
    }

    /**
     * Allow duplicate pins.
     * Set mode to player.
     * Enter solution.
     * Click start game button.
     * Expected: setup is created and has desired solution.
     */
    @Test
    public void should_set_solution() {
        onView(withId(R.id.allow_duplicate_pins)).perform(click());
        onView(withId(R.id.mode_player)).perform(click());
        onView(withId(R.id.setup_solution)).perform(click());
        onView(withText(android.R.string.ok)).perform(click());

        onView(withId(R.id.start_game)).perform(click());

        onView(withText(R.string.solution_success)).inRoot(withDecorView(not(getCurrentActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
        GameSetup setup = getGameSetup();
        assertThat(setup, notNullValue());
        List<Integer> solution = setup.getSolution();
        assertThat(solution, everyItem(is(android.R.color.holo_blue_dark)));
    }

    /**
     * Set mode to player.
     * Enter solution with duplicate pins.
     * Click start game button.
     * Expected: setup not created.
     */
    @Test
    public void should_not_allow_duplicate_pins_in_solution() {
        onView(withId(R.id.mode_player)).perform(click());
        onView(withId(R.id.setup_solution)).perform(click());

        onView(withText(android.R.string.ok)).perform(click());

        // cannot check toast because root is dialog
        assertThat(getGameSetup(), nullValue());
    }

    private Activity getCurrentActivity() {
        final Activity[] activity = new Activity[1];
        onView(isRoot()).check(new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                while (view instanceof ViewGroup && ((ViewGroup) view).getChildCount() > 0) {
                    view = ((ViewGroup) view).getChildAt(0);
                    if (view.getContext() instanceof Activity) {
                        activity[0] = (Activity) view.getContext();
                        return;
                    }
                }
            }
        });
        return activity[0];
    }

    private GameSetup getGameSetup() {
        String json = PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext()).getString(GameSetup.class.getSimpleName(), null);
        if (json == null) {
            return null;
        }
        return new Gson().fromJson(json, GameSetup.class);
    }
}
