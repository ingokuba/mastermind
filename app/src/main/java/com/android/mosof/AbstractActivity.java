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
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.mosof.components.ColorDrawable;

public abstract class AbstractActivity extends AppCompatActivity {

    protected View.OnTouchListener touchListener = new PinOnTouchListener();
    protected View.OnDragListener dragListener = new HoleOnDragListener();
    protected View.OnTouchListener removePinTouchListener = new RemovePinTouchListener();
    protected View.OnDragListener removePinDragListener = new RemovePinDragListener();

    private static final String REMOVE = "remove";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        setBackground(getMainLayout());
    }

    protected abstract int getContentView();

    protected abstract int getMainLayout();

    private void setBackground(int layoutId) {
        int background = getSharedPreferences().getInt(MainActivity.BACKGROUND_KEY, R.drawable.wood_background);
        findViewById(layoutId).setBackgroundResource(background);
    }

    public SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    /**
     * Convert density pixels to pixels.
     */
    public int toPixels(int dps) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

    /**
     * Create a circle with the given color.
     *
     * @param color {@code android.R.color.transparent} for a hole;
     *              different color for a pin.
     */
    protected ColorDrawable colorDrawable(int color) {
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
     * Get the color resource id from the drawable.
     */
    protected int getColor(Drawable drawable) {
        if (drawable instanceof ColorDrawable) {
            // it's a pin
            ColorDrawable pin = (ColorDrawable) drawable;
            return pin.getColorResource();
        }
        return android.R.color.transparent;
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
                Drawable previousDrawable = hole.getDrawable(); // get drawable of hole/pin
                hole.setImageDrawable(pin.getDrawable());
                hole.setOnTouchListener(removePinTouchListener);
                if (REMOVE.contentEquals(event.getClipData().getDescription().getLabel())) {
                    pin.setImageDrawable(previousDrawable);
                    pin.setOnTouchListener(getColor(previousDrawable) == android.R.color.transparent ? null : removePinTouchListener);
                }
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
     * {@link android.view.View.OnTouchListener} for starting the removal of pins.
     */
    private final class RemovePinTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (MotionEvent.ACTION_DOWN == event.getAction()) {
                ClipData data = ClipData.newPlainText(REMOVE, "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(data, shadowBuilder, v, 0);
            }
            return true;
        }
    }

    /**
     * {@link android.view.View.OnDragListener} for removing pins from holes.
     */
    private final class RemovePinDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            if (DragEvent.ACTION_DROP == event.getAction() && REMOVE.contentEquals(event.getClipData().getDescription().getLabel())) {
                ImageView pin = (ImageView) event.getLocalState(); // get object that was dragged
                pin.setImageDrawable(colorDrawable(android.R.color.transparent));
                pin.setOnTouchListener(null);
            }
            return true;
        }
    }

    /**
     * Look for a value in a spinner component.
     *
     * @return the id of the item or null.
     */
    protected Integer getItemId(Spinner spinner, Object value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(value)) {
                return i;
            }
        }
        return null;
    }
}
