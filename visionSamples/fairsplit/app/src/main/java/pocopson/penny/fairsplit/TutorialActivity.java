package pocopson.penny.fairsplit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Vector;

/**
 * Cycle through tutorial pictures.
 */
public class TutorialActivity extends AppCompatActivity implements View.OnClickListener {
    final String TAG = "TutorialActivity";
    Button backButton, nextButton, closeButton;
    ImageView imageView;
    Vector<Integer> imageVector;
    int imageIndex = 0;
    final int NUM_TUTORIAL_IMAGES = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        setTitle("Tutorial");

        setupImageVector();

        imageView = findViewById(R.id.tutorial_image_view);
        imageView.setImageResource(imageVector.firstElement());

        backButton = findViewById(R.id.tutorial_back_button);
        backButton.setOnClickListener(this);

        nextButton = findViewById(R.id.tutorial_next_button);
        nextButton.setOnClickListener(this);

        closeButton = findViewById(R.id.tutorial_close_button);
        closeButton.setOnClickListener(this);
    }

    private void setupImageVector() {
        imageVector = new Vector<>();
        imageVector.add(R.drawable.tutorial_0);
        imageVector.add(R.drawable.tutorial_1);
        imageVector.add(R.drawable.tutorial_2);
        imageVector.add(R.drawable.tutorial_3);
        imageVector.add(R.drawable.tutorial_4);
        imageVector.add(R.drawable.tutorial_5);
    }

    private void advanceOneImage() {
        Log.e(TAG, "Going forward from  " + imageIndex);
        if(imageIndex == (NUM_TUTORIAL_IMAGES-1)) {
            returnToFirstActivity();
            return;
        }
        int nextImage = (imageIndex+1)%NUM_TUTORIAL_IMAGES;
        imageView.setImageResource(imageVector.get(nextImage));
        imageIndex = nextImage;

    }

    private void goBackOneImage() {
        Log.e(TAG, "Going back from  " + imageIndex);
        if(imageIndex == 0) {
            returnToFirstActivity();
            return;
        }
        int lastImage = (imageIndex-1)%NUM_TUTORIAL_IMAGES;
        imageView.setImageResource(imageVector.get(lastImage));
        imageIndex = lastImage;

    }

    private void returnToFirstActivity() {
        Intent intent = new Intent(this, FirstActivity.class);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.tutorial_next_button) {

           advanceOneImage();
        } else if(v.getId() == R.id.tutorial_back_button) {
            goBackOneImage();
        } else if(v.getId() == R.id.tutorial_close_button) {
            returnToFirstActivity();
        }

    }
}
