package com.google.android.gms.samples.vision.ocrreader;

import android.util.Log;

import java.util.ArrayList;

public class PayerDebtCoordinator {
    final String TAG = "PayerDebtCoordinator";
    ArrayList<PayerDebt> payerDebtList;

    public PayerDebtCoordinator(ArrayList<String> payerList) {
        payerDebtList = ComputeUtils.createPayerDebtList(payerList);
    }

    ArrayList<PayerDebt> getPayerDebtList() {
        return payerDebtList;
    }

    void addPayerToItem(PayerDebt payer, AllocatedPrice item) {
        if(payer == null || item == null) {
            return;
        }
        ArrayList<String> otherPayers = item.getPayers();

        // add the new dude to the item, and the item to the dude
        item.addPayer(payer.name);
        payer.addItem(item);

        // tell everybody else to recalculate bc they're sharing now
        for(String oldPayer : otherPayers) {
            findPayerDebt(oldPayer).recalculate();
        }
    }

    void removeLastPayerFromItem(AllocatedPrice item) {
        if(item == null) {
            return;
        }
        String payer = item.removePayer();
        removePayerFromItem(findPayerDebt(payer), item);
    }


    void removePayerFromItem(PayerDebt payer, AllocatedPrice item) {
        if(payer == null || item == null) {
            return;
        }
        ArrayList<String> otherPayers = item.getPayers();

        // remove the dude from the item, and the item from the dude
        item.removePayer(payer.name);
        payer.removeItem(item);

        // tell everybody else to recalculate to take over his cost
        for(String oldPayer : otherPayers) {
            findPayerDebt(oldPayer).recalculate();
        }
    }

    PayerDebt findPayerDebt(String payer) {
        for (PayerDebt pd : payerDebtList) {
            if (pd.name.equals(payer)) {
                return pd;
            }
        }
        return null;
    }
}
