package com.android.mosof;

import android.content.ClipData;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.mosof.setup.GameSetup;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class GameActivity extends AppCompatActivity {

    private View.OnTouchListener touchListener = new PinOnTouchListener();
    private View.OnDragListener dragListener = new HoleOnDragListener();
    private View.OnClickListener clickListener = new HoleOnClickListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        GameSetup setup = loadGameSetup();
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

        TableRow holeRow = findViewById(R.id.hole_row);

        for (int i = 0; i < setup.getHoleCount(); i++) {
            ImageView hole = new ImageView(this);
            TableRow.LayoutParams params = new TableRow.LayoutParams();
            params.weight = 1f;
            hole.setLayoutParams(params);
            hole.setImageDrawable(getDrawable(R.drawable.game_hole));
            hole.setOnDragListener(dragListener);
            holeRow.addView(hole, i + 1);
        }
    }

    private ImageView getPin(int color) {
        ImageView pin = new ImageView(this);
        TableRow.LayoutParams params = new TableRow.LayoutParams();
        params.weight = 1f;
        pin.setLayoutParams(params);
        pin.setImageDrawable(colorDrawable(color));
        pin.setOnTouchListener(touchListener);
        return pin;
    }

    /**
     * Create a circle with the given color.
     */
    private Drawable colorDrawable(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(ContextCompat.getColor(this, color));
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setStroke(toPixels(2), ContextCompat.getColor(this, android.R.color.black));
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
     * Load game setup from shared preferences.
     */
    private GameSetup loadGameSetup() {
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
            hole.setImageResource(R.drawable.game_hole);
            hole.setOnClickListener(null);
        }
    }

    private SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }
}