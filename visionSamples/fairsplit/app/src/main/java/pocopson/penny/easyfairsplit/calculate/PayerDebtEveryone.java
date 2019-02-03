package pocopson.penny.easyfairsplit.calculate;

import pocopson.penny.easyfairsplit.Utils;

public class PayerDebtEveryone extends PayerDebt {

    public PayerDebtEveryone() {
        super(Utils.EVERYONE);
    }

    @Override
    public void toggleSelected() {
        // never select this
    }
}
