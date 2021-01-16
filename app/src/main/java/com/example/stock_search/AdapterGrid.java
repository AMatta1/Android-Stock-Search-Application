package com.example.stock_search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterGrid extends ArrayAdapter {

    ArrayList<String> SummaryList = new ArrayList<>();

    public AdapterGrid(Context context, int textViewResourceId, ArrayList objects) {
        super(context, textViewResourceId, objects);
        SummaryList = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.prices_layout, null);
        TextView textView1 = (TextView) v.findViewById(R.id.textView);
        textView1.setText(SummaryList.get(position));
        return v;
    }

}