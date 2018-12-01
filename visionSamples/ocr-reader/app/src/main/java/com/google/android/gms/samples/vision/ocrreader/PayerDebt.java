package com.google.android.gms.samples.vision.ocrreader;

import android.graphics.Color;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PayerDebt  {
    final String name;
    ArrayList<AllocatedPrice> items;
    float subtotal;
    float total;
    boolean calculated = false; // have we calculated what he owes
    boolean selected = false; // is this person selected in the Activity

    DecimalFormat twoDForm = new DecimalFormat("#.##");

    public PayerDebt(String name) {
        this.name = name;
        items = new ArrayList<>();
    }

    public void addItem(AllocatedPrice ap) {
        if(!items.contains(ap)) {
            items.add(ap);
        }
        calculated = false;
    }

    public void removeItem(AllocatedPrice ap) {
        items.remove(ap);
        calculate();
    }

    public float getSubtotal() {
        if(!calculated) {
           calculate();
        }
        return subtotal;
    }

    /**
     * @return total + tax
     */
    public float getTotal() {
        if(!calculated) {
            calculate();
        }
        return total;
    }

    /**
     * @param tipPercent 0.15 for 15%
     * @return total * tipPercent
     */
    public float getTip(float tipPercent) {
        if(!calculated) {
            calculate();
        }
        return total * tipPercent;
    }

    /**
     * @return subtotal + tax + 15% tip
     */
    public float getTotalAndTip() {
        return getTotal() + getTip(0.15f);
    }

    /**
     * @return subtotal + tax + 15% tip
     */
    public float getTotalAndTip(float tipPercent) {
        return getTotal() + getTip(tipPercent);
    }

    public void recalculate() {
        // for when someone else is sharing an item now
        calculate();
    }

    // add to get subtotal, and add tax for total
    private void calculate() {
        subtotal = 0;
        for(AllocatedPrice ap : items) {
            subtotal += ap.getPricePerPayer();
        }
        total = subtotal * (1 + ComputeUtils.taxRate);
        calculated = true;
    }

    public String getFirstColumnString() {
        return name;
    }

    public String getSecondColumnString() {
        return twoDForm.format(subtotal);
    }

    public String getThirdColumnString() {
        return twoDForm.format(getTotal());
    }

    public int getThirdColumnBackgroundColor() {
        if(selected) {
            return Color.GREEN;
        }
        return ComputeUtils.BACKGROUND;
    }

    public boolean isSelected() {
        return selected;
    }

    public void toggleSelected() {
        if(selected) {
            selected = false;
        }
        else {
            selected = true;
        }
    }
}
