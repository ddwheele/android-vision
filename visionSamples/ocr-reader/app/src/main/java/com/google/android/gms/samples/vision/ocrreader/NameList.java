package com.google.android.gms.samples.vision.ocrreader;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class NameList extends AppCompatActivity implements View.OnClickListener {
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> namesList;
    private int selectedNameIndex;
    private String TAG = "NAMES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_list2);

        listView = (ListView) findViewById(R.id.names_list);
        namesList = new ArrayList<String>();

        // Adapter: You need three parameters 'the context, id of the layout (it will be where the data is shown),
        // and the array that contains the data
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, namesList);

        // Here, you set the data in your ListView
        listView.setAdapter(adapter);

        findViewById(R.id.names_button).setOnClickListener(this);
        findViewById(R.id.names_delete).setOnClickListener(this);
        findViewById(R.id.names_done).setOnClickListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                          public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                                              selectedNameIndex = position;
                                              ((Button)findViewById(R.id.names_delete)).setEnabled(true);
                                              Log.v(TAG, "List Item " + selectedNameIndex + " clicked, enabling delete button");
                                          }
                                      });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.names_button) {
            EditText et = findViewById(R.id.names_prompt);
            String newName = et.getText().toString();
            namesList.add(newName);
            adapter.notifyDataSetChanged();
            et.setText("");
        }
        else if (v.getId() == R.id.names_delete) {
            namesList.remove(selectedNameIndex);
            ((Button)findViewById(R.id.names_delete)).setEnabled(false);
            adapter.notifyDataSetChanged();
            Log.v(TAG, "Deleting item " + selectedNameIndex);
        }
        else if (v.getId() == R.id.names_done) {
            // go back to main and give it the listView
            Intent returnIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("names", namesList);
            Log.e(TAG, "namesList is this long: " + namesList.size());
            returnIntent.putExtras(bundle);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();

        }
    }


}
