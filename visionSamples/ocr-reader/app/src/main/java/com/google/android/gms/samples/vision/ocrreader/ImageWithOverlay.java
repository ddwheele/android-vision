package com.google.android.gms.samples.vision.ocrreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.samples.vision.ocrreader.ui.camera.GraphicOverlay;

import java.util.List;

public class ImageWithOverlay extends ViewGroup {
    final private String TAG = "ImageWithOverlay";

    private ImageView imageView;
    private GraphicOverlay<ParcelableOcrGraphic> mGraphicOverlay;
    int previewWidth = 240;
    int previewHeight = 320;

    public ImageWithOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setImage(String path) {
        imageView = findViewById(R.id.imageView);
        Bitmap b = BitmapFactory.decodeFile(path);
        imageView.setImageBitmap(b);
        previewWidth = b.getWidth();
        previewHeight = b.getHeight();
    }

    public void setGraphicList(List<ParcelableOcrGraphic> graphicList) {
        mGraphicOverlay = findViewById(R.id.graphicOverlayCorrect);
        for(ParcelableOcrGraphic pog : graphicList) {
            pog.setGraphicOverlay(mGraphicOverlay);
            mGraphicOverlay.add(pog);
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int viewWidth = right - left;
        final int viewHeight = bottom - top;

        int childWidth;
        int childHeight;
        int childXOffset = 0;
        int childYOffset = 0;
        float widthRatio = (float) viewWidth / (float) previewWidth;
        float heightRatio = (float) viewHeight / (float) previewHeight;

        // want to show the whole picture, so take the longer one (scale it smaller)
        if (widthRatio < heightRatio) {
            childWidth = viewWidth;
            childHeight = (int) ((float) previewHeight * widthRatio);
            childYOffset = (childHeight - viewHeight) / 2;
        } else {
            childWidth = (int) ((float) previewWidth * heightRatio);
            childHeight = viewHeight;
            childXOffset = (childWidth - viewWidth) / 2;
        }

        for (int i = 0; i < getChildCount(); ++i) {
            // One dimension will be cropped.  We shift child over or up by this offset and adjust
            // the size to maintain the proper aspect ratio.
            getChildAt(i).layout(
                    -1 * childXOffset, -1 * childYOffset,
                    childWidth - childXOffset, childHeight - childYOffset);
        }


    }
}
