package com.google.android.gms.samples.vision.ocrreader;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class TagLayout extends LinearLayout {
    private final String TAG = "TagLayout";

    AssignPayersActivity visitor = null;

    public TagLayout(Context context) {
        super(context);
    }

    public TagLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAssignPayersActivity(AssignPayersActivity v) {
        visitor = v;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,
                             int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        boolean resizeWidth = widthSpecMode != MeasureSpec.EXACTLY;
        boolean resizeHeight = heightSpecMode != MeasureSpec.EXACTLY;

        Log.e(TAG, "M allowed W = " + resizeWidth + ", allowed H = " + resizeHeight);
        int curWidth, curHeight, curLeft, curTop, accumulatedHeight;

        Log.e(TAG, "onMeasure(), widthMS=" + widthMeasureSpec + ", heightMS=" + heightMeasureSpec);

        //get the available size of child view
        final int childrenLeft = this.getPaddingLeft();
        final int childrenTop = this.getPaddingTop();

        final int childrenRight = this.getMeasuredWidth() - this.getPaddingRight();
        final int childrenBottom = this.getMeasuredHeight() - this.getPaddingBottom();

        final int childrenWidth = childrenRight - childrenLeft;
        final int childrenHeight = childrenBottom - childrenTop;

        Log.e(TAG, "onMeasure(), childrenWidth=" + childrenWidth
                + ", childrenHeight=" + childrenHeight);

        accumulatedHeight = 0; // first row is 1 tall
        curLeft = childrenLeft;
        curTop = childrenTop;
        final int count = getChildCount();
        Log.e(TAG, "onMeasure(), with " + count + " children");
        int childState = 0;
        for (int i = 0; i < count; i++) {
            Log.e(TAG, "Processing child " + i);

            View child = getChildAt(i);
            if (child.getVisibility() == GONE)
                return;

            // Measure the child.
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);

//            child.measure(MeasureSpec.makeMeasureSpec(childrenWidth, MeasureSpec.AT_MOST),
//                    MeasureSpec.makeMeasureSpec(childrenHeight, MeasureSpec.AT_MOST));
            curWidth = child.getMeasuredWidth();
            curHeight = child.getMeasuredHeight();
            Log.e(TAG, "onMeasure(), curWidth=" + curWidth
                    + ", curHeight=" + curHeight);

            if (i == 0) {
                accumulatedHeight = curHeight;
            }
            // child is off the end of this row
            if (curLeft + curWidth >= childrenRight) {
                curLeft = childrenLeft; // reset to start
                //curTop += maxHeight;
                accumulatedHeight += curHeight; // add another row
                Log.e(TAG, "child " + i + " added another row, height = " + accumulatedHeight);
            }
            curLeft += curWidth;
            childState = combineMeasuredStates(childState, child.getMeasuredState());
        }

//        if (accumulatedHeight < minHeight) {
//            accumulatedHeight = minHeight;
//        }

        Log.e(TAG, "Trying to set measuredDimensions to w=" + getMeasuredWidth() + ", h=" + accumulatedHeight);
        //setMeasuredDimension(getMeasuredWidth(), accumulatedHeight);

        setMeasuredDimension(resolveSizeAndState(getMeasuredWidth(), widthMeasureSpec, childState),
                resolveSizeAndState(accumulatedHeight, heightMeasureSpec,
                        childState << MEASURED_HEIGHT_STATE_SHIFT));
        invalidate();
        Log.e(TAG, "&&&&&&& Done measuring");
        if (visitor != null) {
            visitor.onResume();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e(TAG, "REDRAWING ^^^^^^^^^^^^^^^^^ w="
                + getMeasuredWidth() + ", h=" + getMeasuredHeight());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e(TAG, "L onLayout(), l=" + l + ", t=" + t + ", r=" + r + ", b=" + b);
        final int count = getChildCount();
        int curWidth, curHeight, curLeft, curTop, maxHeight;

        //get the available size of child view
        final int childrenLeft = this.getPaddingLeft();
        final int childrenTop = this.getPaddingTop();

        final int childrenRight = this.getMeasuredWidth() - this.getPaddingRight();
        final int childrenBottom = this.getMeasuredHeight() - this.getPaddingBottom();

        final int childrenWidth = childrenRight - childrenLeft;
        final int childrenHeight = childrenBottom - childrenTop;

        maxHeight = 0;
        curLeft = childrenLeft;
        curTop = childrenTop;
        for (int i = 0; i < count; i++) {
            Log.e(TAG, "L Processing child " + i);
            View child = getChildAt(i);
            if (child.getVisibility() == GONE)
                return;

            //Get the maximum size of the child
            child.measure(MeasureSpec.makeMeasureSpec(childrenWidth, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(childrenHeight, MeasureSpec.AT_MOST));
            curWidth = child.getMeasuredWidth();
            curHeight = child.getMeasuredHeight();
            Log.e(TAG, "L curWidth=" + curWidth
                    + ", curHeight=" + curHeight);
            // child is off the end of this row
            if (curLeft + curWidth >= childrenRight) {
                curLeft = childrenLeft; // reset to start
                curTop += maxHeight;
                Log.e(TAG, "L child " + i + " added another row, maxHeight = " + maxHeight);
                maxHeight = 0;
                Log.e(TAG, "L child " + i + " added another row, curTop = " + curTop);
            }
            //do the layout
            child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight);
            Log.e(TAG, "L Child layout = l=" + curLeft
                    + ", t=" + curTop
                    + ", r=" + curLeft + curWidth
                    + ", b=" + curTop + curHeight);
            //store the max height
            if (maxHeight < curHeight)
                maxHeight = curHeight;
            curLeft += curWidth;
        }
    }
}