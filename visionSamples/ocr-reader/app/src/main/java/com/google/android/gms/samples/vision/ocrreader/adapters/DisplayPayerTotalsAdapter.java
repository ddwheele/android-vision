package com.google.android.gms.samples.vision.ocrreader.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.samples.vision.ocrreader.ColorUtils;
import com.google.android.gms.samples.vision.ocrreader.calculate.PayerDebt;
import com.google.android.gms.samples.vision.ocrreader.R;

import java.util.ArrayList;

// TODO WHY DOESN'T THIS RECOGNIZE POLYMORPHISM AAAAAAHHHHHH
public class DisplayPayerTotalsAdapter extends ArrayAdapter<PayerDebt> {
    private final Context context;
    private final ArrayList<PayerDebt> values;

    public DisplayPayerTotalsAdapter(Context context, ArrayList<PayerDebt> values) {
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

        PayerDebt payerDebt = values.get(position);
        text1.setText(payerDebt.getFirstColumnString());
        text2.setText(payerDebt.getSecondColumnString());
        text3.setText(payerDebt.getThirdColumnString());

        if(!payerDebt.isTotal()) {
            int rowColor = getColor(payerDebt);
            text1.setTextColor(rowColor);
            text2.setTextColor(rowColor);
            text3.setTextColor(rowColor);
        }
        return rowView;
    }

    private int getColor(PayerDebt payerDebt) {
        return ColorUtils.getNumColor(payerDebt.getNumberInList());
    }
}
