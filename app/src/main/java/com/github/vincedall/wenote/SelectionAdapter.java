package com.github.vincedall.wenote;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SelectionAdapter extends ArrayAdapter<ListItemsSelection> {
    private final Context context;
    private final ArrayList<ListItemsSelection> itemsArrayList;

    public SelectionAdapter(Context context, ArrayList<ListItemsSelection> itemsArrayList) {
        super(context, R.layout.list_view_selection, itemsArrayList);
        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_view_selection, parent, false);
        TextView title = (TextView) view.findViewById(R.id.item_title);
        title.setText(itemsArrayList.get(position).getTitle());
        TextView date = (TextView) view.findViewById(R.id.item_date);
        date.setText(itemsArrayList.get(position).getDate());
        date.setTextColor(Color.GRAY);
        if (itemsArrayList.get(position).getSelected() || itemsArrayList.get(position).getWasSelected()){
            final ImageView image = view.findViewById(R.id.check);
            final int pos = position;
            image.setVisibility(View.VISIBLE);
            if (itemsArrayList.get(position).getWasSelected()){
                image.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View v) {
                        itemsArrayList.get(pos).setWasSelected(false);
                        Animation unexpand = AnimationUtils.loadAnimation(image.getContext(), R.anim.unexpand);
                        image.startAnimation(unexpand);
                        unexpand.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                image.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                    }

                    @Override
                    public void onViewDetachedFromWindow(View v) {
                    }
                });
            }
            if (!itemsArrayList.get(position).getAnimationPlayed()) {
                image.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View v) {
                        Animation expand = AnimationUtils.loadAnimation(image.getContext(), R.anim.expand);
                        image.startAnimation(expand);
                        itemsArrayList.get(pos).setAnimationPlayed(true);
                    }

                    @Override
                    public void onViewDetachedFromWindow(View v) {
                    }
                });
            }
        }
        return view;
    }

}
