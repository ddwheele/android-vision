package com.google.android.gms.samples.vision.ocrreader;

import java.util.ArrayList;

public class PayerDebt implements ThreeColumnProvider {
    final String name;
    ArrayList<AllocatedPrice> items;
    float subtotal;
    float total;
    boolean calculated = false;

    public PayerDebt(String name) {
        this.name = name;
        items = new ArrayList<>();
    }

    public void addItem(AllocatedPrice ap) {
        items.add(ap);
        calculated = false;
    }

    public void removeItem(AllocatedPrice ap) {
        items.remove(ap);
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

    // add to get subtotal, and add tax for total
    private void calculate() {
        subtotal = 0;
        for(AllocatedPrice ap : items) {
            subtotal += ap.getPricePerPayer();
        }
        total = subtotal * (1 + ComputeUtils.taxRate);
        calculated = true;
    }

    @Override
    public String getFirstColumnString() {
        return name;
    }

    @Override
    public String getSecondColumnString() {
        return Float.toString(getTotal());
    }

    @Override
    public String getThirdColumnString() {
        return Float.toString(getTotalAndTip());
    }
}
