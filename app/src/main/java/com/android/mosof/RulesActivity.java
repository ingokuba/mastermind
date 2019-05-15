package com.android.mosof;

import android.os.Bundle;

public class RulesActivity extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_rules;
    }

    @Override
    protected int getMainLayout() {
        return R.id.rules_layout;
    }
}
