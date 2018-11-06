package com.google.android.gms.samples.vision.ocrreader;

import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableOcrGraphic implements Parcelable {
    private int id;
    private float left;
    private float top;
    private float right;
    private float bottom;
    private String text;

    public ParcelableOcrGraphic(OcrGraphic og) {
        RectF rect = new RectF(og.getTextBlock().getBoundingBox());
        left = rect.left;
        top = rect.top;
        right = rect.right;
        bottom = rect.bottom;
        text = og.getTextBlock().getValue();
    }

    protected ParcelableOcrGraphic(Parcel in) {
        id = in.readInt();
        left = in.readFloat();
        top = in.readFloat();
        right = in.readFloat();
        bottom = in.readFloat();
        text = in.readString();
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
}
