package com.google.android.gms.samples.vision.ocrreader;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.samples.vision.ocrreader.adapters.AssignPayersAdapter;
import com.google.android.gms.samples.vision.ocrreader.calculate.AssignedPrice;
import com.google.android.gms.samples.vision.ocrreader.calculate.PayerDebt;
import com.google.android.gms.samples.vision.ocrreader.calculate.PayerDebtCoordinator;
import com.google.android.gms.samples.vision.ocrreader.calculate.Utils;

import java.util.ArrayList;

public class AssignPayersActivity extends AppCompatActivity implements View.OnClickListener {
    final String TAG = "AssignPayersActivity";
    ArrayList<AssignedPrice> priceList;
    ArrayList<PayerTagGraphic> payerTags;
    ListView priceListView;
    AssignPayersAdapter priceAdapter;

    PayerDebtCoordinator payerCoordinator;
    PayerDebt selectedPayer;
    PayerDebt totals;

    Button continueButton;

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split);
        setTitle("Assign Payers to Items");

        setupPayerTags();
        setupPriceList();

        continueButton = findViewById(R.id.split_continue_button);
        continueButton.setOnClickListener(this);
        showInfoToast();
    }

    /**
     * Select or deselect payer, and change invert the colors on the tag
     */
    protected void toggleSelectedPayer() {
        for(PayerTagGraphic g : payerTags) {
            if(g.getName().equals(selectedPayer.getName())) {
                g.togglePayerTag();
            }
        }
        selectedPayer.toggleSelected();
    }

    protected void setupPayerTags() {
        ArrayList<String> payerList = getIntent().getStringArrayListExtra(Utils.PAYERS);
        payerCoordinator = new PayerDebtCoordinator(payerList);
        totals = payerCoordinator.getTotals();
        payerTags = new ArrayList<>();

        TagLayout tagLayout = findViewById(R.id.split_payer_cloud);
        LayoutInflater layoutInflater = getLayoutInflater();
        int counter = ColorUtils.COUNTER_START;
        for (String name : payerList) {
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
                    final PayerDebt tappedPayer = payerCoordinator.findPayerDebt(payerName);
                    if(selectedPayer == null) {
                        // select on first tap
                        selectedPayer = tappedPayer;
                        if(selectedPayer != null) {
                            toggleSelectedPayer();
                        }
                    } else {
                        // if it's second tap, deselect it
                        if(selectedPayer.equals(tappedPayer)) {
                            toggleSelectedPayer();
                            selectedPayer = null;
                        }
                        else { // change to select the new tapped player
                            toggleSelectedPayer();
                            selectedPayer = tappedPayer;
                            if(selectedPayer != null) {
                                toggleSelectedPayer();
                            }
                        }
                    }
                }
            });

            payerTags.add(new PayerTagGraphic(payerName, payerColor, tagTextView));
            tagLayout.addView(tagView);
            counter++;
        }
    }

    protected void setupPriceList() {
        priceList = getIntent().getParcelableArrayListExtra(Utils.PRICES);
        priceAdapter = new AssignPayersAdapter(this, priceList);

        priceListView = findViewById(R.id.split_prices_list);
        priceListView.setAdapter(priceAdapter);

        priceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                AssignedPrice item =  (AssignedPrice)parent.getItemAtPosition(position);
                if(selectedPayer != null) {
                    payerCoordinator.addPayerToItem(selectedPayer, item);
                    priceAdapter.notifyDataSetChanged();
                }
                else if(!item.getPayerString().isEmpty()){
                    payerCoordinator.removeLastPayerFromItem(item);
                    priceAdapter.notifyDataSetChanged();
                }
                else {
                    // TODO give error message and instructions
                }
            }
        });
    }

    private void showInfoToast() {
        Toast toast = Toast.makeText(getApplicationContext(),
                "Tap payer and item to assign payer to item.",
                Toast.LENGTH_LONG);
        toast.show();
    }

    private void showToast(String msg) {
        Toast toast = Toast.makeText(getApplicationContext(),
                msg,
                Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.split_continue_button) {
            Intent intent = new Intent(this, DisplayPayerTotalsActivity.class);
            intent.putExtra(Utils.PAYER_COORDINATOR, payerCoordinator);
            startActivity(intent);
        }
    }
}
