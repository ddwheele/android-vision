package com.google.android.gms.samples.vision.ocrreader;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class VerifyPricesActivity extends AppCompatActivity implements View.OnClickListener {
    ArrayList<AllocatedPrice> pricesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        setTitle("Verify Prices");

        pricesList = getIntent().getParcelableArrayListExtra("prices");
        boolean parsed = ComputeUtils.labelSubtotalTaxAndTotal(pricesList);
        Button continueButton = findViewById(R.id.verify_continue_button);

        TextView pricesToVerify = findViewById(R.id.prices_to_verify);
        pricesToVerify.setText("Prices detected:\n");

        if(!parsed) {
            pricesToVerify.setText("Parsing failed, please try again");
            pricesToVerify.setTextColor(Color.RED);
            continueButton.setEnabled(false);
        }

        for(AllocatedPrice yp : pricesList) {
            pricesToVerify.append(yp.toString() + "\n");
        }
        continueButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, SelectPayersActivity.class);
        intent.putParcelableArrayListExtra("prices", pricesList);
        startActivity(intent);
    }
}
