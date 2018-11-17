package com.google.android.gms.samples.vision.ocrreader.ui.camera;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;


public class RectangleGraphic extends GraphicOverlay.Graphic {
    private final String TAG = "RectangleGraphic";
    private final String text;
    private static Paint sRectPaint;
    private static Paint sTextPaint;
    private final float left, top, right, bottom, textLeft, textBottom;

    public RectangleGraphic(GraphicOverlay overlay, float left, float top, float right, float bottom, String text) {
        super(overlay);

        this.text = text;

        this.textLeft = left;
        this.textBottom = top - 20;

        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;

        if (sRectPaint == null) {
            sRectPaint = new Paint();
            sRectPaint.setColor(Color.WHITE);
            sRectPaint.setAlpha(128);
            sRectPaint.setStyle(Paint.Style.FILL);
            sRectPaint.setStrokeWidth(4.0f);
        }

        if (sTextPaint == null) {
            sTextPaint = new Paint();
            sTextPaint.setColor(Color.WHITE);
            sTextPaint.setTextSize(100.0f);
        }

        // Redraw the overlay, as this graphic has been added.
        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        RectF rect = new RectF();
        rect.left = translateX(left);
        rect.top = translateY(top);
        rect.right = translateX(right);
        rect.bottom = translateY(bottom);
        canvas.drawRect(rect, sRectPaint);

        float tl = translateX(textLeft);
        float tb = translateY(textBottom);
        canvas.drawText(text, tl, tb, sTextPaint);
    }

    @Override
    /**
     * @return False. We are not clickable.
     */
    public boolean contains(float x, float y) {
        return false;
    }
}