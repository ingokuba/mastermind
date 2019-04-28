package com.android.mosof;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ColorAdapter extends BaseAdapter {

    private List<Integer> colors;
    private Context context;

    public ColorAdapter(Context context, List<Integer> colors) {
        this.context = context;
        this.colors = colors;
    }

    @Override
    public int getCount() {
        return colors.size();
    }

    @Override
    public Object getItem(int pos) {
        return colors.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.spinner_item, parent, false);
        TextView item = view.findViewById(android.R.id.text1);
        item.setBackgroundResource(colors.get(pos));
        return view;
    }
}
