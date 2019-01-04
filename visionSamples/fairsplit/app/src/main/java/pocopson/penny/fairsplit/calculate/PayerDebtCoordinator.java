package pocopson.penny.fairsplit.calculate;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class PayerDebtCoordinator implements Parcelable {
    //final String TAG = "PayerDebtCoordinator";
    ArrayList<PayerDebt> payerDebtList;
    PayerDebt totals;

    public PayerDebtCoordinator(ArrayList<PayerDebt> payerList) {
        payerDebtList = payerList;
        payerDebtList.add(new PayerDebtTotals());
        totals = payerDebtList.get(payerDebtList.size() - 1);
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

    public PayerDebt getTotals() {
        return totals;
    }

    public ArrayList<PayerDebt> getPayerDebtList() {
        return payerDebtList;
    }

    public void addPayerToItem(PayerDebt payer, AssignedPrice item) {
        if (payer == null || item == null) {
            return;
        }
        ArrayList<String> otherPayers = item.getPayers();

        if (otherPayers.isEmpty()) {
            // this item is getting paid for, for the first time
            totals.addItem(item);
        }

        // add the item to the payer and vice versa
        payer.addItem(item);
        item.addPayer(payer);

        // if anybody else, tell to recalculate bc they're sharing now
        for (String oldPayer : otherPayers) {
            findPayerDebt(oldPayer).recalculate();
        }
    }

    public void removeLastPayerFromItem(AssignedPrice item) {
        if (item == null) {
            return;
        }
        String payer = item.removePayer();
        removePayerFromItem(payer, item);
    }

    public void removePayerFromItem(String payerName, AssignedPrice item) {
        if (payerName == null || item == null) {
            return;
        }
        PayerDebt payer = findPayerDebt(payerName);
        item.removePayer(payer);
        payer.removeItem(item);

        ArrayList<String> payersLeft = item.getPayers();

        if (payersLeft.isEmpty()) {
            // nobody left to pay for it
            totals.removeItem(item);
        }

        // tell everybody else to recalculate to take over his cost
        for (String oldPayer : payersLeft) {
            PayerDebt opd = findPayerDebt(oldPayer);
            opd.recalculate();
        }
    }

    public PayerDebt findPayerDebt(String payer) {
        for (PayerDebt pd : payerDebtList) {
            if (pd.name.equals(payer)) {
                return pd;
            }
        }
        return null;
    }

    public void changeTipPercent(float tipPercent) {
        if (tipPercent > 1) {
            tipPercent /= 100;
        }
        for (PayerDebt pd : payerDebtList) {
            pd.setTipPercent(tipPercent);
        }
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
