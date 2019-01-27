package pocopson.penny.easyfairsplit.ui.camera;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;


public class RectangleGraphic extends GraphicOverlay.Graphic {
    private final String TAG = "RectangleGraphic";
    private final String text;
    private final int COLOR = Color.WHITE;
    private static Paint sRectPaint;
    private static Paint sTextPaint;
    private final float leftPercent, top, rightPercent, bottom, textLeft, textBottom;
    private float canvasWidth = 1;

    public RectangleGraphic(GraphicOverlay overlay, float leftPercent, float top, float rightPercent, float bottom, String text) {
        super(overlay);

        this.text = text;

        this.textLeft = leftPercent;
        this.textBottom = top - 20;

        this.leftPercent = leftPercent;
        this.top = top;
        this.rightPercent = rightPercent;
        this.bottom = bottom;

        if (sRectPaint == null) {
            sRectPaint = new Paint();
            sRectPaint.setColor(COLOR);
            sRectPaint.setAlpha(64);
            sRectPaint.setStyle(Paint.Style.FILL);
            sRectPaint.setStrokeWidth(4.0f);
        }

        if (sTextPaint == null) {
            sTextPaint = new Paint();
            sTextPaint.setColor(COLOR);
            sTextPaint.setTextSize(100.0f);
        }

        // Redraw the overlay, as this graphic has been added.
        postInvalidate();
    }

    public void setCanvasWidth(float w) {
        canvasWidth = w;
    }


    @Override
    public void draw(Canvas canvas) {
        RectF rect = new RectF();
        rect.left = leftPercent * canvasWidth;
        rect.top = translateY(top);
        rect.right = rightPercent * canvasWidth;
        rect.bottom = translateY(bottom);
        canvas.drawRect(rect, sRectPaint);

        float tl = rect.left;
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