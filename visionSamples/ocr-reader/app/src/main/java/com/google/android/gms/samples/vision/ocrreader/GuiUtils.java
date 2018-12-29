package com.google.android.gms.samples.vision.ocrreader;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.ArrayList;

import static android.graphics.Color.rgb;

public class GuiUtils {
    public static final int BACKGROUND = Color.WHITE;

    public static final int COUNTER_START = 1; // to reserve black for the total

    static ArrayList<Integer> colorList;
    static boolean colorListSetup = false;

    public static int MY_GREEN_COLOR = Color.rgb(39, 174, 96);
    public static int MY_RED_COLOR = Color.rgb(231, 76, 60);

    private static void setupColorList() {
        colorList = new ArrayList<>();

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

        colorList.add(Color.BLACK);

    }

    public static int getNumColor(int index) {
        if(!colorListSetup) {
            setupColorList();
            colorListSetup = true;
        }
        return colorList.get(index%(colorList.size()));
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void requestPermissions(Activity activity, int requestCode, String... permissions) {
        ActivityCompat.requestPermissions(activity, permissions,
                requestCode);
    }
}
