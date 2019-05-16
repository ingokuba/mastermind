package com.android.mosof;

import android.os.Bundle;

public class AboutActivity extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_about;
    }

    @Override
    protected int getMainLayout() {
        return R.id.about_layout;
    }
}
