package pocopson.penny.fairsplit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import pocopson.penny.fairsplit.adapters.VerifyPricesAdapter;
import pocopson.penny.fairsplit.calculate.AssignedPrice;
import pocopson.penny.fairsplit.calculate.CalcUtils;
import pocopson.penny.fairsplit.calculate.Category;
import pocopson.penny.fairsplit.ocr.OcrCaptureActivity;

import java.util.ArrayList;
import java.util.Collections;

public class VerifyPricesActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "Verify Prices";
    ArrayList<AssignedPrice> priceList;
    ListView priceListView;
    VerifyPricesAdapter priceAdapter;
    Button continueButton, readMoreButton, addButton;
    TextView topMessage;
    boolean pricesVerified = false;
    Category selectedCategory = Category.ITEM;

    private static final int VF_OCR_CAPTURE = 9016;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        continueButton = findViewById(R.id.verify_continue_button);
        readMoreButton = findViewById(R.id.verify_append_prices_button);
        addButton = findViewById(R.id.verify_add_a_price_button);
        topMessage = findViewById(R.id.prices_to_verify);

        priceList = getIntent().getParcelableArrayListExtra(Utils.PRICES);
        priceAdapter = new VerifyPricesAdapter(this, priceList);

        priceListView = findViewById(R.id.split_prices_list);
        priceListView.setAdapter(priceAdapter);

        priceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                if(pricesVerified) {
                    return;
                }
                AssignedPrice selectedPrice = (AssignedPrice) parent.getItemAtPosition(position);
                showCorrectAnItemDialog(VerifyPricesActivity.this, selectedPrice);
            }
        });

        priceListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AssignedPrice selectedPrice = (AssignedPrice) parent.getItemAtPosition(position);
                showDeleteAnItemDialog(VerifyPricesActivity.this, selectedPrice);
                return true;
            }
        });

        continueButton.setOnClickListener(this);
        readMoreButton.setOnClickListener(this);
        addButton.setOnClickListener(this);

        parsePrices();
    }

    private void showDeleteAnItemDialog(Context c, final AssignedPrice selectedPrice) {

        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Delete Price")
                .setMessage("Delete $"+Float.toString(selectedPrice.getPrice()) + "?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        priceList.remove(selectedPrice);
                        parsePrices();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void showCorrectAnItemDialog(Context c, final AssignedPrice selectedPrice) {
        final EditText correctPriceInput = new EditText(c);
        correctPriceInput.setText(Float.toString(selectedPrice.getPrice()));
        correctPriceInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String newPriceString = String.valueOf(correctPriceInput.getText());
                try {
                    Float.valueOf(newPriceString);
                } catch (Exception e) {
                    correctPriceInput.setError("Enter a number");
                    return;
                }
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Change Price")
                .setMessage("Input correct price")
                .setView(correctPriceInput)
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newPriceString = String.valueOf(correctPriceInput.getText());
                        try {
                            float newPrice = Float.valueOf(newPriceString);
                            selectedPrice.updatePrice(newPrice);
                        } catch (Exception e) {
                            // ignore
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private void showAddItemDialog(final Context c) {
        LinearLayout layout = new LinearLayout(c);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText addItemInput = new EditText(c);
        addItemInput.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addItemInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String newPriceString = String.valueOf(addItemInput.getText());
                try {
                    Float.valueOf(newPriceString);
                } catch (Exception e) {
                    addItemInput.setError("Enter a number");
                    return;
                }
            }
        });

        final Spinner spinner = new Spinner(this);
        spinner.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Category.values());
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = Category.values()[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        layout.addView(addItemInput);
        layout.addView(spinner);

        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Add Price")
                .setMessage("Input a price")
                .setView(layout)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String newPriceString = String.valueOf(addItemInput.getText());
                            float newPrice = Float.valueOf(newPriceString);
                            float yValue = 0, referenceY;

                            switch (selectedCategory) {
                                case ITEM:
                                    yValue = -1;
                                    break;
                                case TOTAL: // after last price
                                    referenceY = priceList.get(priceList.size() - 1).getYValue();
                                    yValue = referenceY + 10;
                                    break;
                                case TAX: // before last price
                                    referenceY = priceList.get(priceList.size() - 1).getYValue();
                                    yValue = referenceY - 1;
                                    break;
                                case SUBTOTAL: // before second to last price
                                    referenceY = priceList.get(priceList.size() - 2).getYValue();
                                    yValue = referenceY - 1;
                                    break;
                            }
                            priceList.add(new AssignedPrice(yValue, newPrice));
                            parsePrices();
                        } catch (Exception e) {
                            // ignore
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(TAG, "I got a result");
        if(requestCode == VF_OCR_CAPTURE) {
            Log.d(TAG, "with the right request code");
            if (intent != null) {
                Log.d(TAG, "intent is not null");
                ArrayList<AssignedPrice> p2 = intent.getParcelableArrayListExtra(Utils.PRICES);
                Log.d(TAG, "prices has " + priceList.size() + " and p2 has " + p2.size());
                priceList.addAll(p2);
                Log.d(TAG, "NOW prices has " + priceList.size());
                parsePrices();
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.verify_continue_button) {
            Intent intent = new Intent(this, SelectPayersActivity.class);
            ArrayList<AssignedPrice> cleanPriceList = CalcUtils.removeNonItemRows(priceList);
            intent.putParcelableArrayListExtra(Utils.PRICES, cleanPriceList);
            startActivity(intent);
        } else if(v.getId() == R.id.verify_add_a_price_button) {
            // add a new price at the top
            showAddItemDialog(VerifyPricesActivity.this);
        } else if(v.getId() == R.id.verify_append_prices_button) {
            // get Y value of last price
            // TODO is it still guaranteed to be sorted from the last parsing?
            Collections.sort(priceList);
            float offset = priceList.get(priceList.size()-1).getYValue();
            Intent intent = new Intent(this, OcrCaptureActivity.class);
            intent.putExtra(Utils.OFFSET, offset);
            intent.putParcelableArrayListExtra(Utils.PRICES, priceList);
            startActivityForResult(intent, VF_OCR_CAPTURE);
        }
    }

    private void parsePrices() {
        boolean parsed = CalcUtils.labelSubtotalTaxAndTotal(priceList);

        if(parsed) {
            parseSuccessful();
        } else {
            parseNotSuccessful();
        }
        priceAdapter.notifyDataSetChanged();
    }

    private void parseNotSuccessful() {
        setTitle("Partial Receipt");
        topMessage.setText("Correct these prices or read in more.");
        topMessage.setTextColor(ColorUtils.MY_RED_COLOR);
        addButton.setEnabled(true);
        readMoreButton.setEnabled(true);
        continueButton.setEnabled(false);
        pricesVerified = false;
    }

    private void parseSuccessful() {
        setTitle("Verify Prices");
        topMessage.setText("Prices Verified");
        topMessage.setTextColor(ColorUtils.MY_GREEN_COLOR);
        readMoreButton.setEnabled(false);
        addButton.setEnabled(false);
        continueButton.setEnabled(true);
        pricesVerified = true;
    }
}
