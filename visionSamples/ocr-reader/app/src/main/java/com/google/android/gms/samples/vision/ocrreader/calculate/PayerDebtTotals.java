package com.google.android.gms.samples.vision.ocrreader.calculate;

import android.graphics.Color;

/**
 * This is the aggregate of all the assigned debts
 */
public class PayerDebtTotals extends PayerDebt {

    public PayerDebtTotals(String name) {
        super(name, 0);
    }

    @Override
    public boolean isTotal() {
        return true;
    }

    @Override
    public void toggleSelected() {
        // never select this
    }

}
