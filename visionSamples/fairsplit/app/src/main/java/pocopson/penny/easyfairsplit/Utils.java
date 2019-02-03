package pocopson.penny.easyfairsplit;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import pocopson.penny.easyfairsplit.ocr.OcrCaptureActivity;

public class Utils {
    private static final String TAG = "Utils";

    public static final String TOTAL = "Total";
    public static final String EVERYONE = "Everyone";

    public static final String PRICES = "prices";
    public static final String PAYERS = "payers";
    public static final String OFFSET = "offset";

    public static final String PAYER_COORDINATOR = "payer coordinator";

    public static String filepath = "Storage";

    protected static Map<String, File> stringFileMap = new HashMap<>();

    public static String phoneRegex = "[-|\\d|.|(|)|+| ]";

    protected static File getFileFromName(String name) {
        File ret = stringFileMap.get(name);
        if (ret == null) {
            ret = new File(
                    OcrCaptureActivity.getAppContext().getExternalFilesDir(Utils.filepath),
                    name);
            stringFileMap.put(name, ret);
        }
        return ret;
    }

    public static Object loadData(String filename) {
        File myExternalFile = getFileFromName(filename);
        if (!isExternalStorageReadable()) {
            Log.e(TAG, "External storage not readable");
        } else {
            Log.d(TAG, "Readable");
        }
        Object ret = null;
        try {
            FileInputStream fis = new FileInputStream(myExternalFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            ret = ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public static void saveData(String filename, Object toWrite) {
        File myExternalFile = getFileFromName(filename);

        if (!isExternalStorageWritable()) {
            Log.e(TAG, "External storage not writable");
        } else {
            Log.d(TAG, "Writable");
        }

        try {
            FileOutputStream fos = new FileOutputStream(myExternalFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(toWrite);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void requestPermissions(Activity activity, int requestCode, String... permissions) {
        ActivityCompat.requestPermissions(activity, permissions,
                requestCode);
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

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean phoneNumberOkay(String phoneNumberString) {
        String illegalChars = phoneNumberString.replaceAll(phoneRegex, "");
        int illegal = illegalChars.length();

        if(illegal > 0) {
            return false;
        }

        String digits = phoneNumberString.replaceAll("\\D", "");
        if(digits.length() == 10) {
            return true;
        }

        if(digits.length() == 11) {
            if(phoneNumberString.startsWith("1") || phoneNumberString.startsWith("+1")) {
                return true;
            }
        }
        return false;
    }
}
