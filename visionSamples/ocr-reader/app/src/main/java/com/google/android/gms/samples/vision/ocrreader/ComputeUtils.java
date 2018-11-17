package com.google.android.gms.samples.vision.ocrreader;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

public class ComputeUtils {
    static final String TAG = "ComputeUtils";
    static float epsilon = 0.00001f;
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

    static ArrayList<PayerDebt> createPayerDebtList(ArrayList<String> payers) {
        ArrayList<PayerDebt> ret = new ArrayList<>();
        for(String p : payers) {
            PayerDebt pd = new PayerDebt(p);
            ret.add(pd);
        }
        return ret;
    }

    static public boolean labelSubtotalTaxAndTotal(ArrayList<AllocatedPrice> prices) {
        if(prices.size() < 1) {
            return false;
        }
        Collections.sort(prices);

        AllocatedPrice totalAllocatedPrice =  prices.get(prices.size()-1);
        totalAllocatedPrice.labelAsTotal();
        float total = totalAllocatedPrice.getPrice(); // assume last item is total
        float calcSubtotal = 0; // from summing items
        float subtotal = 0; //  line that equals sum of other items
        float tax = 0; // line that comes after subtotal; total - subtotal
        boolean foundSubtotal = false;
        boolean foundTax = false;

        for(AllocatedPrice p : prices) {
            float price = p.getPrice();
            if(floatEquals(price, total)) {
                // they are repeating the total
                p.labelAsTotal();
                continue;
            }
            else if(foundSubtotal && floatEquals(price, subtotal)) {
                // they are repeating the subtotal
                p.labelAsSubtotal();
                continue;
            }
            else if(foundTax && floatEquals(price, tax)) {
                // they are repeating the tax
                p.labelAsTax();
                continue;
            }

            if(!foundSubtotal) {
                if (floatEquals(price, calcSubtotal)) {
                    p.labelAsSubtotal();
                    foundSubtotal = true;
                    subtotal = calcSubtotal;
                    continue;
                }
                else {
                    // this line is not the subtotal, add it
                    calcSubtotal += price;
                }
            } else {
                if(floatEquals(calcSubtotal + price, total)) {
                    p.labelAsTax();
                    tax = price;
                    foundTax = true;
                    continue;
                }
            }
        }

        if(foundSubtotal && foundTax && floatEquals(subtotal + tax, total)) {
            taxRate = tax / subtotal;
            return true;
        }

        // reset all the labels back to item
        for(AllocatedPrice p : prices) {
            p.labelAsItem();
        }
        return false;
    }
}
