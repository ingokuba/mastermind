package com.android.mosof;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class HighscoreActivity extends AppCompatActivity {

    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        listView=findViewById(R.id.highscore);

        final ArrayList<String> arrayList=new ArrayList<>();

        arrayList.add("1. " + "Eric " + "8" + " Farben " + "8" + " Pins " + "1" + " Züge ");
        arrayList.add("2. " + "Michel " + "7 " + " Farben " + "8" + " Pins " + "3" + " Züge ");
        arrayList.add("3. " + "Hansi " + "7 " + " Farben " + "7" + " Pins " + "10" + " Züge ");
        arrayList.add("4. " + "Michael " + "7 " + " Farben " + "7" + " Pins " + "15" + " Züge ");
        arrayList.add("5. " + "Timi " + "4" + " Farben " + "4" + " Pins " + "3" + " Züge ");
        arrayList.add("6. " + "Name " + " Farben " + " Pins " + " Züge ");
        arrayList.add("7. " + "Name " + " Farben " + " Pins " + " Züge ");
        arrayList.add("8. " + "Name " + " Farben " + " Pins " + " Züge ");
        arrayList.add("9. " + "Name " + " Farben " + " Pins " + " Züge ");
        arrayList.add("10. " + "Name " + " Farben " + " Pins " + " Züge ");

        ArrayAdapter arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,arrayList);

        listView.setAdapter(arrayAdapter);
    }
}
