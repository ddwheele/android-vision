package pocopson.penny.fairsplit.calculate;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

import pocopson.penny.fairsplit.Utils;

public class PayerDebt implements Parcelable, Serializable, Comparable {
    private static final String TAG = "PayerDebt";
    private static int count = 0; // # of payers created, used for color
    final String name;
    private String phoneNumber;
    protected ArrayList<AssignedPrice> items;
    protected float subtotal;
    protected float total;
    protected boolean calculated = false; // have we calculated what he owes
    private boolean selected = false; // is this person selected in the Activity
    protected final int numberInList; // Payer's order in list (used for color)
    protected float tipPercent = 0.15f;
    protected int popularity = 0; // # of times user has selected him, used for Recents list

    DecimalFormat twoDecimalFormat = new DecimalFormat("#.00");

    /**
     * Keep track of items and prices assigned to payer
     *
     * @param name Payer's name
     */
    public PayerDebt(String name) {
        numberInList = count++;
        this.name = name;
        items = new ArrayList<>();
    }

    protected PayerDebt(Parcel in) {
        name = in.readString();
        phoneNumber = in.readString();
        items = in.createTypedArrayList(AssignedPrice.CREATOR);
        subtotal = in.readFloat();
        total = in.readFloat();
        calculated = in.readByte() != 0;
        selected = in.readByte() != 0;
        numberInList = in.readInt();
        tipPercent = in.readFloat();
        popularity = in.readInt();
    }

    public static final Creator<PayerDebt> CREATOR = new Creator<PayerDebt>() {
        @Override
        public PayerDebt createFromParcel(Parcel in) {
            return new PayerDebt(in);
        }

        @Override
        public PayerDebt[] newArray(int size) {
            return new PayerDebt[size];
        }
    };

    public void incrementPopularity() {
        popularity++;
    }

    public void setPopularity(int newPopularity) {
        popularity = newPopularity;
    }

    public int getPopularity() {
        return popularity;
    }


    public String getName() {
        return name;
    }

    public boolean isTotal() {
        return false;
    }


    public void addItem(AssignedPrice ap) {
        if (!items.contains(ap)) {
            items.add(ap);
        }
        calculated = false;
    }

    public void removeItem(AssignedPrice ap) {
        items.remove(ap);
        calculate();
    }

    public float getSubtotal() {
        if (!calculated) {
            calculate();
        }
        return subtotal;
    }

    /**
     * @return total + tax
     */
    public float getTotal() {
        if (!calculated) {
            calculate();
        }
        return total;
    }

    /**
     * @return total * tipPercent
     */
    public float getTip() {
        if (!calculated) {
            calculate();
        }
        return subtotal * tipPercent;
    }

    public float getTipPercent() {
        return tipPercent;
    }

    public void setTipPercent(float tipPercent) {
        if (tipPercent > 1.0) {
            throw new IllegalArgumentException("Cannot tip more than 100%");
        }
        this.tipPercent = tipPercent;
    }

    /**
     * @return subtotal + tax + tip
     */
    public float getTotalAndTip() {
        return getTotal() + getTip();
    }

    public void recalculate() {
        // for when someone else is sharing an item now
        calculate();
    }

    // add to get subtotal, and add tax for total
    protected void calculate() {
        // apparently Android doesn't do polymorphism. Very annoying.
        if (name.equals(Utils.TOTAL)) {
            calculateForTotal();
            return;
        }
        subtotal = 0;
        for (AssignedPrice ap : items) {
            subtotal += ap.getPricePerPayer();
        }
        total = subtotal * (1 + CalcUtils.taxRate);
        calculated = true;
    }

    protected void calculateForTotal() {
        subtotal = 0;
        for (AssignedPrice ap : items) {
            subtotal += ap.getPrice();
        }
        total = subtotal * (1 + CalcUtils.taxRate);
        calculated = true;
    }

    public String getFirstColumnString() {
        return name;
    }

    public String getSecondColumnString() {
        Log.d(TAG, "total = " + twoDecimalFormat.format(getTotal()));
        return twoDecimalFormat.format(getTotal());
    }

    public String getThirdColumnString() {
        Log.d(TAG, "Tip = " + twoDecimalFormat.format(getTotalAndTip()));
        return twoDecimalFormat.format(getTotalAndTip());
    }

    public int getNumberInList() {
        return numberInList;
    }

    public boolean isSelected() {
        return selected;
    }

    public void toggleSelected() {
        if (selected) {
            selected = false;
        } else {
            selected = true;
        }
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    // TODO Add error checking
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phoneNumber);
        dest.writeTypedList(items);
        dest.writeFloat(subtotal);
        dest.writeFloat(total);
        dest.writeByte((byte) (calculated ? 1 : 0));
        dest.writeByte((byte) (selected ? 1 : 0));
        dest.writeInt(numberInList);
        dest.writeFloat(tipPercent);
        dest.writeInt(popularity);
    }


    @Override
    public int compareTo(Object o) {
        if (o instanceof PayerDebt) {
            PayerDebt other = (PayerDebt) o;
            int level =  other.popularity - popularity;
            if(level != 0) {
                return level;
            }
            int ret = name.compareTo(((PayerDebt) o).name);
            return ret;
        }
        return 0;
    }
}
