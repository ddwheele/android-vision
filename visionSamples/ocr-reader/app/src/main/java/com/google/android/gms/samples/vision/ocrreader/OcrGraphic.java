/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.samples.vision.ocrreader;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.google.android.gms.samples.vision.ocrreader.correct.ParcelableOcrGraphic;
import com.google.android.gms.samples.vision.ocrreader.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 */
public class OcrGraphic extends GraphicOverlay.Graphic {
    private final String TAG = "OcrGraphic";
    private int mId;

    private static final int TEXT_COLOR = Color.WHITE;
    private static final int SELECTED_COLOR = Color.GREEN;
    private static final int OUT_OF_RANGE_COLOR = Color.BLACK;

    private static Paint sRectPaint;
    private static Paint sTextPaint;
    private static Paint sRectPaintSelected;
    private static Paint sTextPaintSelected;
    private static Paint sRectPaintOutOfRange;
    private static Paint sTextPaintOutOfRange;

    private Paint currentRectPaint;
    private Paint currentTextPaint;
    private final TextBlock mText;

    private boolean selected = false;
    private List<AllocatedPrice> myPrices = new ArrayList<>();

    // percentage across the screen where we start looking for prices
    private static float midpoint_scale_left = 0f;
    private static float midpoint_scale_right = 1f;

    OcrGraphic(GraphicOverlay overlay, TextBlock text) {
        super(overlay);

        mText = text;

        if (sRectPaint == null) {
            sRectPaint = new Paint();
            sRectPaint.setColor(TEXT_COLOR);
            sRectPaint.setStyle(Paint.Style.STROKE);
            sRectPaint.setStrokeWidth(4.0f);
        }

        if (sTextPaint == null) {
            sTextPaint = new Paint();
            sTextPaint.setColor(TEXT_COLOR);
            sTextPaint.setTextSize(54.0f);
        }

        if (sRectPaintSelected == null) {
            sRectPaintSelected = new Paint();
            sRectPaintSelected.setColor(SELECTED_COLOR);
            sRectPaintSelected.setStyle(Paint.Style.STROKE);
            sRectPaintSelected.setStrokeWidth(4.0f);
        }

        if (sTextPaintSelected == null) {
            sTextPaintSelected = new Paint();
            sTextPaintSelected.setColor(SELECTED_COLOR);
            sTextPaintSelected.setTextSize(54.0f);
        }


        if (sRectPaintOutOfRange == null) {
            sRectPaintOutOfRange = new Paint();
            sRectPaintOutOfRange.setColor(OUT_OF_RANGE_COLOR);
            sRectPaintOutOfRange.setStyle(Paint.Style.STROKE);
            sRectPaintOutOfRange.setStrokeWidth(4.0f);
        }

        if (sTextPaintOutOfRange == null) {
            sTextPaintOutOfRange = new Paint();
            sTextPaintOutOfRange.setColor(OUT_OF_RANGE_COLOR);
            sTextPaintOutOfRange.setTextSize(54.0f);
        }

        currentRectPaint = sRectPaint;
        currentTextPaint = sTextPaint;

        // Redraw the overlay, as this graphic has been added.
        postInvalidate();
    }

    /**
     *
     * @return List of AllocatedPrices
     */
    public List<AllocatedPrice> getMyPrices() {
        return myPrices;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public TextBlock getTextBlock() {
        return mText;
    }

    public ParcelableOcrGraphic getParcelable() {
        return new ParcelableOcrGraphic(this);
    }

    /**
     * Checks whether a point is within the bounding box of this graphic.
     * The provided point should be relative to this graphic's containing overlay.
     * @param x An x parameter in the relative context of the canvas.
     * @param y A y parameter in the relative context of the canvas.
     * @return True if the provided point is contained within this graphic's bounding box.
     */
    public boolean contains(float x, float y) {
        TextBlock text = mText;
        if (text == null) {
            return false;
        }
        RectF rect = new RectF(text.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);
        return (rect.left < x && rect.right > x && rect.top < y && rect.bottom > y);
    }

    /**
     * Tell graphic the width of the canvas it is drawn on
     * If graphic is right of the width*midpoint_scale, it will
     * "select" itself and the app will include it in the list of prices
     * @param w width of the image this graphic is overlaid on
     */
    public void calculatePriceList(float w, float offset) {
        String priceRegex = ".*?(?<neg>-?)[\\$|S]?(?<dollars>\\d*)[\\.| |,](?<cents>\\d{2}).*";
        // Create a Pattern object
        Pattern r = Pattern.compile(priceRegex);

        float midLeft = w * midpoint_scale_left;
        float midRight = w * midpoint_scale_right;
        float textLeft = translateX(mText.getBoundingBox().left);
        float textRight = translateX(mText.getBoundingBox().right);
        if( midLeft < textLeft && textRight < midRight ) {
            List<? extends Text> textComponents = mText.getComponents();
            for(Text t : textComponents) {
                try {
                    float bottom = translateY(t.getBoundingBox().bottom);
                    String text = t.getValue();
                    //String numberString = new String();
                    // Now create matcher object.
                    Matcher m = r.matcher(text);

                    if(m.find()) {

                        String d = m.group("dollars");
                        String c = m.group("cents");
                        String numberString = d + "." + c;
                        Log.e(TAG, "MATCHED " + numberString);
                        float price = Float.valueOf(numberString);
                        if("-".equals(m.group("neg"))) {
                            price = -price;
                        }
                        myPrices.add(new AllocatedPrice(bottom + offset, price));
                        Log.e(TAG, "Price " + price + " added SUCCESSFULLY");
                        // if we got here, we're good
                        // TODO: betting on no mix of prices and non-prices in a block
                        currentRectPaint = sRectPaintSelected;
                        currentTextPaint = sTextPaintSelected;
                    } else {
                        Log.e(TAG, text + " did not match regex");
                    }

                } catch(NumberFormatException e) {
                    Log.e(TAG, e.getMessage());
                    // abort
                    return;
                }
            }

        } else {
            currentRectPaint = sRectPaintOutOfRange;
            currentTextPaint = sTextPaintOutOfRange;
        }
    }

    /**
     * Draws the text block annotations for position, size, and raw value on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        TextBlock text = mText;
        if (text == null) {
            return;
        }

//        // don't draw non-prices, they are just clutter
//        if(currentRectPaint != sRectPaintSelected) {
//            return;
//        }

        // Draws the bounding box around the TextBlock.
        RectF rect = new RectF(text.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);

        canvas.drawRect(rect, currentRectPaint);

        // Break the text into multiple lines and draw each one according to its own bounding box.
        List<? extends Text> textComponents = text.getComponents();
        for(Text currentText : textComponents) {
            float left = translateX(currentText.getBoundingBox().left);
            float bottom = translateY(currentText.getBoundingBox().bottom);
            canvas.drawText(currentText.getValue(), left, bottom, currentTextPaint);
        }
    }
}
