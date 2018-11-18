package com.google.android.gms.samples.vision.ocrreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class SplitActivity extends AppCompatActivity {
    ArrayList<AllocatedPrice> priceList;
    ListView priceListView;
    ThreeColumnPricesAdapter priceAdapter;

    PayerDebtCoordinator payerCoordinator;
    ListView payerListView;
    ArrayAdapter<PayerDebt> payerAdapter;
    PayerDebt selectedPayer;

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

        payerAdapter = new ThreeColumnPayerAdapter(this, payerCoordinator.getPayerDebtList());

        payerListView = findViewById(R.id.split_payer_list);
        View header = getLayoutInflater().inflate(R.layout.column_view, null);
        TextView header1 = header.findViewById(R.id.first_column);
        TextView header2 = header.findViewById(R.id.second_column);
        TextView header3 = header.findViewById(R.id.third_column);

        header1.setText("Name");
        header2.setText("+Tax");
        header3.setText("+Tip");

        payerListView.addHeaderView(header);
        payerListView.setAdapter(payerAdapter);

        payerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final PayerDebt tappedPayer = (PayerDebt) parent.getItemAtPosition(position);
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
    }
}
