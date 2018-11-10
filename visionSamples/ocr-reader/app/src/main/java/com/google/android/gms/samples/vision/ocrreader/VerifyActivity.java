package com.google.android.gms.samples.vision.ocrreader;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.samples.vision.ocrreader.correct.ParcelableOcrGraphic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class VerifyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        // Unparcel the graphics data
        ArrayList<YAndPrice> pricesList = getIntent().getParcelableArrayListExtra("prices");
        boolean parsed = ComputeUtils.labelSubtotalTaxAndTotal(pricesList);

        TextView pricesToVerify = findViewById(R.id.prices_to_verify);
        if(!parsed) {
            pricesToVerify.setTextColor(Color.RED);
        }

        pricesToVerify.setText("Prices detected:\n");

        for(YAndPrice yp : pricesList) {
            pricesToVerify.append(yp.toString() + "\n");
        }
    }
}
