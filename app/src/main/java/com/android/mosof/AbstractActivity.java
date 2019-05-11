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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.mosof.components.ColorDrawable;

public abstract class AbstractActivity extends AppCompatActivity {

    protected View.OnTouchListener touchListener = new PinOnTouchListener();
    protected View.OnDragListener dragListener = new HoleOnDragListener();
    private View.OnClickListener clickListener = new HoleOnClickListener();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        setBackground();
    }

    protected abstract int getContentView();

    protected abstract void setBackground();

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
}
