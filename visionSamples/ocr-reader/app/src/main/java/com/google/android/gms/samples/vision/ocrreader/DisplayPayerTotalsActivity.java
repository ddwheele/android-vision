package com.google.android.gms.samples.vision.ocrreader;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class DisplayPayerTotalsActivity extends Activity {
    PayerDebtCoordinator payerCoordinator;
    ListView payerListView;
    ArrayAdapter<PayerDebt> payerAdapter;
    PayerDebt totals;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_payer_totals);
        setTitle("Complete Totals");

        payerCoordinator = getIntent().getParcelableExtra(ComputeUtils.PAYER_COORDINATOR);
        totals = payerCoordinator.getTotals();

        payerAdapter = new ThreeColumnPayerAdapter(this, payerCoordinator.getPayerDebtList());

        payerListView = findViewById(R.id.display_payer_totals);
        View header = getLayoutInflater().inflate(R.layout.column_view, null);
        TextView header1 = header.findViewById(R.id.first_column);
        TextView header2 = header.findViewById(R.id.second_column);
        TextView header3 = header.findViewById(R.id.third_column);

        header1.setText("Name");
        header2.setText("+Tax");
        header3.setText("+Tip");
        payerListView.addHeaderView(header);

        payerListView.setAdapter(payerAdapter);
        payerListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
    }
}
