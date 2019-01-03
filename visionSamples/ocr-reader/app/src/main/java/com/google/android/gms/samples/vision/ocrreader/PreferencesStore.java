package com.google.android.gms.samples.vision.ocrreader;

import java.io.Serializable;

public class PreferencesStore implements Serializable {
    private static PreferencesStore INSTANCE;
    public static String preferencesFilename = "Preferences.dat";

    protected int lastTipPercent = 15; // x100

    public static PreferencesStore getInstance() {
        if(INSTANCE == null) {
            INSTANCE = (PreferencesStore) Utils.loadData(preferencesFilename);
        }
        if(INSTANCE == null) {
            INSTANCE = new PreferencesStore();
        }
        return INSTANCE;
    }
    public static void save() {
        Utils.saveData(preferencesFilename, INSTANCE);
    }

    private PreferencesStore() {}

    public int getLastTipPercent() {
        return lastTipPercent;
    }

    public void setLastTipPercent(int lastTipPercent) {
        this.lastTipPercent = lastTipPercent;
        save();
    }
}
