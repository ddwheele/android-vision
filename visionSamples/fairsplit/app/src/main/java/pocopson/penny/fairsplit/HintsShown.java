package pocopson.penny.fairsplit;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Remembers which hints have been shown to user so we don't keep bothering them
 */
public class HintsShown implements Parcelable, Serializable {
    private static HintsShown INSTANCE;

    public static String hintFilename = "ShownHints.dat";

    protected boolean ocrCaptureToast = false;
    protected boolean verifyPricesToast = false;
    protected boolean selectPayersToast = false;
    protected boolean assignPayersToast = false;
    protected boolean displayPayersToast = false;

    public static HintsShown getInstance() {
        if(INSTANCE == null) {
            INSTANCE = (HintsShown) Utils.loadData(hintFilename);
        }
        if(INSTANCE == null) {
            INSTANCE = new HintsShown();
        }
        return INSTANCE;
    }

    public static void save() {
        Utils.saveData(hintFilename, INSTANCE);
    }

    private HintsShown() {}

    private HintsShown(Parcel in) {
        ocrCaptureToast = in.readByte() != 0;
        verifyPricesToast = in.readByte() != 0;
        selectPayersToast = in.readByte() != 0;
        assignPayersToast = in.readByte() != 0;
        displayPayersToast = in.readByte() != 0;
    }

    public static final Creator<HintsShown> CREATOR = new Creator<HintsShown>() {
        @Override
        public HintsShown createFromParcel(Parcel in) {
            return new HintsShown(in);
        }

        @Override
        public HintsShown[] newArray(int size) {
            return new HintsShown[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (ocrCaptureToast ? 1 : 0));
        dest.writeByte((byte) (verifyPricesToast ? 1 : 0));
        dest.writeByte((byte) (selectPayersToast ? 1 : 0));
        dest.writeByte((byte) (assignPayersToast ? 1 : 0));
        dest.writeByte((byte) (displayPayersToast ? 1 : 0));
    }

    public static void setOcrCaptureToast(boolean ocrCaptureToast) {
        getInstance().ocrCaptureToast = ocrCaptureToast;
        save();
    }

    public static void setVerifyPricesToast(boolean verifyPricesToast) {
        getInstance().verifyPricesToast = verifyPricesToast;
        save();
    }

    public static void setSelectPayersToast(boolean selectPayersToast) {
        getInstance().selectPayersToast = selectPayersToast;
        save();
    }

    public static void setAssignPayersToast(boolean assignPayersToast) {
        getInstance().assignPayersToast = assignPayersToast;
        save();
    }

    public static void setDisplayPayersToast(boolean displayPayersToast) {
        getInstance().displayPayersToast = displayPayersToast;
        save();
    }

    public static boolean isOcrCaptureToast() {
        return getInstance().ocrCaptureToast;
    }

    public static boolean isVerifyPricesToast() {
        return getInstance().verifyPricesToast;
    }

    public static boolean isSelectPayersToast() {
        return getInstance().selectPayersToast;
    }

    public static boolean isAssignPayersToast() {
        return getInstance().assignPayersToast;
    }

    public static boolean isDisplayPayersToast() {
        return getInstance().displayPayersToast;
    }

}
