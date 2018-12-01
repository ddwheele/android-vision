package com.google.android.gms.samples.vision.ocrreader;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.graphics.Color.*;

public class SplitActivity extends AppCompatActivity implements View.OnClickListener {
    final String TAG = "SplitActivity";
    ArrayList<AllocatedPrice> priceList;
    ArrayList<PayerTagGraphic> payerTags;
    ListView priceListView;
    ThreeColumnPricesAdapter priceAdapter;

    PayerDebtCoordinator payerCoordinator;
    ListView payerListView;
    PayerDebt selectedPayer;
    PayerDebt totals;

    Button continueButton;
    ArrayList<Integer> colorList;


    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split);
        setTitle("Assign Items to Payers");

        setupColorList();
        setupPayerTags();
        setupPriceList();

        continueButton = findViewById(R.id.split_continue_button);
        continueButton.setOnClickListener(this);
        showInfoToast();
    }

    protected void toggleSelectedPayer() {
        for(PayerTagGraphic g : payerTags) {
            if(g.name.equals(selectedPayer.name)) {
                g.togglePayerTag();
            }
        }

        selectedPayer.toggleSelected();
    }

    protected void setupPayerTags() {
        ArrayList<String> payerList = getIntent().getStringArrayListExtra(ComputeUtils.PAYERS);
        payerCoordinator = new PayerDebtCoordinator(payerList);
        totals = payerCoordinator.getTotals();
        payerTags = new ArrayList<>();


        TagLayout tagLayout = findViewById(R.id.split_payer_list);
        LayoutInflater layoutInflater = getLayoutInflater();
        int counter =0;
        for (String name : payerList) {
            View tagView = layoutInflater.inflate(R.layout.tag_layout, null, false);

            final TextView tagTextView = tagView.findViewById(R.id.tagTextView);
            final String payerName = name;
            final int payerColor = getNumColor(counter);
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
                            Log.e(TAG, "selecting on 1: " + selectedPayer);
                            toggleSelectedPayer();
                        }
                    } else {
                        // if it's second tap, deselect it
                        if(selectedPayer.equals(tappedPayer)) {
                            Log.e(TAG, "selecting on 2: " + selectedPayer);
                            toggleSelectedPayer();
                            selectedPayer = null;
                        }
                        else { // change to select the new tapped player
                            Log.e(TAG, "selecting on 3: " + selectedPayer);
                            toggleSelectedPayer();
                            selectedPayer = tappedPayer;
                            if(selectedPayer != null) {
                                Log.e(TAG, "selecting on 4: " + selectedPayer);
                                toggleSelectedPayer();
                            }
                        }
                    }

                    // recolor
                    showToast("Tapped payer: " + payerName);
                    //   payerAdapter.notifyDataSetChanged();
                }
            });

            payerTags.add(new PayerTagGraphic(payerName, payerColor, tagTextView));
            tagLayout.addView(tagView);
            counter++;
        }
    }

    protected void setupPriceList() {
        priceList = getIntent().getParcelableArrayListExtra(ComputeUtils.PRICES);
        priceAdapter = new ThreeColumnPricesAdapter(this, priceList);

        priceListView = findViewById(R.id.split_prices_list);
        priceListView.setAdapter(priceAdapter);

        //priceListView.setOnDragListener();

        priceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                AllocatedPrice item =  (AllocatedPrice)parent.getItemAtPosition(position);
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
                "Tap a payer and item to assign to assign payer to item.",
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
            Intent intent = new Intent(this, DragActivity.class);
            startActivity(intent);
        }
    }

    protected void setupColorList() {
        colorList = new ArrayList<>();
        colorList.add(rgb(33,97,140)); // dark blue
        colorList.add(rgb(206, 97, 85)); // salmon
        colorList.add(rgb(212, 172, 13)); // dark yellow

        colorList.add(rgb(136, 78, 160)); // red purple
        colorList.add(rgb(25, 111, 61)); // dark green
        colorList.add(rgb(72, 201, 176)); // teal
        colorList.add(rgb(230, 126, 34)); // orange
        colorList.add(rgb(91,44, 111)); // dark purple

        colorList.add(rgb(93,173, 226)); // cyan

        colorList.add(rgb(133, 146, 158)); //gray

    }

    protected int getNumColor(int index) {
        return colorList.get(index%(colorList.size()));
    }

}
