package com.google.android.gms.samples.vision.ocrreader;

public interface ThreeColumnProvider {
     String getFirstColumnString();
     String getSecondColumnString();
     String getThirdColumnString();

      int getFirstColumnBackgroundColor();
      int getSecondColumnBackgroundColor();
      int getThirdColumnBackgroundColor();
}
