package com.google.android.gms.samples.vision.ocrreader;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class CorrectTextActivity extends AppCompatActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correct_text);

        textView = (TextView)findViewById(R.id.textView);
        String path = getIntent().getStringExtra("image");
        textView.setText(path);

        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setImageBitmap(BitmapFactory.decodeFile(path));
    }
}
