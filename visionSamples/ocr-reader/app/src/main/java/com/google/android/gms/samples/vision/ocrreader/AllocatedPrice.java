package com.google.android.gms.samples.vision.ocrreader;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * This class keeps track of which prices are:
 * - items that contribute to subtotal (including discounts)
 * - tax added to subtotal
 * - total = subtotal + tax
 * TODO: handle if tip is computed
 */
enum Category implements Parcelable
{   ITEM("Item______"), SUBTOTAL("Subtotal__"), TAX("Tax_______"), TOTAL("Total_____");

    private final String label;

    Category(String s) {
        label = s;
    }

    public String toString() {
        return label;
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return Category.values()[in.readInt()];
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ordinal());
    }
}

/**
 * This class holds
 * - a Y value (distance from top of screen)
 * - dollar value for each price found
 * - if it is subtotal, tax, and total
 * - list of who's paying for it
 */
public class AllocatedPrice implements Parcelable, Comparable, ThreeColumnProvider{
    final String TAG = "Allocated Price";
    final private float yValue;
    final private float price;
    private Category category;
    private List<String> payers;

    public AllocatedPrice(float yValue, float price) {
        this.yValue = yValue;
        this.price = price;
        category = Category.ITEM; // assume most common category
        payers = new ArrayList<>();
    }

    protected AllocatedPrice(Parcel in) {
        yValue = in.readFloat();
        price = in.readFloat();
        category = in.readParcelable(Category.class.getClassLoader());
        payers = in.createStringArrayList();
    }

    public static final Creator<AllocatedPrice> CREATOR = new Creator<AllocatedPrice>() {
        @Override
        public AllocatedPrice createFromParcel(Parcel in) {
            return new AllocatedPrice(in);
        }

        @Override
        public AllocatedPrice[] newArray(int size) {
            return new AllocatedPrice[size];
        }
    };

    public float getPrice() {
        return price;
    }

    public void labelAsTotal() {
        category = Category.TOTAL;
    }

    public void labelAsSubtotal() {
        category = Category.SUBTOTAL;
    }

    public void labelAsTax() {
        category = Category.TAX;
    }

    public void addPayer(String payer) {
        if(category.equals(Category.ITEM)) {
            payers.add(payer);
        }
        else {
            Log.e(TAG, "Trying to assign payer to " + category.toString() + "!");
        }
    }

    // for now, just assume that the last payer entered was the mistake
    public String removePayer() {
        return payers.remove(payers.size()-1);
    }

    // for later ...
    public void removePayer(String payer) {
        payers.remove(payer);
    }

    public String getCategoryString() {
        return category.toString();
    }

    public String getPriceString() {
        return Float.toString(price);
    }

    public String getPayerString() {
        if(payers.isEmpty()) {
            return "";
        }
        else if(payers.size() == 1) {
            return payers.get(0);
        }
        else if(payers.size() == 2) {
            return payers.get(0) + ", " + payers.get(1);
        }
        return "Many people";
    }

    public float getPricePerPayer() {
        int splitBy = payers.size();
        return price / (float) splitBy;
    }

    @Override
    public String toString() {
        if(category != Category.ITEM) {
            return category.toString() + " " + Float.toString(price);
        }
        return "__________" + Float.toString(price);
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof AllocatedPrice) {
            if (((AllocatedPrice) o).yValue < yValue) {
                return 1;
            }
            if(((AllocatedPrice) o).yValue > yValue) {
                return -1;
            }
        }
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(yValue);
        dest.writeFloat(price);
        dest.writeParcelable(category, flags);
        dest.writeStringList(payers);
    }

    @Override
    public String getFirstColumnString() {
        return category.toString();
    }

    @Override
    public String getSecondColumnString() {
        return getPriceString();
    }

    @Override
    public String getThirdColumnString() {
        return getPayerString();
    }
}
