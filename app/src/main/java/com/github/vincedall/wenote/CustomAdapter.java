package com.github.vincedall.wenote;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<ListItems> {
    private final Context context;
    private final ArrayList<ListItems> itemsArrayList;

    public CustomAdapter(Context context, ArrayList<ListItems> itemsArrayList) {
        super(context, R.layout.list_view_item, itemsArrayList);
        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_view_item, parent, false);
        TextView title = (TextView) view.findViewById(R.id.item_title);
        title.setText(itemsArrayList.get(position).getTitle());
        TextView date = (TextView) view.findViewById(R.id.item_date);
        date.setText(itemsArrayList.get(position).getDate());
        date.setTextColor(Color.GRAY);
        return view;
    }
}
