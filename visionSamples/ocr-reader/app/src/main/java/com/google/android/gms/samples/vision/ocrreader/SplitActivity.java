package com.google.android.gms.samples.vision.ocrreader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SplitActivity extends AppCompatActivity implements View.OnClickListener {
    ArrayList<AllocatedPrice> priceList;
    ListView priceListView;
    ThreeColumnPricesAdapter priceAdapter;

    PayerDebtCoordinator payerCoordinator;
    ListView payerListView;
    ArrayAdapter<PayerDebt> payerAdapter;
    PayerDebt selectedPayer;
    PayerDebt totals;

    Button continueButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split);
        setTitle("Assign Items to Payers");

        priceList = getIntent().getParcelableArrayListExtra(ComputeUtils.PRICES);
        priceAdapter = new ThreeColumnPricesAdapter(this, priceList);

        priceListView = findViewById(R.id.split_prices_list);
        priceListView.setAdapter(priceAdapter);

        priceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                AllocatedPrice item =  (AllocatedPrice)parent.getItemAtPosition(position);
                if(selectedPayer != null) {
                    payerCoordinator.addPayerToItem(selectedPayer, item);
                    priceAdapter.notifyDataSetChanged();
                    payerAdapter.notifyDataSetChanged();
                }
                else if(!item.getPayerString().isEmpty()){
                    payerCoordinator.removeLastPayerFromItem(item);
                    priceAdapter.notifyDataSetChanged();
                    payerAdapter.notifyDataSetChanged();
                }
                else {
                    // TODO give error message and instructions
                }
            }
        });

        ArrayList<String> payerList = getIntent().getStringArrayListExtra(ComputeUtils.PAYERS);
        payerCoordinator = new PayerDebtCoordinator(payerList);
        totals = payerCoordinator.getTotals();

        payerAdapter = new ThreeColumnPayerAdapter(this, payerCoordinator.getPayerDebtList());

        payerListView = findViewById(R.id.split_payer_list);
//        View header = getLayoutInflater().inflate(R.layout.column_view, null);
//        TextView header1 = header.findViewById(R.id.first_column);
//        TextView header2 = header.findViewById(R.id.second_column);
//        TextView header3 = header.findViewById(R.id.third_column);
//
//        header1.setText("Name");
//        header2.setText("Subtotal");
//        header3.setText("+Tax");
//        payerListView.addHeaderView(header);

        payerListView.setAdapter(payerAdapter);
        payerListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
        payerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final PayerDebt tappedPayer = (PayerDebt) parent.getItemAtPosition(position);
                if(totals.equals(tappedPayer)) {
                    // never select the totals row, nothing can be assigned to it
                    return;
                }
                if(selectedPayer == null) {
                    // select on first tap
                    selectedPayer = tappedPayer;
                    if(selectedPayer != null) {
                        selectedPayer.toggleSelected();
                    }
                } else {
                    // if it's second tap, deselect it
                    if(selectedPayer.equals(tappedPayer)) {
                        selectedPayer.toggleSelected();
                        selectedPayer = null;
                    }
                    else { // change to select the new tapped player
                        selectedPayer.toggleSelected();
                        selectedPayer = tappedPayer;
                        if(selectedPayer != null) {
                            selectedPayer.toggleSelected();
                        }
                    }
                }

                // recolor
                payerAdapter.notifyDataSetChanged();
            }

        });

        continueButton = findViewById(R.id.split_continue_button);
        continueButton.setOnClickListener(this);
        showToast();
    }

    private void showToast() {
        Toast toast = Toast.makeText(getApplicationContext(),
                "Tap a payer and item to assign to assign payer to item.",
                Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.split_continue_button) {
            Intent intent = new Intent(this, DragActivity.class);
            startActivity(intent);
        }
    }
}
