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
        int curWidth, curHeight, curLeft, curTop, accumulatedHeight;

        //get the available size of child view
        final int childrenLeft = this.getPaddingLeft();

        final int childrenRight = this.getMeasuredWidth() - this.getPaddingRight();

        accumulatedHeight = 0; // first row is 1 tall
        curLeft = childrenLeft;
        final int count = getChildCount();
        int childState = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE)
                return;

            // Measure the child.
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);

            curWidth = child.getMeasuredWidth();
            curHeight = child.getMeasuredHeight();

            if (i == 0) {
                accumulatedHeight = curHeight;
            }
            // child is off the end of this row
            if (curLeft + curWidth >= childrenRight) {
                curLeft = childrenLeft; // reset to start
                accumulatedHeight += curHeight; // add another row
            }
            curLeft += curWidth;
            childState = combineMeasuredStates(childState, child.getMeasuredState());
        }

        setMeasuredDimension(resolveSizeAndState(getMeasuredWidth(), widthMeasureSpec, childState),
                resolveSizeAndState(accumulatedHeight, heightMeasureSpec,
                        childState << MEASURED_HEIGHT_STATE_SHIFT));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
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
            View child = getChildAt(i);
            if (child.getVisibility() == GONE)
                return;

            //Get the maximum size of the child
            child.measure(MeasureSpec.makeMeasureSpec(childrenWidth, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(childrenHeight, MeasureSpec.AT_MOST));
            curWidth = child.getMeasuredWidth();
            curHeight = child.getMeasuredHeight();
            // child is off the end of this row
            if (curLeft + curWidth >= childrenRight) {
                curLeft = childrenLeft; // reset to start
                curTop += maxHeight;
                maxHeight = 0;
            }
            //do the layout
            child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight);
            //store the max height
            if (maxHeight < curHeight)
                maxHeight = curHeight;
            curLeft += curWidth;
        }
    }
}