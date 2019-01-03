package com.google.android.gms.samples.vision.ocrreader;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.samples.vision.ocrreader.calculate.AssignedPrice;
import com.google.android.gms.samples.vision.ocrreader.calculate.PayerDebt;

import java.util.ArrayList;

/**
 * Select payers from Contact List, type in payer names, or tap on recent payer names
 */
public class SelectPayersActivity extends AppCompatActivity implements View.OnClickListener {
    String TAG = "Select Payers";
    static final int PICK_CONTACT = 1;
    static final int GET_READ_CONTACT_PERMISSION = 55;
    ArrayList<PayerDebt> payerList = new ArrayList<>();
    ArrayList<PayerDebt> oldPayerList;
    ListView listView;
    ArrayAdapter<PayerDebt> adapter;
    Button continueButton, addButton;
    EditText inputName;
    String payersFilename = "RecentPayers.dat";
    ArrayList<PayerTagGraphic> payerTags;

    boolean hasPayerCloud = false;
    String[] permissions = new String[]{Manifest.permission.READ_CONTACTS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_payers);
        setTitle("Select Payers");

        makePayerList();

        findViewById(R.id.select_contact_button).setOnClickListener(this);

        makeManualInput();

        continueButton = findViewById(R.id.select_continue);
        continueButton.setEnabled(false);
        continueButton.setOnClickListener(this);
    }

    protected void setupOldPayerTags() {
        if (hasPayerCloud) {
            return;
        }
        payerTags = new ArrayList<>();

        TagLayout tagLayout = findViewById(R.id.old_payer_cloud);
        LayoutInflater layoutInflater = getLayoutInflater();
        for (final PayerDebt payerDebt : oldPayerList) {
            View tagView = layoutInflater.inflate(R.layout.tag_layout, null, false);

            final TextView tagTextView = tagView.findViewById(R.id.tagTextView);
            final String payerName = payerDebt.getName();
            final int payerColor = ColorUtils.getNumColor(payerDebt.getNumberInList());
            tagTextView.setText(payerName);

            GradientDrawable drawable = (GradientDrawable) tagTextView.getBackground();
            drawable.setColor(payerColor); // set solid color

            tagTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // add payerName to list
                    if (payerList.contains(payerDebt)) {
                        return;
                    }
                    payerList.add(0, payerDebt);
                    adapter.notifyDataSetChanged();
                    continueButton.setEnabled(true);
                    showToast();
                }
            });

            payerTags.add(new PayerTagGraphic(payerName, payerColor, tagTextView));
            tagLayout.addView(tagView);
        }

        hasPayerCloud = true;
    }

    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor cur = getContentResolver().query(contactData, null, null, null, null);
                    if (cur.getCount() > 0) {// that means some result has been found
                        if (cur.moveToNext()) {
                            String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                            String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            PayerDebt pickedPayer = new PayerDebt(name);
                            Log.d(TAG, "Names: " + name);

                            if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                                Cursor phones = getContentResolver().query(
                                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                        null,
                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                        null,
                                        null);
                                while (phones.moveToNext()) {
                                    int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                                    switch (type) {
                                        case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                            String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                            pickedPayer.setPhoneNumber(number);
                                            Log.d(TAG, "MOBILE Number: " + number);
                                            break;
                                    }
                                }
                                phones.close();
                            }
                            payerList.add(0, pickedPayer);
                        }
                    }
                    cur.close();
                    adapter.notifyDataSetChanged();
                    continueButton.setEnabled(true);
                    showToast();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.select_contact_button) {
            if (Utils.hasPermissions(SelectPayersActivity.this, permissions)) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
            } else {
                Utils.requestPermissions(this, GET_READ_CONTACT_PERMISSION, permissions);
            }
        } else if (v.getId() == R.id.select_add_button) {

            String newName = inputName.getText().toString();
            if (newName.isEmpty()) {
                return; // don't add blanks
            }
            payerList.add(0, new PayerDebt(newName));
            adapter.notifyDataSetChanged();
            inputName.getText().clear();
            continueButton.setEnabled(true);
            showToast();
        } else if (v.getId() == R.id.select_continue) {
            Intent intent = new Intent(this, AssignPayersActivity.class);
            ArrayList<AssignedPrice> pricesList = getIntent().getParcelableArrayListExtra(Utils.PRICES);
            intent.putParcelableArrayListExtra(Utils.PRICES, pricesList);
            intent.putParcelableArrayListExtra(Utils.PAYERS, payerList);
            startActivity(intent);
        }
    }

    private void showToast() {
        // Show Toast message: "Long press on name to delete"
        if (!HintsShown.isSelectPayersToast()) {
            Toast toast = Toast.makeText(
                    getApplicationContext(),
                    "Long press name to delete",
                    Toast.LENGTH_LONG);
            toast.show();
            HintsShown.setSelectPayersToast(true);
        }
    }

    void makeManualInput() {
        inputName = findViewById(R.id.editText);
        inputName.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(inputName.getText().toString().trim().length() > 0) {
                    addButton.setEnabled(true);
                }
            }
        });
        addButton = findViewById(R.id.select_add_button);
        addButton.setOnClickListener(this);
        addButton.setEnabled(false);
    }

    void makePayerList() {
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, payerList);

        listView = findViewById(R.id.payer_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final PayerDebt item = (PayerDebt) parent.getItemAtPosition(position);
                view.animate().setDuration(750).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                payerList.remove(item);
                                adapter.notifyDataSetChanged();
                                view.setAlpha(1);
                                if (payerList.isEmpty()) {
                                    continueButton.setEnabled(false);
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // TODO make this a priority queue
        Utils.saveData(payersFilename, payerList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        oldPayerList = (ArrayList<PayerDebt>) Utils.loadData(payersFilename);
        if(oldPayerList == null) {
            oldPayerList = new ArrayList<>();
        }
        setupOldPayerTags();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case GET_READ_CONTACT_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivityForResult(intent, PICK_CONTACT);
                }
            }
        }
    }
}
