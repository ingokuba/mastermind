package com.android.mosof;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonRules = findViewById(R.id.app_rules);
        Button buttonAbout = findViewById(R.id.app_about);

        buttonRules.setOnClickListener(this);
        buttonAbout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.app_rules:
                Intent intentRules = new Intent(this, RulesActivity.class);
                startActivity(intentRules);
                break;
            case R.id.app_about:
                Intent intentAbout = new Intent(this, AboutActivity.class);
                startActivity(intentAbout);
                break;
            default:
                break;
        }
    }
}
