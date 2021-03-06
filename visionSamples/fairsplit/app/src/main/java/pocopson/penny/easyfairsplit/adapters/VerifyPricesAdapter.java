package pocopson.penny.easyfairsplit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pocopson.penny.easyfairsplit.R;
import pocopson.penny.easyfairsplit.calculate.AssignedPrice;

public class VerifyPricesAdapter extends ArrayAdapter {
    private final Context context;
    private final ArrayList<AssignedPrice> values;

    public VerifyPricesAdapter(Context context, ArrayList<AssignedPrice> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.two_column_view_verify, parent, false);
        TextView text1 = rowView.findViewById(R.id.first_column);
        TextView text2 = rowView.findViewById(R.id.second_column);
        text1.setText(values.get(position).getFirstColumnString());
        text2.setText(values.get(position).getSecondColumnString());

        return rowView;
    }
}
