package com.google.android.gms.samples.vision.ocrreader;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

import static android.graphics.Color.rgb;

public class Utils {
    static final String TAG = "Utils";
    static float epsilon = 0.01f;
    static float taxRate = 0.0925f;

    static final String PRICES = "prices";
    static final String PAYERS = "payers";
    static final String OFFSET = "offset";
    static final String PAYER_COORDINATOR = "payer coordinator";

    static final int BACKGROUND = Color.WHITE;

    static ArrayList<Integer> colorList;
    static boolean colorListSetup = false;


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
        int payerCounter = 0;
        for(String p : payers) {
            PayerDebt pd = new PayerDebt(p, getNumColor(payerCounter));
            ret.add(pd);
            payerCounter++;
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
        Log.d(TAG, "============================");
        Log.d(TAG, "TOTAL = " + total);
        float calcSubtotal = 0; // from summing items
        float subtotal = 0; //  line that equals sum of other items
        float tax = 0; // line that comes after subtotal; total - subtotal
        boolean foundSubtotal = false;
        boolean foundTax = false;

        Log.d(TAG, "prices size is " + prices.size());
        for(int i=lastPriceIndex; i>=0; i--) {
            AllocatedPrice p = prices.get(i);
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
                Log.e(TAG, "TAX = " + tax);
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

    private static void resetLabelsToItem(ArrayList<AllocatedPrice> prices) {
        for(AllocatedPrice p : prices) {
            p.labelAsItem();
        }
    }

    protected static void setupColorList() {
        colorList = new ArrayList<>();
        colorList.add(rgb(33,97,140)); // dark blue
        colorList.add(rgb(206, 97, 85)); // salmon
        colorList.add(rgb(212, 172, 13)); // dark yellow

        colorList.add(rgb(136, 78, 160)); // red purple
        colorList.add(rgb(25, 111, 61)); // dark green
        colorList.add(rgb(72, 201, 176)); // teal
        colorList.add(rgb(230, 126, 34)); // orange
        colorList.add(rgb(91,44, 111)); // dark purple

        colorList.add(rgb(93,173, 226)); // cyan

        colorList.add(rgb(133, 146, 158)); //gray

    }

    protected static int getNumColor(int index) {
        if(!colorListSetup) {
            setupColorList();
            colorListSetup = true;
        }
        return colorList.get(index%(colorList.size()));
    }

}
