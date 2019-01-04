package pocopson.penny.fairsplit;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import android.telephony.SmsManager;

import pocopson.penny.fairsplit.adapters.DisplayPayerTotalsAdapter;
import pocopson.penny.fairsplit.calculate.PayerDebtCoordinator;
import pocopson.penny.fairsplit.calculate.PayerDebt;

import java.text.DecimalFormat;

public class DisplayPayerTotalsActivity extends Activity {
    final String TAG = "DisplayPayerTotalsActivity";
    PayerDebtCoordinator payerCoordinator;
    ListView payerListView;
    ArrayAdapter<PayerDebt> payerAdapter;
    PayerDebt totals;

    DecimalFormat twoDecimalFormat = new DecimalFormat("#.00");
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    String phoneNumber, message, nameToText;
    String[] permissions;
    int tipPercent = 15;
    TextView tipHeader;

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
                showSendSmSDialog(DisplayPayerTotalsActivity.this, payerDebt);
            }
        });

        if (android.os.Build.VERSION.SDK_INT == 26) {
            // stupid Android bug that Google refuses to fix in 8.0
            permissions = new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE};
        } else {
            permissions = new String[]{Manifest.permission.SEND_SMS};
        }
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

    protected void showSendSmSDialog(Context c, final PayerDebt payerDebt) {
        Log.e(TAG, "inside showSendSmSDialog");
        final EditText taskEditText = new EditText(c);
        nameToText = payerDebt.getName();
        String withTax = twoDecimalFormat.format(payerDebt.getTotal());
        String withTip = twoDecimalFormat.format(payerDebt.getTotalAndTip());

        message = "FairSplit:  $" + withTax + " with tax and $" + withTip + " with " + tipPercent + "% tip.";
        phoneNumber = payerDebt.getPhoneNumber();
        taskEditText.setText(message);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Send Text Message")
                .setMessage("Text this message to " + nameToText + "?")
                .setView(taskEditText)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Utils.hasPermissions(DisplayPayerTotalsActivity.this, permissions)) {
                            sendSms();
                        } else {
                            getPermission();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    protected void getPermission() {
        // uncomment to require user to click OK
//        View.OnClickListener listener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Utils.requestPermissions(DisplayPayerTotalsActivity.this,
//                        MY_PERMISSIONS_REQUEST_SEND_SMS, permissions);
//            }
//        };

        Snackbar.make(payerListView, R.string.permission_sms_rationale,
                Snackbar.LENGTH_LONG)
//                .setAction(R.string.ok, listener)
                .show();

        Utils.requestPermissions(DisplayPayerTotalsActivity.this,
                MY_PERMISSIONS_REQUEST_SEND_SMS, permissions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSms();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
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
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String phoneNumberString = String.valueOf(phoneNumberInput.getText());

                        if (Utils.phoneNumberOkay(phoneNumberString) ) {
                            phoneNumberInput.setError(null);
                            phoneNumber = phoneNumberString;
                            Log.e(TAG, "Good phone number: "+phoneNumber);
                            sendSms();
                        } else {
                            Toast.makeText(
                                    getApplicationContext(), "No SMS sent",
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
        sendSmsUnchecked();
    }

    private void sendSmsUnchecked() {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        Toast.makeText(
                getApplicationContext(), "SMS sent",
                Toast.LENGTH_LONG).
                show();

        nameToText = null;
        phoneNumber = null;
        message = null;
    }
}
