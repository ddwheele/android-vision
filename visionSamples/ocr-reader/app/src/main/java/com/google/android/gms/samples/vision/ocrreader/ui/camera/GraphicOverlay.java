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
package com.google.android.gms.samples.vision.ocrreader.ui.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.gms.samples.vision.ocrreader.calculate.Utils;
import com.google.android.gms.samples.vision.ocrreader.ocr.OcrGraphic;
import com.google.android.gms.samples.vision.ocrreader.calculate.AllocatedPrice;
import com.google.android.gms.vision.CameraSource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * A view which renders a series of custom graphics to be overlaid on top of an associated preview
 * (i.e., the camera preview).  The creator can add graphics objects, update the objects, and remove
 * them, triggering the appropriate drawing and invalidation within the view.<p>
 *
 * Supports scaling and mirroring of the graphics relative the camera's preview properties.  The
 * idea is that detection items are expressed in terms of a preview size, but need to be scaled up
 * to the full view size, and also mirrored in the case of the front-facing camera.<p>
 *
 * Associated {@link Graphic} items should use the following methods to convert to view coordinates
 * for the graphics that are drawn:
 * <ol>
 * <li>{@link Graphic#scaleX(float)} and {@link Graphic#scaleY(float)} adjust the size of the
 * supplied value from the preview scale to the view scale.</li>
 * <li>{@link Graphic#translateX(float)} and {@link Graphic#translateY(float)} adjust the coordinate
 * from the preview's coordinate system to the view coordinate system.</li>
 * </ol>
 */
public class GraphicOverlay<T extends GraphicOverlay.Graphic> extends View {
    private final String TAG = "Graphic Overlay";
    private final Object mLock = new Object();
    private int mPreviewWidth;
    private float mWidthScaleFactor = 1.0f;
    private int mPreviewHeight;
    private float mHeightScaleFactor = 1.0f;
    private int mFacing = CameraSource.CAMERA_FACING_BACK;
    private Set<T> mGraphics = new HashSet<>();
    private Set<RectangleGraphic> otherGraphics = new HashSet<>();
    private float width = 3000; // width of the image that this is on (to find prices)
    private float yOffset = 0; // if this is the second page or more, offset the y value
    private ArrayList<AllocatedPrice> previousPriceList; // from previous pics of receipt
    private ArrayList<AllocatedPrice> precomputedPriceList = new ArrayList<>();

    public GraphicOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        synchronized (mLock) {
            otherGraphics.add(new RectangleGraphic(this,
                    0.33f, 250, 0.66f, 1100,
                    "Prices"));
        }
    }

    /**
     * Checks if selected text makes well-formed receipt
     * @return true if currently selected text has items that add to subtotal and total
     */
    public boolean isConsistent() {
        precomputedPriceList.clear();

        if(previousPriceList != null) {
            precomputedPriceList.addAll(previousPriceList);
        }
        precomputedPriceList.addAll(flattenPriceList());
        return Utils.labelSubtotalTaxAndTotal(precomputedPriceList);
    }

    /**
     * @return all prices in mGraphics in a flat list instead of hierarchy
     */
    private ArrayList<AllocatedPrice> flattenPriceList() {
        ArrayList<AllocatedPrice> ret = new ArrayList<>();
        for(T g : mGraphics) {
            ret.addAll(((OcrGraphic)g).getMyPrices());
        }
        return ret;
    }

    public ArrayList<AllocatedPrice> getPriceList() {
        return precomputedPriceList;
    }

    /**
     * If this is not the first page of the receipt, offset to add to
     * y values from this page so the items will still be in order when
     * sorted by y value.
     * TODO this really should be the height of the screen
     * @param offset lowest y value of the prices on the preceeding page
     */
    public void setYOffset(float offset) {
        yOffset = offset;
    }

    public void setPreviousPriceList(ArrayList<AllocatedPrice> oldList) {
        previousPriceList = oldList;
    }

    public float getWidthScaleFactor() { return mWidthScaleFactor; }

    public float getHeightScaleFactor() { return mHeightScaleFactor; }

    public int getFacing() { return mFacing; }

    /**
     * Base class for a custom graphics object to be rendered within the graphic overlay.  Subclass
     * this and implement the {@link Graphic#draw(Canvas)} method to define the
     * graphics element.  Add instances to the overlay using {@link GraphicOverlay#add(Graphic)}.
     */
    public static abstract class Graphic {
        private final String TAG = "Graphic";
        protected GraphicOverlay mOverlay;



        public Graphic(GraphicOverlay overlay) {
            mOverlay = overlay;
        }

        /**
         * Draw the graphic on the supplied canvas.  Drawing should use the following methods to
         * convert to view coordinates for the graphics that are drawn:
         * <ol>
         * <li>{@link Graphic#scaleX(float)} and {@link Graphic#scaleY(float)} adjust the size of
         * the supplied value from the preview scale to the view scale.</li>
         * <li>{@link Graphic#translateX(float)} and {@link Graphic#translateY(float)} adjust the
         * coordinate from the preview's coordinate system to the view coordinate system.</li>
         * </ol>
         *
         * @param canvas drawing canvas
         */
        public abstract void draw(Canvas canvas);

        /**
         * Returns true if the supplied coordinates are within this graphic.
         */
        public abstract boolean contains(float x, float y);

        /**
         * Adjusts a horizontal value of the supplied value from the preview scale to the view
         * scale.
         */
        public float scaleX(float horizontal) {
            return horizontal * mOverlay.mWidthScaleFactor;
        }

        /**
         * Adjusts a vertical value of the supplied value from the preview scale to the view scale.
         */
        public float scaleY(float vertical) {
            return vertical * mOverlay.mHeightScaleFactor;
        }

        /**
         * Adjusts the x coordinate from the preview's coordinate system to the view coordinate
         * system.
         */
        public float translateX(float x) {
            if (mOverlay.mFacing == CameraSource.CAMERA_FACING_FRONT) {
                return mOverlay.getWidth() - scaleX(x);
            } else {
                return scaleX(x);
            }
        }

        /**
         * Adjusts the y coordinate from the preview's coordinate system to the view coordinate
         * system.
         */
        public float translateY(float y) {
            return scaleY(y);
        }

        public void postInvalidate() {
            mOverlay.postInvalidate();
        }
    }

    /**
     * Removes all graphics from the overlay.
     */
    public void clear() {
        synchronized (mLock) {
            mGraphics.clear();
        }
        postInvalidate();
    }

    /**
     * Adds a graphic to the overlay.
     */
    public void add(T graphic) {
        synchronized (mLock) {
            if(graphic instanceof OcrGraphic) {
                ((OcrGraphic)graphic).calculatePriceList(width, yOffset);
            }
            mGraphics.add(graphic);
        }
        postInvalidate();
    }

    /**
     * Removes a graphic from the overlay.
     */
    public void remove(T graphic) {
        synchronized (mLock) {
            mGraphics.remove(graphic);
        }
        postInvalidate();
    }

    public void setWidth(float w) {
        width = w;
    }

    /**
     * Returns the first graphic, if any, that exists at the provided absolute screen coordinates.
     * These coordinates will be offset by the relative screen position of this view.
     * @return First graphic containing the point, or null if no text is detected.
     */
    public T getGraphicAtLocation(float rawX, float rawY) {
        synchronized (mLock) {
            // Get the position of this View so the raw location can be offset relative to the view.
            int[] location = new int[2];
            this.getLocationOnScreen(location);
            for (T graphic : mGraphics) {
                if (graphic.contains(rawX - location[0], rawY - location[1])) {
                    return graphic;
                }
            }
            return null;
        }
    }

    /**
     * Sets the camera attributes for size and facing direction, which informs how to transform
     * image coordinates later.
     */
    public void setCameraInfo(int previewWidth, int previewHeight, int facing) {
        synchronized (mLock) {
            mPreviewWidth = previewWidth;
            mPreviewHeight = previewHeight;
            mFacing = facing;
        }
        postInvalidate();
    }

    /**
     * Draws the overlay with its associated graphic objects.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        synchronized (mLock) {
            if ((mPreviewWidth != 0) && (mPreviewHeight != 0)) {
                mWidthScaleFactor = (float) canvas.getWidth() / (float) mPreviewWidth;
                mHeightScaleFactor = (float) canvas.getHeight() / (float) mPreviewHeight;
            }

            for (Graphic graphic : mGraphics) {
                graphic.draw(canvas);
            }

            for (Graphic graphic : otherGraphics) {
                if(graphic instanceof RectangleGraphic) {
                    ((RectangleGraphic) graphic).setCanvasWidth((float)canvas.getWidth());
                }
                graphic.draw(canvas);
            }
        }
    }
}
