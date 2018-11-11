package com.google.android.gms.samples.vision.ocrreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

// TODO WHY DOESN'T THIS RECOGNIZE POLYMORPHISM AAAAAAHHHHHH
public class ThreeColumnArrayAdapter extends ArrayAdapter<AllocatedPrice> {
    private final Context context;
    private final ArrayList<AllocatedPrice> values;

    public ThreeColumnArrayAdapter(Context context, ArrayList<AllocatedPrice> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.column_view, parent, false);
        TextView text1 = rowView.findViewById(R.id.first_column);
        TextView text2 = rowView.findViewById(R.id.second_column);
        TextView text3 = rowView.findViewById(R.id.third_column);
        text1.setText(values.get(position).getFirstColumnString());
        text2.setText(values.get(position).getSecondColumnString());
        text3.setText(values.get(position).getThirdColumnString());

        text1.setBackgroundColor(values.get(position).getThirdColumnBackgroundColor());

        return rowView;
    }
}
