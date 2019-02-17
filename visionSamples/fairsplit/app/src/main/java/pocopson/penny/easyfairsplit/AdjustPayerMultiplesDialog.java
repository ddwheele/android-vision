package pocopson.penny.easyfairsplit;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import pocopson.penny.easyfairsplit.calculate.AssignedPrice;

public class AdjustPayerMultiplesDialog extends Dialog {
    private String TAG = "AdjustPayerMultiplesDialog";
    public Button doneButton, cancelButton;
    final String doneString = "Done", cancelString = "Cancel";
    Context context;
    AssignedPrice assignedPrice;

    public AdjustPayerMultiplesDialog(Context context, AssignedPrice ap) {
        super(context);
        this.context = context;
        assignedPrice = ap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_increment_number_dialog);

        LinearLayout incrementNumberListLayout = findViewById(R.id.increment_number_list);

        for (final String payerName : assignedPrice.getPayers()) {
            LinearLayout inner = new LinearLayout(context);
            inner.setOrientation(LinearLayout.HORIZONTAL);



            Button minusButton = new Button(context);
            minusButton.setText("-");
            minusButton.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
            minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, payerName + " minus");
                }
            });
            TextView countTextView = new TextView(context);
            countTextView.setText(" 0 ");
            Button plusButton = new Button(context);
            plusButton.setText("+");
            plusButton.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, payerName + " plus");
                }
            });
            inner.addView(minusButton);
            inner.addView(countTextView);
            inner.addView(plusButton);


            TextView nameTextView = new TextView(context);
            nameTextView.setText(payerName);
            nameTextView.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
            inner.addView(nameTextView);


            incrementNumberListLayout.addView(inner);
        }

        LinearLayout lastInner = new LinearLayout(context);
        lastInner.setOrientation(LinearLayout.HORIZONTAL);
        cancelButton = new Button(context);
        cancelButton.setText(cancelString);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        doneButton = new Button(context);
        doneButton.setText(doneString);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //context.finish();
            }
        });

        lastInner.addView(cancelButton);
        lastInner.addView(doneButton);
        incrementNumberListLayout.addView(lastInner);
    }
}
