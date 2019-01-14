package pocopson.penny.fairsplit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pocopson.penny.fairsplit.ColorUtils;
import pocopson.penny.fairsplit.R;
import pocopson.penny.fairsplit.calculate.PayerDebt;

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
        View rowView = inflater.inflate(R.layout.three_column_view, parent, false);
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
