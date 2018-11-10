package com.google.android.gms.samples.vision.ocrreader;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

public class ComputeUtils {
    static final String TAG = "ComputeUtils";
    static float epsilon = 0.00001f;

    static boolean floatEquals(float f1, float f2) {
        if(Math.abs(f1 - f2) < epsilon) {
            return true;
        }
        return false;
    }


    static boolean labelSubtotalTaxAndTotal(ArrayList<YAndPrice> prices) {
        Collections.sort(prices);

        YAndPrice totalYAndPrice =  prices.get(prices.size()-1);
        totalYAndPrice.labelAsTotal();
        float total = totalYAndPrice.getPrice(); // assume last item is total
        float calcSubtotal = 0; // from summing items
        float subtotal = 0; //  line that equals sum of other items
        float tax = 0; // line that comes after subtotal; total - subtotal
        boolean foundSubtotal = false;
        boolean foundTax = false;

        for(YAndPrice p : prices) {
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
                    Log.e(TAG, "FOUND SUBTOTAL = " + subtotal);
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
                    Log.e(TAG, "FOUND TAX = " + tax);
                    continue;
                }
            }
        }

        if(foundSubtotal && foundTax && floatEquals(subtotal + tax, total)) {
            Log.e(TAG, "subtotal = " + subtotal);
            Log.e(TAG, "tax = " + tax);
            Log.e(TAG, "total = " + total);
            return true;
        }
        return false;

    }
}
