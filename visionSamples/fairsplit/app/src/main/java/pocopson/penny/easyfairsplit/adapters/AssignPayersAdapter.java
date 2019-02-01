package pocopson.penny.easyfairsplit.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import pocopson.penny.easyfairsplit.calculate.AssignedPrice;
import pocopson.penny.easyfairsplit.ColorUtils;
import pocopson.penny.easyfairsplit.R;

// TODO WHY DOESN'T THIS RECOGNIZE POLYMORPHISM AAAAAAHHHHHH
public class AssignPayersAdapter extends ArrayAdapter<AssignedPrice> {
    private final Context context;
    private final ArrayList<AssignedPrice> values;

    public AssignPayersAdapter(Context context, ArrayList<AssignedPrice> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.two_column_view_assign, parent, false);
        TextView text2 = rowView.findViewById(R.id.first_column);
        text2.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        TextView text3 = rowView.findViewById(R.id.second_column);

        AssignedPrice assignedPrice = values.get(position);
        text2.setText(assignedPrice.getSecondColumnString());
        text3.setText(assignedPrice.getThirdColumnString());

        int rowColor = getColor(assignedPrice);
        text2.setTextColor(rowColor);

        return rowView;
    }

    private int getColor(AssignedPrice assignedPrice) {
        if(assignedPrice.isItem()) {
            if(assignedPrice.hasNoPayers()) {
                return ColorUtils.MY_RED_COLOR;
            }
            else {
                return ColorUtils.MY_GREEN_COLOR;
            }
        }
        else {
            return Color.WHITE;
        }
    }
}
