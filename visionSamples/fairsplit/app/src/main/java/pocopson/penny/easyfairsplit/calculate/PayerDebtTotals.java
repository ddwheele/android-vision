package pocopson.penny.easyfairsplit.calculate;

import pocopson.penny.easyfairsplit.Utils;

/**
 * This is the aggregate of all the assigned debts
 */
public class PayerDebtTotals extends PayerDebt {

    public PayerDebtTotals() {
        super(Utils.TOTAL);
    }

    @Override
    public boolean isTotal() {
        return true;
    }

    @Override
    public void toggleSelected() {
        // never select this
    }

    // add to get subtotal, and add tax for total
    @Override
    protected void calculate() {
        subtotal = 0;
        for(AssignedPrice ap : items) {
            subtotal += ap.getPrice();
        }
        total = subtotal * (1 + CalcUtils.taxRate);
        calculated = true;
    }

}
