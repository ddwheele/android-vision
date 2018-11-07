package com.google.android.gms.samples.vision.ocrreader;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.samples.vision.ocrreader.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.CameraSource;

public class ParcelableOcrGraphic extends GraphicOverlay.Graphic implements Parcelable {
    private int id;
    private float left;
    private float top;
    private float right;
    private float bottom;
    private String text;

    private static final int TEXT_COLOR = Color.MAGENTA;

    private static Paint sRectPaint;
    private static Paint sTextPaint;

    public ParcelableOcrGraphic(OcrGraphic og) {
        super(null);

        RectF rect = new RectF(og.getTextBlock().getBoundingBox());
        left = rect.left;
        top = rect.top;
        right = rect.right;
        bottom = rect.bottom;
        text = og.getTextBlock().getValue();

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
        // Redraw the overlay, as this graphic has been added.
        postInvalidate();
    }

    protected ParcelableOcrGraphic(Parcel in) {
        super(null);
        id = in.readInt();
        left = in.readFloat();
        top = in.readFloat();
        right = in.readFloat();
        bottom = in.readFloat();
        text = in.readString();
    }

    void setGraphicOverlay(GraphicOverlay overlay) {
        mOverlay = overlay;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeFloat(left);
        dest.writeFloat(top);
        dest.writeFloat(right);
        dest.writeFloat(bottom);
        dest.writeString(text);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ParcelableOcrGraphic> CREATOR = new Creator<ParcelableOcrGraphic>() {
        @Override
        public ParcelableOcrGraphic createFromParcel(Parcel in) {
            return new ParcelableOcrGraphic(in);
        }

        @Override
        public ParcelableOcrGraphic[] newArray(int size) {
            return new ParcelableOcrGraphic[size];
        }
    };

    @Override
    public void draw(Canvas canvas) {
        if (text == null) {
            return;
        }

        // Draws the bounding box around the TextBlock.
        RectF rect = new RectF();
        rect.left = translateX(left);
        rect.top = translateY(top);
        rect.right = translateX(right);
        rect.bottom = translateY(bottom);
        canvas.drawRect(rect, sRectPaint);

        // Break the text into multiple lines and draw each one according to its own bounding box.
        // Wha??? may need to recursively break up the TextBlock ... agh.
//        List<? extends Text> textComponents = text.getComponents();
//        for(Text currentText : textComponents) {
//            float left = translateX(currentText.getBoundingBox().left);
//            float bottom = translateY(currentText.getBoundingBox().bottom);
//            canvas.drawText(currentText.getValue(), left, bottom, sTextPaint);
//        }

        // Just draw the one thing for now and see if it works
        float m_left = translateX(left);
        float m_bottom = translateY(bottom);
        canvas.drawText(text, m_left, m_bottom, sTextPaint);
    }

    @Override
    public boolean contains(float x, float y) {
        if (text == null) {
            return false;
        }
        float t_left = translateX(left);
        float t_top = translateY(top);
        float t_right = translateX(right);
        float t_bottom = translateY(bottom);
        return (t_left < x && t_right > x && t_top < y && t_bottom > y);
    }

    public float scaleX(float horizontal) {
        if(mOverlay != null) {
            return horizontal * mOverlay.getWidthScaleFactor();
        }
        else {
            return horizontal;
        }
    }

    /**
     * Adjusts a vertical value of the supplied value from the preview scale to the view scale.
     */
    public float scaleY(float vertical) {
        if(mOverlay != null) {
            return vertical * mOverlay.getHeightScaleFactor();
        } else {
            return vertical;
        }
    }

    /**
     * Adjusts the x coordinate from the preview's coordinate system to the view coordinate
     * system.
     */
    public float translateX(float x) {
        if (mOverlay != null && mOverlay.getFacing()== CameraSource.CAMERA_FACING_FRONT) {
            return mOverlay.getWidth() - scaleX(x);
        } else {
            return scaleX(x);
        }
    }

    public void postInvalidate() {
        if(mOverlay != null) {
            mOverlay.postInvalidate();
        }
    }
}
