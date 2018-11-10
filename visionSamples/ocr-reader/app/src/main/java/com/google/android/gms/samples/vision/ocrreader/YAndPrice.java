package com.google.android.gms.samples.vision.ocrreader;

import android.os.Parcel;
import android.os.Parcelable;

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
 * This class will hold a Y value (distance from top of screen) and dollar
 * value for each price found.  We will want to be able to sort by Y
 * value later to figure out what is subtotal, tax, and total
 */
public class YAndPrice implements Parcelable, Comparable{
    final private float yValue;
    final private float price;
    private Category category;

    public YAndPrice(float yValue, float price) {
        this.yValue = yValue;
        this.price = price;
        category = Category.ITEM; // assume most common category
    }

    protected YAndPrice(Parcel in) {
        yValue = in.readFloat();
        price = in.readFloat();
        category = in.readParcelable(Category.class.getClassLoader());
    }

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

    public static final Creator<YAndPrice> CREATOR = new Creator<YAndPrice>() {
        @Override
        public YAndPrice createFromParcel(Parcel in) {
            return new YAndPrice(in);
        }

        @Override
        public YAndPrice[] newArray(int size) {
            return new YAndPrice[size];
        }
    };

    @Override
    public String toString() {
        if(category != Category.ITEM) {
            return category.toString() + " " + Float.toString(price);
        }
        return "__________" + Float.toString(price);
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof YAndPrice) {
            if (((YAndPrice) o).yValue < yValue) {
                return 1;
            }
            if(((YAndPrice) o).yValue > yValue) {
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
    }
}
