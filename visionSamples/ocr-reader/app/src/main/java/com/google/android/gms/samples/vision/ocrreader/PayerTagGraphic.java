package com.google.android.gms.samples.vision.ocrreader;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.widget.TextView;

/**
 * Class that keeps tabs on the Payer Tags on the SplitActivity
 * So that they can act like radio buttons
 */
public class PayerTagGraphic {
    final String TAG = "PayerTagGraphic";
        final String name;
        final int color;
        final TextView textView;
        boolean selected = false;

        public PayerTagGraphic(String name, int color, TextView textView) {
            this.name = name;
            this.color = color;
            this.textView = textView;
        }

        public void togglePayerTag() {
            GradientDrawable drawable = (GradientDrawable)textView.getBackground();
            if(selected) {
                Log.e(TAG, "Returning to normal: " + name);
                // change back to normal colors
                textView.setTextColor(Color.WHITE);
                drawable.setColor(color); // set solid color
                drawable.setStroke(1, Color.WHITE);
                selected = false;
            } else {
                Log.e(TAG, "Inverting: " + name);
                // invert the colors
                textView.setTextColor(color);
                drawable.setColor(Color.WHITE);
                drawable.setStroke(3, color);
                selected = true;
            }
        }
    }




