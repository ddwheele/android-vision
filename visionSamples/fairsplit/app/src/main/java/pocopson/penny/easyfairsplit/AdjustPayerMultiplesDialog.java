package pocopson.penny.easyfairsplit;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

import pocopson.penny.easyfairsplit.calculate.AssignedPrice;

public class AdjustPayerMultiplesDialog extends Dialog implements View.OnClickListener {
    private String TAG = "AdjustPayerMultiplesDialog";
    public Button yes, no;
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
        LayoutInflater layoutInflater = getLayoutInflater();

        for (final String payerName : assignedPrice.getPayers()) {
            View incrementNumberView = layoutInflater.inflate(R.layout.layout_increment_number, null, false);

            TextView nameTextView = incrementNumberView.findViewById(R.id.incrementNumberName);
            nameTextView.setText(payerName);

            final ElegantNumberButton numberButton = incrementNumberView.findViewById(R.id.incrementNumberButton);
            numberButton.setOnClickListener(new ElegantNumberButton.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String num = numberButton.getNumber();
                    Log.e(TAG, payerName + " is at " + num);
                }
            });
            incrementNumberListLayout.addView(incrementNumberView);
        }
        yes = findViewById(R.id.btn_yes);
        no = findViewById(R.id.btn_no);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                //context.finish();
                break;
            case R.id.btn_no:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
