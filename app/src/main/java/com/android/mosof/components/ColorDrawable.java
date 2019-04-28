package com.android.mosof.components;

import android.graphics.drawable.GradientDrawable;

public class ColorDrawable extends GradientDrawable {

    private int colorResource;

    public ColorDrawable(Orientation orientation, int[] colors) {
        super(orientation, colors);
    }

    public int getColorResource() {
        return colorResource;
    }

    public void setColorResource(int res) {
        colorResource = res;
    }
}
