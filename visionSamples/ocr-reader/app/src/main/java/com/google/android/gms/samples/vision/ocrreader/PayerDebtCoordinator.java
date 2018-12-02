package com.google.android.gms.samples.vision.ocrreader;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

public class PayerDebtCoordinator implements Parcelable {
    //final String TAG = "PayerDebtCoordinator";
    ArrayList<PayerDebt> payerDebtList;
    PayerDebt totals;

    public PayerDebtCoordinator(ArrayList<String> payerList) {
        payerDebtList = ComputeUtils.createPayerDebtList(payerList);
        totals = payerDebtList.get(payerDebtList.size()-1);
    }

    protected PayerDebtCoordinator(Parcel in) {
        payerDebtList = in.createTypedArrayList(PayerDebt.CREATOR);
        totals = in.readParcelable(PayerDebt.class.getClassLoader());
    }

    public static final Creator<PayerDebtCoordinator> CREATOR = new Creator<PayerDebtCoordinator>() {
        @Override
        public PayerDebtCoordinator createFromParcel(Parcel in) {
            return new PayerDebtCoordinator(in);
        }

        @Override
        public PayerDebtCoordinator[] newArray(int size) {
            return new PayerDebtCoordinator[size];
        }
    };

    public PayerDebt getTotals() { return totals; }

    ArrayList<PayerDebt> getPayerDebtList() {
        return payerDebtList;
    }

    void addPayerToItem(PayerDebt payer, AllocatedPrice item) {
        if(payer == null || item == null) {
            return;
        }
        ArrayList<String> otherPayers = item.getPayers();

        if(otherPayers.isEmpty()) {
            // this item is getting paid for, for the first time
            totals.addItem(item);
        }

        // add the new payer to the item, and the item to the payer
        item.addPayer(payer.name);
        payer.addItem(item);


        // if anybody else, tell to recalculate bc they're sharing now
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
        item.removePayer(payer.name);
        payer.removeItem(item);

        ArrayList<String> payersLeft = item.getPayers();


        if(payersLeft.isEmpty()) {
            // nobody left to pay for it
            totals.removeItem(item);
        }

        // tell everybody else to recalculate to take over his cost
        for(String oldPayer : payersLeft) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(payerDebtList);
        dest.writeParcelable(totals, flags);
    }
}
