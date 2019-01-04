package pocopson.penny.fairsplit.ocr;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;

import pocopson.penny.fairsplit.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ParcelableOcrGraphic extends GraphicOverlay.Graphic implements Parcelable {
    private int id;
    private float left;
    private float top;
    private float right;
    private float bottom;
    private List<TextCoord> texts;

    private static final int TEXT_COLOR = Color.WHITE;
    private static final int SELECTED_COLOR = Color.GREEN;

    private static Paint sRectPaint;
    private static Paint sTextPaint;

    private static Paint sRectPaintSelected;
    private static Paint sTextPaintSelected;

    private Paint currentRectPaint = sRectPaint;
    private Paint currentTextPaint = sTextPaint;

    private boolean selected = false;

    public static class TextCoord implements Parcelable {
        String text;
        float left;
        float bottom;

        public TextCoord(String text, float left, float bottom) {
            this.text = text;
            this.left = left;
            this.bottom = bottom;
        }

        protected TextCoord(Parcel in) {
            text = in.readString();
            left = in.readFloat();
            bottom = in.readFloat();
        }

        public static final Creator<TextCoord> CREATOR = new Creator<TextCoord>() {
            @Override
            public TextCoord createFromParcel(Parcel in) {
                return new TextCoord(in);
            }

            @Override
            public TextCoord[] newArray(int size) {
                return new TextCoord[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(text);
            dest.writeFloat(left);
            dest.writeFloat(bottom);
        }
    }

    public ParcelableOcrGraphic(OcrGraphic og) {
        super(null);

        RectF rect = new RectF(og.getTextBlock().getBoundingBox());
        left = rect.left;
        top = rect.top;
        right = rect.right;
        bottom = rect.bottom;

        List<? extends Text> textComponents = og.getTextBlock().getComponents();
        texts = new ArrayList<>();
        for(Text currentText : textComponents) {
            float left = currentText.getBoundingBox().left;
            float bottom = currentText.getBoundingBox().bottom;
            texts.add(new TextCoord(currentText.getValue(), left, bottom));
        }

        if (sRectPaint == null) {
            sRectPaint = new Paint();
            sRectPaint.setColor(TEXT_COLOR);
            sRectPaint.setStyle(Paint.Style.STROKE);
            sRectPaint.setStrokeWidth(4.0f);
        }

        if (sTextPaint == null) {
            sTextPaint = new Paint();
            sTextPaint.setColor(TEXT_COLOR);
            sTextPaint.setTextSize(30.0f);
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
            sTextPaintSelected.setTextSize(30.0f);
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
        texts = new ArrayList<>();
        in.readTypedList(texts, TextCoord.CREATOR);
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
        dest.writeTypedList(texts);
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
        if (texts == null) {
            return;
        }

        // Draws the bounding box around the TextBlock.
        RectF rect = new RectF();
        rect.left = translateX(left);
        rect.top = translateY(top);
        rect.right = translateX(right);
        rect.bottom = translateY(bottom);
        canvas.drawRect(rect, currentRectPaint);

        for(TextCoord t : texts) {
            float l = translateX(t.left);
            float b = translateY(t.bottom);
            canvas.drawText(t.text, l, b, currentTextPaint);
        }
    }

    /**
     * tries to return all the numbers in this block of text
     * Maybe later could enforce that it adds only numbers with 2 decimal places
     * Also could try to split the item name out from price if they are on same line
     * @return
     */
    public List<Float> getNumbers() {
        List<Float> ret = new ArrayList<>();
        for(TextCoord t: texts) {
            float x = Float.parseFloat(t.text);
            ret.add(x);
        }
        return ret;
    }

    @Override
    public boolean contains(float x, float y) {
        if (texts == null) {
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

    public boolean isSelected() {
        return selected;
    }

    public void toggleSelected() {
        if(selected) {
            selected = false;
            currentRectPaint = sRectPaint;
            currentTextPaint = sTextPaint;
        } else {
            selected = true;
            currentRectPaint = sRectPaintSelected;
            currentTextPaint = sTextPaintSelected;
        }
    }

}
