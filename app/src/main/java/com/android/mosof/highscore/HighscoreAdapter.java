package com.android.mosof.highscore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.mosof.R;

import java.util.List;

import static java.lang.String.valueOf;

public class HighscoreAdapter extends BaseAdapter {

    private Context context;
    private List<Highscore> highscores;

    public HighscoreAdapter(Context context, List<Highscore> highscores) {
        this.context = context;
        this.highscores = highscores;
    }

    @Override
    public int getCount() {
        return highscores.size();
    }

    @Override
    public Object getItem(int position) {
        return highscores.get(position);
    }

    @Override
    public long getItemId(int position) {
        return highscores.get(position).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.highscore_list_item, parent, false);
        Highscore highscore = highscores.get(position);
        TextView place = view.findViewById(R.id.highscore_row_pos);
        place.setText(valueOf(position + 1));
        TextView player = view.findViewById(R.id.highscore_row_name);
        player.setText(highscore.getPlayer());
        TextView tries = view.findViewById(R.id.highscore_row_tries);
        tries.setText(valueOf(highscore.getTries()));
        return view;
    }
}
