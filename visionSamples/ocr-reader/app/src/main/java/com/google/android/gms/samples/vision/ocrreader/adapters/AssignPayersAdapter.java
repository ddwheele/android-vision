package com.google.android.gms.samples.vision.ocrreader.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.samples.vision.ocrreader.calculate.AllocatedPrice;
import com.google.android.gms.samples.vision.ocrreader.R;

import java.util.ArrayList;

// TODO WHY DOESN'T THIS RECOGNIZE POLYMORPHISM AAAAAAHHHHHH
public class AssignPayersAdapter extends ArrayAdapter<AllocatedPrice> {
    private final Context context;
    private final ArrayList<AllocatedPrice> values;

    public AssignPayersAdapter(Context context, ArrayList<AllocatedPrice> values) {
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

        text1.setTextColor(values.get(position).getFirstColumnTextColor());
        text2.setTextColor(values.get(position).getSecondColumnTextColor());

//        text3.setBackgroundColor(values.get(position).getThirdColumnBackgroundColor());

        return rowView;
    }
}
