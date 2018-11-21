package com.google.android.gms.samples.vision.ocrreader;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

public class ComputeUtils {
    static final String TAG = "ComputeUtils";
    static float epsilon = 0.01f;
    static float taxRate = 0.0925f;

    static final String PRICES = "prices";
    static final String PAYERS = "payers";
    static final String OFFSET = "offset";

    static final int BACKGROUND = Color.rgb(48, 48, 48);

    static public boolean floatEquals(float f1, float f2) {
        if(Math.abs(f1 - f2) < epsilon) {
            return true;
        }
        return false;
    }

    /**
     * Also adds a row for totals
     * @param payers
     * @return list of PayerDebts ready for assignment
     */
    static ArrayList<PayerDebt> createPayerDebtList(ArrayList<String> payers) {
        ArrayList<PayerDebt> ret = new ArrayList<>();
        for(String p : payers) {
            PayerDebt pd = new PayerDebt(p);
            ret.add(pd);
        }
        ret.add(new PayerDebtTotals("Total"));
        return ret;
    }

    static public boolean labelSubtotalTaxAndTotal(ArrayList<AllocatedPrice> prices) {
        if(prices.size() < 1) {
            return false;
        }
        Collections.sort(prices);

        int lastPriceIndex = prices.size()-1;
        AllocatedPrice totalAllocatedPrice =  prices.get(lastPriceIndex);
        totalAllocatedPrice.labelAsTotal();
        float total = totalAllocatedPrice.getPrice(); // assume last item is total
        Log.e(TAG, "============================");
        Log.e(TAG, "TOTAL = " + total);
        float calcSubtotal = 0; // from summing items
        float subtotal = 0; //  line that equals sum of other items
        float tax = 0; // line that comes after subtotal; total - subtotal
        boolean foundSubtotal = false;
        boolean foundTax = false;

        Log.e(TAG, "prices size is " + prices.size());
        for(int i=lastPriceIndex; i>=0; i--) {
            AllocatedPrice p = prices.get(i);
            Log.e(TAG, "First price " + p);
            float price = p.getPrice();
            if(floatEquals(price, total)) {
                // they are repeating the total
                p.labelAsTotal();
//                continue;
            }
            else if(!foundTax) {
                // tax is always right before total
                tax = price;
                p.labelAsTax();
                foundTax = true;
                Log.e(TAG, "TAX = " + tax);
//                continue;
            }
            else if(floatEquals(price, tax)) { // must have found tax already
                // they are repeating the tax
                p.labelAsTax();
//                continue;
            }
            else if(!foundSubtotal) {
                // subtotal is always right before tax
                subtotal = price;
                p.labelAsSubtotal();
                foundSubtotal = true;
                Log.e(TAG, "SUBTOTAL = " + subtotal);
            }
            else if(foundSubtotal && floatEquals(price, subtotal)) {  // must have found subtotal already
                // they are repeating the subtotal
                p.labelAsSubtotal();
//                continue;
            } else {
                Log.e(TAG, "normal ITEM = " + price);
                calcSubtotal += price;
                Log.e(TAG, "calcSubtotal = " + calcSubtotal);

            }
        }

        if(!floatEquals(subtotal, calcSubtotal)) {
            Log.e(TAG, "ABORT, subtotal != subtotal");
            Log.e(TAG, "============================");
            return false;
        }

        if(!floatEquals(calcSubtotal + tax, total)) {
            Log.e(TAG, "ABORT: subtotal+tax does not equal total");
            Log.e(TAG, "============================");
            return false;
        }

        if(foundSubtotal && foundTax && floatEquals(calcSubtotal + tax, total)) {
            taxRate = tax / subtotal;
            Log.e(TAG, "subtotal+tax equals total, HAPPY ENDING");
            Log.e(TAG, "============================");
            return true;
        }

        // reset all the labels back to item
        for(AllocatedPrice p : prices) {
            p.labelAsItem();
        }
        Log.e(TAG, "============================");
        return false;
    }
}
