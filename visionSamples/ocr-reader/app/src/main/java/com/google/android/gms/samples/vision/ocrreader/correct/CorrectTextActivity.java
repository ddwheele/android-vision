package com.google.android.gms.samples.vision.ocrreader.correct;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.samples.vision.ocrreader.R;
import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;
import java.util.Vector;

public class CorrectTextActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView textView;
    final String TAG = "CorrectTextActivity";
    private boolean listenForPriceClick = false;
    private ImageWithOverlay imageWithOverlay;
    private Vector<Float> priceList = new Vector<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correct_text);

        String path = getIntent().getStringExtra("image");
        imageWithOverlay = findViewById(R.id.imageWithOverlay);
        imageWithOverlay.setImage(path);

        // Unparcel the graphics data
        ArrayList<ParcelableOcrGraphic> graphicList = getIntent().getParcelableArrayListExtra("graphics");
        imageWithOverlay.setGraphicList(graphicList);

        findViewById(R.id.correct_add_prices).setOnClickListener(this);
        findViewById(R.id.correct_continue).setOnClickListener(this);
    }

    /**
     * onTap is called to capture the first TextBlock under the tap location and add it
     * to the price list
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the activity is ending.
     */
    private boolean onTap(float rawX, float rawY) {
        ParcelableOcrGraphic graphic = imageWithOverlay.getGraphicAtLocation(rawX, rawY);
        graphic.toggleSelected();

        if(graphic.isSelected()) {

        }

        TextBlock text = null;
//        if (graphic != null) {
//            text = graphic.getTextBlock();
//            if (text != null && text.getValue() != null) {
//                Intent data = new Intent();
//                data.putExtra(TextBlockObject, text.getValue());
//                setResult(CommonStatusCodes.SUCCESS, data);
//                finish();
//            }
//            else {
//                Log.d(TAG, "text data is null");
//            }
//        }
//        else {
//            Log.d(TAG,"no text detected");
//        }
        return text != null;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.correct_add_prices) {
            listenForPriceClick = true;
//            EditText et = findViewById(R.id.names_prompt);
//            String newName = et.getText().toString();
//            namesList.add(newName);
//            adapter.notifyDataSetChanged();
//            et.setText("");
        }
        else if (v.getId() == R.id.correct_continue) {
//            namesList.remove(selectedNameIndex);
//            ((Button) findViewById(R.id.names_delete)).setEnabled(false);
//            adapter.notifyDataSetChanged();
//            Log.v(TAG, "Deleting item " + selectedNameIndex);
        }
    }
}
