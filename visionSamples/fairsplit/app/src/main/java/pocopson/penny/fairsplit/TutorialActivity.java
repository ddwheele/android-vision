package pocopson.penny.fairsplit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Vector;

/**
 * Cycle through tutorial pictures.
 */
public class TutorialActivity extends AppCompatActivity implements View.OnClickListener {
    Button nextButton, closeButton;
    ImageView imageView;
    Vector<Integer> imageVector;
    int imageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        setTitle("Tutorial");

        setupImageVector();

        imageView = findViewById(R.id.tutorial_image_view);
        imageView.setImageResource(imageVector.firstElement());

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
        int nextImage = (imageIndex+1)%6;
        imageView.setImageResource(imageVector.get(nextImage));
        imageIndex = nextImage;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.tutorial_next_button) {
           advanceOneImage();
        } else if(v.getId() == R.id.tutorial_close_button) {
            Intent intent = new Intent(this, FirstActivity.class);
            startActivity(intent);
        }

    }
}
