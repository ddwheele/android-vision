package com.google.android.gms.samples.vision.ocrreader;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.DecimalFormat;
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
{   ITEM("Item"), SUBTOTAL("Subtotal"), TAX("Tax"), TOTAL("Total");

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
public class AllocatedPrice implements Parcelable, Comparable {
    final String TAG = "Allocated Price";
    final private float yValue;
    final private float price;
    private Category category;
    private ArrayList<String> payers;
    public static final DecimalFormat df2 = new DecimalFormat( "#.00" );

    @Override
    public String toString() {
        return getCategoryString() + ": $" + price + ", "+getPayerString();
    }

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

    public float getYValue() { return yValue; }

    public void labelAsItem() {
        category = Category.ITEM;
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
        // don't add somebody twice
        if(isItem() && !payers.contains(payer)) {
            payers.add(payer);
        }
        else {
            Log.v(TAG, "Trying to assign payer to " + category.toString() + "!");
        }
    }

    // for now, just assume that the last payer entered was the mistake
    public String removePayer() {
        if(payers.size() > 0) {
            return payers.remove(payers.size() - 1);
        }
        return null;
    }

    // for later ...
    public void removePayer(String payer) {
        payers.remove(payer);
    }

    public String getCategoryString() {
        return category.toString();
    }

    public String getPriceString() {
        return df2.format(price);
    }

    public ArrayList<String> getPayers() {
        return payers;
    }

    public String getPayerString() {
        if(isItem()) {
            if(payers.isEmpty()) {
                return "?";
            }
            else {
                StringBuilder sb = new StringBuilder();
                for(String p : payers) {
                    sb.append(p + ", ");
                }
                sb.deleteCharAt(sb.length()-1); // remove last space
                sb.deleteCharAt(sb.length()-1); // remove last comma
                return sb.toString();
            }
        }
        return "-";
    }

    public float getPricePerPayer() {
        if(isItem()) {
            int splitBy = payers.size();
            return price / (float) splitBy;
        }
        return 0;
    }

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

    public String getFirstColumnString() {
        return category.toString();
    }

    public String getSecondColumnString() {
        return getPriceString();
    }

    public String getThirdColumnString() {
        return getPayerString();
    }

    public int getFirstColumnTextColor() {
        if(isItem()) {
            if(payers.isEmpty()) {
                return Color.RED;
            } else {
                return Color.GREEN;
            }
        }
        return Color.WHITE;
    }

//
//    public int getSecondColumnBackgroundColor() {
//
//    }

    public int getSecondColumnTextColor() {
        return getFirstColumnTextColor();
    }


    public int getThirdColumnBackgroundColor() {
//        if(isItem() && payers.isEmpty()) {
//            return Color.BLACK;
//        }
        return ComputeUtils.BACKGROUND;
    }

    boolean isItem() {
        return category == Category.ITEM;
    }
}
