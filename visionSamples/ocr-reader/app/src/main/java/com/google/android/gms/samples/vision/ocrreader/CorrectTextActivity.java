package com.google.android.gms.samples.vision.ocrreader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.samples.vision.ocrreader.ui.camera.GraphicOverlay;

import java.util.ArrayList;

public class CorrectTextActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView textView;
    final String TAG = "CorrectTextActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correct_text);

        String path = getIntent().getStringExtra("image");
        ImageWithOverlay iwo = findViewById(R.id.imageWithOverlay);
        iwo.setImage(path);

        // Unparcel the graphics data
        ArrayList<ParcelableOcrGraphic> graphicList = getIntent().getParcelableArrayListExtra("graphics");
        iwo.setGraphicList(graphicList);

        findViewById(R.id.correct_add_prices).setOnClickListener(this);
        findViewById(R.id.correct_continue).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.correct_add_prices) {
//            EditText et = findViewById(R.id.names_prompt);
//            String newName = et.getText().toString();
//            namesList.add(newName);
//            adapter.notifyDataSetChanged();
//            et.setText("");
        }
        else if (v.getId() == R.id.correct_continue) {
//            namesList.remove(selectedNameIndex);
//            ((Button) findViewById(R.id.names_delete)).setEnabled(false);
//            adapter.notifyDataSetChanged();
//            Log.v(TAG, "Deleting item " + selectedNameIndex);
        }
    }
}
