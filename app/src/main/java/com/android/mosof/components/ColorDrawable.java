package com.android.mosof.components;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class ColorDrawable extends GradientDrawable {

    private Context context;
    private int colorResource;

    public ColorDrawable(@NonNull Context context) {
        this.context = context;
    }

    public int getColorResource() {
        return colorResource;
    }

    public void setColorResource(int res) {
        colorResource = res;
        super.setColor(ContextCompat.getColor(context, res));
    }
}
