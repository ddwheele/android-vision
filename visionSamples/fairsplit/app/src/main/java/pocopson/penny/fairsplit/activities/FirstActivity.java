package pocopson.penny.fairsplit.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import pocopson.penny.fairsplit.R;
import pocopson.penny.fairsplit.ocr.OcrCaptureActivity;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "FirstActivity";
    Button tutorialButton, cameraButton;
//    Button galleryButton;
    static final int GET_WRITE_EXTERNAL_PERMISSION = 24;
    String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        tutorialButton = findViewById(R.id.tutorial_button);
        tutorialButton.setOnClickListener(this);
        cameraButton = findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(this);
//        galleryButton = findViewById(R.id.gallery_button);
//        galleryButton.setOnClickListener(this);
//        galleryButton.setEnabled(false);

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (rc != PackageManager.PERMISSION_GRANTED) {
            requestWriteToStoragePermission();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.tutorial_button) {
            Intent intent = new Intent(this, TutorialActivity.class);
            startActivity(intent);
        } else if(v.getId() == R.id.camera_button) {
            Intent intent = new Intent(this, OcrCaptureActivity.class);
            startActivity(intent);
//        } else if(v.getId() == R.id.gallery_button) {
//            // select from gallery
        }
    }

    private void requestWriteToStoragePermission() {
        Log.w(TAG, "Requesting permission to write to external storage.");

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, GET_WRITE_EXTERNAL_PERMISSION);
            return;
        }

        // Uncomment to force user to click ok
//        View.OnClickListener listener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ActivityCompat.requestPermissions(OcrCaptureActivity.this, permissions,
//                        RC_HANDLE_CAMERA_PERM);
//            }
//        };

//        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
//                Snackbar.LENGTH_LONG)
////                .setAction(R.string.ok, listener)
//                .show();

        ActivityCompat.requestPermissions(FirstActivity.this, permissions,
                GET_WRITE_EXTERNAL_PERMISSION);
    }
}
