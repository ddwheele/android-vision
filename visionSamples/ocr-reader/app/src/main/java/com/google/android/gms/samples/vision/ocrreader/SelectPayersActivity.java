package com.google.android.gms.samples.vision.ocrreader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Environment;
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
import com.google.android.gms.samples.vision.ocrreader.calculate.PayerDebtCoordinator;
import com.google.android.gms.samples.vision.ocrreader.calculate.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Select payers from Contact List, type in payer names, or tap on recent payer names
 */
public class SelectPayersActivity extends AppCompatActivity implements View.OnClickListener {
    String TAG = "Select Payers";
    static final int PICK_CONTACT = 1;
    ArrayList<String> payerList = new ArrayList<>();
    ArrayList<String> oldPayerList = new ArrayList<>();
    ListView listView;
    ArrayAdapter<String> adapter;
    Button continueButton, addButton;
    private boolean has_shown_toast = false;
    EditText inputName;
    String filename = "RecentPayers.txt";
    ArrayList<PayerTagGraphic> payerTags;
    private String filepath = "MyFileStorage";
    File myExternalFile;
    boolean hasPayerCloud = false;

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

        myExternalFile = new File(getExternalFilesDir(filepath), filename);
    }

    protected void setupOldPayerTags() {
        if(hasPayerCloud) {
            return;
        }
        payerTags = new ArrayList<>();

        TagLayout tagLayout = findViewById(R.id.old_payer_cloud);
        LayoutInflater layoutInflater = getLayoutInflater();
        int counter = ColorUtils.COUNTER_START;
        Log.e(TAG, "Making Payer Cloud");
        for (String name : oldPayerList) {
            View tagView = layoutInflater.inflate(R.layout.tag_layout, null, false);

            final TextView tagTextView = tagView.findViewById(R.id.tagTextView);
            final String payerName = name;
            final int payerColor = ColorUtils.getNumColor(counter);
            tagTextView.setText(payerName);

            GradientDrawable drawable = (GradientDrawable)tagTextView.getBackground();
            drawable.setColor(payerColor); // set solid color

            tagTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // add payerName to list
                    if(payerList.contains(payerName)) {
                        return;
                    }
                    payerList.add(payerName);
                    adapter.notifyDataSetChanged();
                    continueButton.setEnabled(true);
                    showToast();
                }
            });

            payerTags.add(new PayerTagGraphic(payerName, payerColor, tagTextView));
            tagLayout.addView(tagView);
            counter++;
            Log.e(TAG, "Added " + name + " to Payer Cloud");
        }
        if(!payerTags.isEmpty()) {
            hasPayerCloud = true;
        }
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
                            payerList.add(name);
                            adapter.notifyDataSetChanged();
                            continueButton.setEnabled(true);
                            showToast();
                        } catch (Exception ex) {
                            ex.getMessage();
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.select_contact_button) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, PICK_CONTACT);
        } else if (v.getId() == R.id.select_add_button) {

            String newName = inputName.getText().toString();
            if (newName.isEmpty()) {
                return; // don't add blanks
            }
            payerList.add(newName);
            adapter.notifyDataSetChanged();
            inputName.getText().clear();
            continueButton.setEnabled(true);
            showToast();
        } else if (v.getId() == R.id.select_continue) {
            Intent intent = new Intent(this, AssignPayersActivity.class);
            ArrayList<AssignedPrice> pricesList = getIntent().getParcelableArrayListExtra(Utils.PRICES);
            intent.putParcelableArrayListExtra(Utils.PRICES, pricesList);
            intent.putStringArrayListExtra(Utils.PAYERS, payerList);
            startActivity(intent);
        }
    }

    private void showToast() {
        // Show Toast message: "Long press on name to delete"
        if (!has_shown_toast) {
            Toast toast = Toast.makeText(getApplicationContext(), "Long press a name to delete", Toast.LENGTH_LONG);
            toast.show();
            has_shown_toast = true;
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
                addButton.setEnabled(true);
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
                final String item = (String) parent.getItemAtPosition(position);
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
        Log.e(TAG, "*********** PAUSING **************");
        super.onPause();
        saveData();
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "*********** RESUMING **************");
        super.onResume();
        loadData();
        setupOldPayerTags();

    }

    private void saveData() {
        String str = "Saved data. Saved data. Saved data. ";
        if(!isExternalStorageWritable()) {
            Log.e(TAG, "Not writable");
        } else {
            Log.e(TAG, "Writable");
        }

        try {
            FileOutputStream fos = new FileOutputStream(myExternalFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(payerList);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadData() {
        if(!isExternalStorageReadable()) {
            Log.e(TAG, "Not readable");
        }  else {
            Log.e(TAG, "Readable");
        }

        try {
            FileInputStream fis = new FileInputStream(myExternalFile);
            ObjectInputStream ois = new ObjectInputStream(fis);

            oldPayerList = (ArrayList<String>) ois.readObject();

            ois.close();

            Log.e(TAG, "GOT DATA!!!!!!! " );
            for(String s : oldPayerList) {
                Log.e(TAG, "\t" + s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (oldPayerList == null) {
            oldPayerList = new ArrayList<>();
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
