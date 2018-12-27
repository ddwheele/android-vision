package com.google.android.gms.samples.vision.ocrreader;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.samples.vision.ocrreader.adapters.VerifyPricesAdapter;
import com.google.android.gms.samples.vision.ocrreader.calculate.AssignedPrice;
import com.google.android.gms.samples.vision.ocrreader.calculate.Utils;
import com.google.android.gms.samples.vision.ocrreader.ocr.OcrCaptureActivity;

import java.util.ArrayList;
import java.util.Collections;

public class VerifyPricesActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "Verify Prices";
    ArrayList<AssignedPrice> priceList;
    ListView priceListView;
    VerifyPricesAdapter priceAdapter;
    Button continueButton, appendButton;
    TextView topMessage;

    private static final int VF_OCR_CAPTURE = 9016;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        continueButton = findViewById(R.id.verify_continue_button);
        appendButton = findViewById(R.id.verify_append_prices_button);
        topMessage = findViewById(R.id.prices_to_verify);

        priceList = getIntent().getParcelableArrayListExtra(Utils.PRICES);
        priceAdapter = new VerifyPricesAdapter(this, priceList);

        priceListView = findViewById(R.id.split_prices_list);
        priceListView.setAdapter(priceAdapter);

        priceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
               //TODO let user correct the price
            }
        });

        continueButton.setOnClickListener(this);
        appendButton.setOnClickListener(this);

        parsePrices();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(TAG, "I got a result");
        if(requestCode == VF_OCR_CAPTURE) {
            Log.d(TAG, "with the right request code");
            if (intent != null) {
                Log.d(TAG, "intent is not null");
                ArrayList<AssignedPrice> p2 = intent.getParcelableArrayListExtra(Utils.PRICES);
                Log.d(TAG, "prices has " + priceList.size() + " and p2 has " + p2.size());
                priceList.addAll(p2);
                Log.d(TAG, "NOW prices has " + priceList.size());
                parsePrices();
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.verify_continue_button) {
            Intent intent = new Intent(this, SelectPayersActivity.class);
            ArrayList<AssignedPrice> cleanPriceList = Utils.removeNonItemRows(priceList);
            intent.putParcelableArrayListExtra(Utils.PRICES, cleanPriceList);
            startActivity(intent);
        } else if(v.getId() == R.id.verify_append_prices_button) {
            // get Y value of last price
            // TODO is it stil guaranteed to be sorted from the last parsing?
            Collections.sort(priceList);
            float offset = priceList.get(priceList.size()-1).getYValue();
            Intent intent = new Intent(this, OcrCaptureActivity.class);
            intent.putExtra(Utils.OFFSET, offset);
            intent.putParcelableArrayListExtra(Utils.PRICES, priceList);
            startActivityForResult(intent, VF_OCR_CAPTURE);
        }
    }

    private void parsePrices() {
        boolean parsed = Utils.labelSubtotalTaxAndTotal(priceList);

        if(parsed) {
            parseSuccessful();
        } else {
            parseNotSuccessful();
        }
        priceAdapter.notifyDataSetChanged();
    }

    private void parseNotSuccessful() {
        setTitle("Partial Receipt");
        topMessage.setText("Add more prices or try again.");
        topMessage.setTextColor(Color.CYAN);
        appendButton.setEnabled(true);
        continueButton.setEnabled(false);
        Log.e(TAG, "not success ");
    }

    private void parseSuccessful() {
        setTitle("Verify Prices");
        topMessage.setText("Prices detected:\n");
        appendButton.setEnabled(false);
        continueButton.setEnabled(true);
        Log.d(TAG, "SUCCESS!!!!!!!!!!! " );
    }
}
