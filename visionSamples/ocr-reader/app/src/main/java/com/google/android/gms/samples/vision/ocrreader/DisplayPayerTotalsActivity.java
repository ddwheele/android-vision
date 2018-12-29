package com.google.android.gms.samples.vision.ocrreader;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.telephony.SmsManager;

import com.google.android.gms.samples.vision.ocrreader.adapters.DisplayPayerTotalsAdapter;
import com.google.android.gms.samples.vision.ocrreader.calculate.PayerDebt;
import com.google.android.gms.samples.vision.ocrreader.calculate.PayerDebtCoordinator;
import com.google.android.gms.samples.vision.ocrreader.calculate.Utils;

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
        TextView header3 = header.findViewById(R.id.third_column);

        header1.setText("Name");
        header2.setText("+Tax");
        header3.setText("+Tip");
        payerListView.addHeaderView(header);

        payerListView.setAdapter(payerAdapter);
        payerListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
        payerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                PayerDebt payerDebt = (PayerDebt) parent.getItemAtPosition(position);
                showSendSmSDialog(DisplayPayerTotalsActivity.this, payerDebt);
            }
        });

        Log.e(TAG, "Android build version = " + android.os.Build.VERSION.SDK_INT);


        if (android.os.Build.VERSION.SDK_INT == 26) {
            // stupid Google bug that they refuse to fix in 8.0
            permissions = new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE};
        } else {
            permissions = new String[]{Manifest.permission.SEND_SMS};
        }
    }

    protected void showSendSmSDialog(Context c, final PayerDebt payerDebt) {
        Log.e(TAG, "inside showSendSmSDialog");
        final EditText taskEditText = new EditText(c);
        nameToText = payerDebt.getName();
        String withTax = twoDecimalFormat.format(payerDebt.getTotal());
        String withTip = twoDecimalFormat.format(payerDebt.getTotalAndTip());

        message = "FairSplit:  $" + withTax + " with tax and $" + withTip + " with 15% tip.";
        phoneNumber = null;
        taskEditText.setText(message);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Send Text Message")
                .setMessage("Text this message to " + nameToText + "?")
                .setView(taskEditText)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e(TAG, "inside showSendSmSDialog onClick()");
                        if (GuiUtils.hasPermissions(DisplayPayerTotalsActivity.this, permissions)) {
                            Log.e(TAG, "inside showSendSmSDialog onClick() - granted");
                            sendSms();
                        } else {
                            Log.e(TAG, "inside showSendSmSDialog onClick() - not granted");
                            GuiUtils.requestPermissions(DisplayPayerTotalsActivity.this,
                                    MY_PERMISSIONS_REQUEST_SEND_SMS, permissions);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.e(TAG, "got onRequestPermissionsResult callback");
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
        final EditText taskEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Enter Phone Number")
                .setMessage("Enter " + nameToText + "'s phone number.")
                .setView(taskEditText)
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        phoneNumber = String.valueOf(taskEditText.getText());
                        sendSms();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    public void sendSms() {
        if (phoneNumber == null) {
            showGetPhoneNumberDialog(DisplayPayerTotalsActivity.this);
            return;
        }
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
