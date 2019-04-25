package com.android.mosof;

import android.content.ClipData;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    //TODO: set colors/holes invisible if not needed
    //private final static int settingsColors = 6; // 6-8
    //private final static int settingsHoles = 4; // 4-5-6-8

    private View.OnTouchListener touchListener = new MyTouchListener();
    private View.OnDragListener dragListener = new MyDragListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ImageView color1 = findViewById(R.id.color1);
        ImageView color2 = findViewById(R.id.color2);
        ImageView color3 = findViewById(R.id.color3);
        ImageView color4 = findViewById(R.id.color4);
        ImageView color5 = findViewById(R.id.color5);
        ImageView color6 = findViewById(R.id.color6);
        ImageView color7 = findViewById(R.id.color7);
        ImageView color8 = findViewById(R.id.color8);

        color1.setOnTouchListener(touchListener);
        color2.setOnTouchListener(touchListener);
        color3.setOnTouchListener(touchListener);
        color4.setOnTouchListener(touchListener);
        color5.setOnTouchListener(touchListener);
        color6.setOnTouchListener(touchListener);
        color7.setOnTouchListener(touchListener);
        color8.setOnTouchListener(touchListener);

        ImageView hole1 = findViewById(R.id.hole1);
        ImageView hole2 = findViewById(R.id.hole2);
        ImageView hole3 = findViewById(R.id.hole3);
        ImageView hole4 = findViewById(R.id.hole4);
        ImageView hole5 = findViewById(R.id.hole5);
        ImageView hole6 = findViewById(R.id.hole6);
        ImageView hole7 = findViewById(R.id.hole7);
        ImageView hole8 = findViewById(R.id.hole8);

        hole1.setOnDragListener(dragListener);
        hole2.setOnDragListener(dragListener);
        hole3.setOnDragListener(dragListener);
        hole4.setOnDragListener(dragListener);
        hole5.setOnDragListener(dragListener);
        hole6.setOnDragListener(dragListener);
        hole7.setOnDragListener(dragListener);
        hole8.setOnDragListener(dragListener);
    }

    private final class MyDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {

            switch (event.getAction()) {
                case DragEvent.ACTION_DROP:
                    ImageView hole = (ImageView) v; //get object that the other was dragged on
                    hole.setImageResource(R.drawable.game_circle);
                    hole.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ImageView hole = (ImageView) v;
                            hole.setImageResource(R.drawable.game_hole);
                            hole.setOnClickListener(null);
                        }
                    });
                    break;
            }
            return true;
        }
    }

    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                    v.startDrag(data, shadowBuilder, v, 0);
                    break;
            }
            return true;
        }
    }
}