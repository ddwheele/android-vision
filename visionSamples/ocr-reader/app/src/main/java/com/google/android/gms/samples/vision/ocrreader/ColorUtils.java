package com.google.android.gms.samples.vision.ocrreader;

import android.graphics.Color;

import java.util.ArrayList;

import static android.graphics.Color.rgb;

public class ColorUtils {
    public static final int BACKGROUND = Color.WHITE;

    public static final int COUNTER_START = 1; // to reserve black for the total

    static ArrayList<Integer> colorList;
    static boolean colorListSetup = false;

    private static void setupColorList() {
        colorList = new ArrayList<>();

        colorList.add(Color.BLACK);

        colorList.add(rgb(33,97,140)); // dark blue
        colorList.add(rgb(206, 97, 85)); // salmon
        colorList.add(rgb(212, 172, 13)); // dark yellow

        colorList.add(rgb(136, 78, 160)); // red purple
        colorList.add(rgb(25, 111, 61)); // dark green
        colorList.add(rgb(72, 201, 176)); // teal
        colorList.add(rgb(230, 126, 34)); // orange
        colorList.add(rgb(91,44, 111)); // dark purple

        colorList.add(rgb(93,173, 226)); // cyan

        colorList.add(rgb(133, 146, 158)); //gray
    }

    public static int getNumColor(int index) {
        if(!colorListSetup) {
            setupColorList();
            colorListSetup = true;
        }
        return colorList.get(index%(colorList.size()));
    }
}
