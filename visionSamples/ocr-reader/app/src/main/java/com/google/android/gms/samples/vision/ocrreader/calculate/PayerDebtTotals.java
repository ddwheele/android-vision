package com.google.android.gms.samples.vision.ocrreader.calculate;

import android.graphics.Color;

/**
 * This is the aggregate of all the assigned debts
 */
public class PayerDebtTotals extends PayerDebt {


    public PayerDebtTotals(String name) {
        super(name, Color.BLACK);
    }

    @Override
    public void toggleSelected() {
        // never select this
    }

}
