package com.google.android.gms.samples.vision.ocrreader;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.samples.vision.ocrreader.correct.ParcelableOcrGraphic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class VerifyActivity extends AppCompatActivity implements View.OnClickListener {
    ArrayList<YAndPrice> pricesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        // Unparcel the graphics data
        pricesList = getIntent().getParcelableArrayListExtra("prices");
        boolean parsed = ComputeUtils.labelSubtotalTaxAndTotal(pricesList);

        TextView pricesToVerify = findViewById(R.id.prices_to_verify);
        if(!parsed) {
            pricesToVerify.setTextColor(Color.RED);
        }

        pricesToVerify.setText("Prices detected:\n");

        for(YAndPrice yp : pricesList) {
            pricesToVerify.append(yp.toString() + "\n");
        }

        findViewById(R.id.verify_continue_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, SelectDiners.class);
        intent.putParcelableArrayListExtra("prices", pricesList);
        startActivity(intent);
    }
}
