package com.google.android.gms.samples.vision.ocrreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SplitActivity extends AppCompatActivity {
    ArrayList<AllocatedPrice> priceList;
    ListView priceListView;
    ThreeColumnArrayAdapter priceAdapter;

    ArrayList<PayerDebt> payerDebtList;
    ListView payerListView;
    ArrayAdapter<PayerDebt> payerAdapter;
    PayerDebt selectedPayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split);
        setTitle("Assign Items to Payers");

        priceList = getIntent().getParcelableArrayListExtra("prices");
        priceAdapter = new ThreeColumnArrayAdapter(this, priceList);

        priceListView = findViewById(R.id.split_prices_list);
        priceListView.setAdapter(priceAdapter);

        priceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                AllocatedPrice item =  (AllocatedPrice)parent.getItemAtPosition(position);
                if(selectedPayer != null) {
                    item.addPayer(selectedPayer.name);
                    selectedPayer.addItem(item);
                    priceAdapter.notifyDataSetChanged();
                    payerAdapter.notifyDataSetChanged();
                }
                else if(!item.getPayerString().isEmpty()){
                    removePayer(item);
                    priceAdapter.notifyDataSetChanged();
                    payerAdapter.notifyDataSetChanged();
                }
                else {
                    // TODO give error message and instructions
                }
            }
        });

        ArrayList<String> payerList = getIntent().getStringArrayListExtra("payers");
        payerDebtList = ComputeUtils.createPayerDebtList(payerList);

        payerAdapter = new ThreeColumnPayerAdapter(this, payerDebtList);

        payerListView = findViewById(R.id.split_payer_list);
        payerListView.setAdapter(payerAdapter);

        payerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final PayerDebt tappedPayer = (PayerDebt) parent.getItemAtPosition(position);
                if(selectedPayer != null && selectedPayer.equals(tappedPayer.name)) {
                    // deselect if second tap
                    selectedPayer = null;
                } else {
                    // select on first tap
                    selectedPayer = tappedPayer;
                }
            }

        });
    }

    private void removePayer(AllocatedPrice item) {
        // remove payer from item
        String payer = item.removePayer();
        // see who it is, find him, tell him to delete item
        // TODO do this a better way, like make payerDebtList into a HashMap. But request is rare
        for(PayerDebt pd : payerDebtList) {
            if(pd.name.equals(payer)) {
                pd.removeItem(item);
            }
        }
    }
}
