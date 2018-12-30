package com.google.android.gms.samples.vision.ocrreader.calculate;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

public class CalcUtils {
    static final String TAG = "CalcUtils";
    public static float epsilon = 0.01f;
    public static float taxRate = 0.0925f;

    public static boolean floatEquals(float f1, float f2) {
        if(Math.abs(f1 - f2) < epsilon) {
            return true;
        }
        return false;
    }

    /**
     * Remove rows for total, subtotal, and tax
     * @param prices list to clean
     */
    public static ArrayList<AssignedPrice> removeNonItemRows(ArrayList<AssignedPrice> prices) {
        ArrayList<AssignedPrice> newList = new ArrayList<>();
        for(AssignedPrice p : prices) {
            if(p.isItem()) {
                newList.add(p);
            }
        }
        return newList;
    }

    public static boolean labelSubtotalTaxAndTotal(ArrayList<AssignedPrice> prices) {
        if(prices.size() < 1) {
            return false;
        }
        Collections.sort(prices);

        int lastPriceIndex = prices.size()-1;
        AssignedPrice totalAssignedPrice =  prices.get(lastPriceIndex);
        totalAssignedPrice.labelAsTotal();
        float total = totalAssignedPrice.getPrice(); // assume last item is total
        Log.d(TAG, "============================");
        Log.d(TAG, "TOTAL = " + total);
        float calcSubtotal = 0; // from summing items
        float subtotal = 0; //  line that equals sum of other items
        float tax = 0; // line that comes after subtotal; total - subtotal
        boolean foundSubtotal = false;
        boolean foundTax = false;

        Log.d(TAG, "prices size is " + prices.size());
        for(int i=lastPriceIndex; i>=0; i--) {
            AssignedPrice p = prices.get(i);
            Log.d(TAG, "Price " + p);
            float price = p.getPrice();
            if(floatEquals(price, total)) {
                // they are repeating the total
                p.labelAsTotal();
            }
            else if(!foundTax) {
                // tax is always right before total
                tax = price;
                p.labelAsTax();
                foundTax = true;
                Log.d(TAG, "TAX = " + tax);
            }
            // don't think they ever repeat tax, and what if tax happens to match an item price?
//            else if(floatEquals(price, tax)) { // must have found tax already
//                // they are repeating the tax
//                p.labelAsTax();
//            }
            else if(!foundSubtotal) {
                // subtotal is always right before tax
                subtotal = price;
                p.labelAsSubtotal();
                foundSubtotal = true;
                Log.d(TAG, "SUBTOTAL = " + subtotal);
            }
            else if(foundSubtotal && floatEquals(price, subtotal)) {  // must have found subtotal already
                // unless this is the first and only item,
                // in which case it equals the subtotal w/o being a subtotal
                if(i==0) {
                    Log.d(TAG, "normal ITEM = " + price);
                    calcSubtotal += price;
                } else {
                    // they actually are repeating the subtotal
                    p.labelAsSubtotal();
                }
            } else {
                Log.d(TAG, "normal ITEM = " + price);
                calcSubtotal += price;
                Log.d(TAG, "calcSubtotal = " + calcSubtotal);
            }
        }

        if(!floatEquals(subtotal, calcSubtotal)) {
            Log.d(TAG, "ABORT, subtotal != subtotal");
            Log.d(TAG, "============================");
            resetLabelsToItem(prices);
            return false;
        }

        if(!floatEquals(calcSubtotal + tax, total)) {
            Log.d(TAG, "ABORT: subtotal+tax does not equal total");
            Log.d(TAG, "============================");
            resetLabelsToItem(prices);
            return false;
        }

        if(foundSubtotal && foundTax && floatEquals(calcSubtotal + tax, total)) {
            taxRate = tax / subtotal;
            Log.d(TAG, "subtotal+tax equals total, HAPPY ENDING");
            Log.d(TAG, "============================");
            return true;
        }

        // reset all the labels back to item
        Log.d(TAG, "============================");
        return false;
    }

    private static void resetLabelsToItem(ArrayList<AssignedPrice> prices) {
        for(AssignedPrice p : prices) {
            p.labelAsItem();
        }
    }
}
