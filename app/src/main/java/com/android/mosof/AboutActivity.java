package com.android.mosof;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView version = findViewById(R.id.about_version);
        String versionName;
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "unknown";
        }
        version.setText(String.format(getString(R.string.app_version), versionName));
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
