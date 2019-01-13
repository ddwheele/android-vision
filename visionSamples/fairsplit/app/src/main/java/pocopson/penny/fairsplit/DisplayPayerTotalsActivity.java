package pocopson.penny.fairsplit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import pocopson.penny.fairsplit.adapters.DisplayPayerTotalsAdapter;
import pocopson.penny.fairsplit.calculate.PayerDebtCoordinator;
import pocopson.penny.fairsplit.calculate.PayerDebt;

import java.text.DecimalFormat;

public class DisplayPayerTotalsActivity extends Activity implements View.OnClickListener {
    final String TAG = "DisplayPayerTotalsActivity";
    PayerDebtCoordinator payerCoordinator;
    ListView payerListView;
    ArrayAdapter<PayerDebt> payerAdapter;
    PayerDebt totals;

    DecimalFormat twoDecimalFormat = new DecimalFormat("#.00");

    String phoneNumber, message, nameToText;
    int tipPercent = 15;
    TextView tipHeader;

    String sendString = "Open SMS App";
    String invalidNumber = "Invalid phone number";

    Button restartButton, closeButton;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_payer_totals);
        setTitle("Complete Totals");

        payerCoordinator = getIntent().getParcelableExtra(Utils.PAYER_COORDINATOR);
        totals = payerCoordinator.getTotals();

        payerAdapter = new DisplayPayerTotalsAdapter(this, payerCoordinator.getPayerDebtList());

        payerListView = findViewById(R.id.display_payer_totals);
        View header = getLayoutInflater().inflate(R.layout.column_view, null);
        TextView header1 = header.findViewById(R.id.first_column);
        TextView header2 = header.findViewById(R.id.second_column);
        tipHeader = header.findViewById(R.id.third_column);

        header1.setText("Name");
        header2.setText("+Tax");
        tipPercent = PreferencesStore.getInstance().getLastTipPercent();
        tipHeader.setText("+" + tipPercent + "%");
        payerListView.addHeaderView(header);

        payerListView.setAdapter(payerAdapter);
        payerListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
        payerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                if (position == 0) {
                    // change the tip percentage
                    showTipPercentPicker(DisplayPayerTotalsActivity.this);
                    return;
                }

                PayerDebt payerDebt = (PayerDebt) parent.getItemAtPosition(position);
                nameToText = payerDebt.getName();
                String withTax = twoDecimalFormat.format(payerDebt.getTotal());
                String withTip = twoDecimalFormat.format(payerDebt.getTotalAndTip());

                message = "FairSplit:  $" + withTax + " with tax and $" + withTip + " with " + tipPercent + "% tip.";
                phoneNumber = payerDebt.getPhoneNumber();
                sendSms();
            }
        });

        restartButton = findViewById(R.id.display_restart_button);
        restartButton.setOnClickListener(this);
        closeButton = findViewById(R.id.display_close_button);
        closeButton.setOnClickListener(this);
        showToast();
    }

    protected void changeTipPercent() {
        tipHeader.setText("+" + tipPercent + "%");
        PreferencesStore.getInstance().setLastTipPercent(tipPercent);
        payerCoordinator.changeTipPercent(tipPercent);
        payerAdapter.notifyDataSetChanged();
    }

    protected void showTipPercentPicker(Context context) {
        final AlertDialog.Builder d = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.number_picker_dialog, null);
        d.setTitle("Tip Percent");
        d.setMessage("Select Tip Percent");
        d.setView(dialogView);
        final NumberPicker numberPicker = dialogView.findViewById(R.id.dialog_number_picker);
        numberPicker.setMaxValue(50);
        numberPicker.setMinValue(10);
        numberPicker.setValue(tipPercent);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                Log.d(TAG, "onValueChange: ");
            }
        });
        d.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tipPercent = numberPicker.getValue();
                changeTipPercent();
            }
        });
        d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog alertDialog = d.create();
        alertDialog.show();
    }

    protected void showGetPhoneNumberDialog(Context c) {
        final EditText phoneNumberInput = new EditText(c);
        phoneNumberInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String newPriceString = String.valueOf(phoneNumberInput.getText());
                Log.d(TAG, "you entered: " + newPriceString);
                newPriceString = newPriceString.replaceAll(Utils.phoneRegex, "");

                int len = newPriceString.length();
                if (len > 0) {
                    phoneNumberInput.setError("Enter a valid phone number");
                } else {
                    phoneNumberInput.setError(null);
                }
            }
        });
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Enter Phone Number")
                .setMessage("Enter " + nameToText + "'s phone number.")
                .setView(phoneNumberInput)
                .setPositiveButton(sendString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String phoneNumberString = String.valueOf(phoneNumberInput.getText());

                        if (Utils.phoneNumberOkay(phoneNumberString)) {
                            phoneNumberInput.setError(null);
                            phoneNumber = phoneNumberString;
                            Log.e(TAG, "Good phone number: " + phoneNumber);
                            sendSms();
                        } else {
                            Toast.makeText(
                                    getApplicationContext(), invalidNumber,
                                    Toast.LENGTH_LONG).
                                    show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void showToast() {
        if (!HintsShown.isDisplayPayersToast()) {
            Toast toast = Toast.makeText(
                    getApplicationContext(),
                    "Tap payer name to text",
                    Toast.LENGTH_LONG);
            toast.show();
            HintsShown.setDisplayPayersToast(true);
        }
    }

    public void sendSms() {
        if (phoneNumber == null) {
            showGetPhoneNumberDialog(DisplayPayerTotalsActivity.this);
            return;
        }
        composeMmsMessage();
    }

    public void composeMmsMessage() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.setData(Uri.parse("smsto:" + phoneNumber));
        intent.putExtra("sms_body", message);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

        nameToText = null;
        phoneNumber = null;
        message = null;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.display_restart_button) {
            Intent intent = new Intent(this, FirstActivity.class);
            startActivity(intent);
        } else if(v.getId() == R.id.display_close_button) {
            moveTaskToBack(true);
        }
    }
}
