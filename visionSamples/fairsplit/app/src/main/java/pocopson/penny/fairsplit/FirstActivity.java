package pocopson.penny.fairsplit;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import pocopson.penny.fairsplit.ocr.OcrCaptureActivity;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "FirstActivity";
    Button tutorialButton, cameraButton, galleryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        tutorialButton = findViewById(R.id.tutorial_button);
        tutorialButton.setOnClickListener(this);
        cameraButton = findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(this);
        galleryButton = findViewById(R.id.gallery_button);
        galleryButton.setOnClickListener(this);
        galleryButton.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.tutorial_button) {
            Log.e(TAG, "STARTING TutorialActivity");
            Intent intent = new Intent(this, TutorialActivity.class);
            startActivity(intent);
        } else if(v.getId() == R.id.camera_button) {
            Log.e(TAG, "STARTING OcrCaptureActivity");
            Intent intent = new Intent(this, OcrCaptureActivity.class);
            startActivity(intent);
        } else if(v.getId() == R.id.gallery_button) {
            // select from gallery
        }
    }
}
