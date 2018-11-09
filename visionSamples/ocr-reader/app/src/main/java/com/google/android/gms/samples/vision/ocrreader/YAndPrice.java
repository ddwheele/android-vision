package com.google.android.gms.samples.vision.ocrreader;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class will hold a Y value (distance from top of screen) and dollar
 * value for each price found.  We will want to be able to sort by Y
 * value later to figure out what is subtotal, tax, and total
 */
public class YAndPrice implements Parcelable, Comparable{
    final float yValue;
    final float price;

    public YAndPrice(float yValue, float price) {
        this.price = price;
        this.yValue = yValue;
    }

    protected YAndPrice(Parcel in) {
        yValue = in.readFloat();
        price = in.readFloat();
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(yValue);
        dest.writeFloat(price);
    }

    @Override
    public String toString() {
        return Float.toString(price);
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
}
