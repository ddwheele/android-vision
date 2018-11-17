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

import com.google.android.gms.samples.vision.ocrreader.correct.ParcelableOcrGraphic;
import com.google.android.gms.samples.vision.ocrreader.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;
import java.util.List;

/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 */
public class OcrGraphic extends GraphicOverlay.Graphic {
    private final String TAG = "OcrGraphic";
    private int mId;

    private static final int TEXT_COLOR = Color.WHITE;
    private static final int SELECTED_COLOR = Color.GREEN;

    private static Paint sRectPaint;
    private static Paint sTextPaint;
    private static Paint sRectPaintSelected;
    private static Paint sTextPaintSelected;

    private Paint currentRectPaint;
    private Paint currentTextPaint;
    private final TextBlock mText;

    private boolean selected = false;
    private List<AllocatedPrice> myPrices = new ArrayList<>();

    // percentage across the screen where we start looking for prices
    private static float midpoint_scale = 0.5f;

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
        String priceRegex = "-?\\d+(\\.\\d{2})?";
        float midpointx = w * midpoint_scale;
        if( translateX(mText.getBoundingBox().left) > midpointx ) {
            List<? extends Text> textComponents = mText.getComponents();
            for(Text t : textComponents) {
                try {
                    float bottom = translateY(t.getBoundingBox().bottom);
                    String text = t.getValue();
                    String numberString = new String();
                    if(text.matches(priceRegex)) { // it's a number
                        numberString = text;
                    } else {
                        String[] tokens = text.split(" ");
                        for(String s : tokens) {
                            if(s.startsWith("$")) {
                                s = s.substring(1, s.length()-1);
                            }
                            if(s.matches(priceRegex)) {
                                numberString = s;
                                break;
                            }
                        }
                    }

                    float price = Float.valueOf(numberString);
                    myPrices.add(new AllocatedPrice(bottom + offset, price));
                } catch(NumberFormatException e) {
                    // abort
                    return;
                }
            }
            // if we got here, we're good
            // TODO: betting on no mix of prices and non-prices in a block
            currentRectPaint = sRectPaintSelected;
            currentTextPaint = sTextPaintSelected;
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
