package com.google.android.gms.samples.vision.ocrreader;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class SelectPayersActivity extends AppCompatActivity implements View.OnClickListener {
    String TAG = "Select Payers";
    static final int PICK_CONTACT = 1;
    ArrayList<String> payers = new ArrayList<>();
    ListView listView;
    ArrayAdapter<String> adapter;
    Button continueButton, addButton;
    private boolean has_shown_toast = false;
    EditText inputName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_payers);
        setTitle("Select Payers");

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, payers);

        listView = findViewById(R.id.payer_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                view.animate().setDuration(750).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                payers.remove(item);
                                adapter.notifyDataSetChanged();
                                view.setAlpha(1);
                                if(payers.isEmpty()) {
                                    continueButton.setEnabled(false);
                                }
                            }
                        });
            }

        });

        findViewById(R.id.select_contact_button).setOnClickListener(this);

        inputName = findViewById(R.id.editText);
        inputName.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                addButton.setEnabled(true);
            }
        });
        addButton = findViewById(R.id.select_add_button);
        addButton.setOnClickListener(this);
        addButton.setEnabled(false);


        continueButton = findViewById(R.id.select_continue);
        continueButton.setEnabled(false);
        continueButton.setOnClickListener(this);
    }

    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        try {
                            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            payers.add(name);
                            adapter.notifyDataSetChanged();
                            continueButton.setEnabled(true);
                            showToast();
                        }
                        catch (Exception ex)
                        {
                            ex.getMessage();
                        }
                    }
                }
                break;
        }
    }

//    // TODO make this interaction suck less
//    private void AccessContact()
//    {
//        List<String> permissionsNeeded = new ArrayList<String>();
//        final List<String> permissionsList = new ArrayList<String>();
//        if (!addPermission(permissionsList, Manifest.permission.READ_CONTACTS))
//            permissionsNeeded.add("Read Contacts");
//        if (permissionsList.size() > 0) {
//            if (permissionsNeeded.size() > 0) {
//                String message = "Do you want to grant access to " + permissionsNeeded.get(0);
//                for (int i = 1; i < permissionsNeeded.size(); i++)
//                    message = message + ", " + permissionsNeeded.get(i);
//                showMessageOKCancel(message,
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
//                                        REQUEST_MULTIPLE_PERMISSIONS);
//                            }
//                        });
//                return;
//            }
//            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
//                    REQUEST_MULTIPLE_PERMISSIONS);
//            return;
//        }
//    }

//    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
//        new AlertDialog.Builder(SelectPayersActivity.this)
//                .setMessage(message)
//                .setPositiveButton("OK", okListener)
//                .setNegativeButton("Cancel", null)
//                .create()
//                .show();
//    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.select_contact_button) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, PICK_CONTACT);
        }
        else if (v.getId() == R.id.select_add_button) {

            String newName = inputName.getText().toString();
            if(newName.isEmpty()) {
                return; // don't add blanks
            }
            payers.add(newName);
            adapter.notifyDataSetChanged();
            inputName.getText().clear();
            continueButton.setEnabled(true);
            showToast();
        }
        else if(v.getId() == R.id.select_continue) {
            Intent intent = new Intent(this, SplitActivity.class);
            ArrayList<AllocatedPrice> pricesList = getIntent().getParcelableArrayListExtra(ComputeUtils.PRICES);
            intent.putParcelableArrayListExtra(ComputeUtils.PRICES, pricesList);
            intent.putStringArrayListExtra(ComputeUtils.PAYERS, payers);
            startActivity(intent);
        }
    }

    private void showToast() {
        // Show Toast message: "Long press on name to delete"
        if(!has_shown_toast) {
            Toast toast = Toast.makeText(getApplicationContext(), "Long press on name to delete", Toast.LENGTH_LONG);
            toast.show();
            has_shown_toast = true;
        }
    }
}
