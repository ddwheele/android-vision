package com.google.android.gms.samples.vision.ocrreader;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PayerDebt implements Parcelable {
    final String name;
    ArrayList<AllocatedPrice> items;
    float subtotal;
    float total;
    boolean calculated = false; // have we calculated what he owes
    boolean selected = false; // is this person selected in the Activity

    DecimalFormat twoDecimalFormat = new DecimalFormat("#.00");

    public PayerDebt(String name, int color) {
        this.name = name;
        items = new ArrayList<>();
    }

    protected PayerDebt(Parcel in) {
        name = in.readString();
        items = in.createTypedArrayList(AllocatedPrice.CREATOR);
        subtotal = in.readFloat();
        total = in.readFloat();
        calculated = in.readByte() != 0;
        selected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeTypedList(items);
        dest.writeFloat(subtotal);
        dest.writeFloat(total);
        dest.writeByte((byte) (calculated ? 1 : 0));
        dest.writeByte((byte) (selected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PayerDebt> CREATOR = new Creator<PayerDebt>() {
        @Override
        public PayerDebt createFromParcel(Parcel in) {
            return new PayerDebt(in);
        }

        @Override
        public PayerDebt[] newArray(int size) {
            return new PayerDebt[size];
        }
    };

    public void addItem(AllocatedPrice ap) {
        if(!items.contains(ap)) {
            items.add(ap);
        }
        calculated = false;
    }

    public void removeItem(AllocatedPrice ap) {
        items.remove(ap);
        calculate();
    }

    public float getSubtotal() {
        if(!calculated) {
           calculate();
        }
        return subtotal;
    }

    /**
     * @return total + tax
     */
    public float getTotal() {
        if(!calculated) {
            calculate();
        }
        return total;
    }

    /**
     * @param tipPercent 0.15 for 15%
     * @return total * tipPercent
     */
    public float getTip(float tipPercent) {
        if(!calculated) {
            calculate();
        }
        return total * tipPercent;
    }

    /**
     * @return subtotal + tax + 15% tip
     */
    public float getTotalAndTip() {
        return getTotal() + getTip(0.15f);
    }

    /**
     * @return subtotal + tax + 15% tip
     */
    public float getTotalAndTip(float tipPercent) {
        return getTotal() + getTip(tipPercent);
    }

    public void recalculate() {
        // for when someone else is sharing an item now
        calculate();
    }

    // add to get subtotal, and add tax for total
    private void calculate() {
        subtotal = 0;
        for(AllocatedPrice ap : items) {
            subtotal += ap.getPricePerPayer();
        }
        total = subtotal * (1 + Utils.taxRate);
        calculated = true;
    }

    public String getFirstColumnString() {
        return name;
    }

    public String getSecondColumnString() {

        Log.e("PayerDebt", "total = " + twoDecimalFormat.format(getTotal()));
        return twoDecimalFormat.format(getTotal());
    }

    public String getThirdColumnString() {
        Log.e("PayerDebt", "Tip = " + twoDecimalFormat.format(getTotalAndTip()));
        return twoDecimalFormat.format(getTotalAndTip());
    }

    public int getThirdColumnBackgroundColor() {
        if(selected) {
            return Color.GREEN;
        }
        return Utils.BACKGROUND;
    }

    public boolean isSelected() {
        return selected;
    }

    public void toggleSelected() {
        if(selected) {
            selected = false;
        }
        else {
            selected = true;
        }
    }
}
