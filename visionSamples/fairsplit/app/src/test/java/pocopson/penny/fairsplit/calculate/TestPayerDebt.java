package pocopson.penny.fairsplit.calculate;

import android.util.Log;

import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

public class TestPayerDebt {
    double epsilon = 0.001;

    @Test
    public void testSorting() {
        Vector<PayerDebt> payers = new Vector<>();
        payers.add(new PayerDebt("Jack"));
        payers.add(new PayerDebt("Kelly"));
        payers.add(new PayerDebt("Leonard"));
        payers.add(new PayerDebt("Mark"));

        int pop = 5;
        for(PayerDebt p : payers) {
            p.setPopularity(pop++);
        }

        TreeSet<PayerDebt> set = new TreeSet<>();
        set.addAll(payers);

        Iterator<PayerDebt> it = set.iterator();
        int index = payers.size() - 1;
        while(it.hasNext()) {
            PayerDebt real = payers.get(index);
            PayerDebt cand = it.next();
            Assert.assertEquals(real, cand);
            index--;
        }
    }

    @Test
    public void testEquals() {
        PayerDebt henri = new PayerDebt("Henri");
        PayerDebt ivan = new PayerDebt("Ivan");
        PayerDebt henry = new PayerDebt("Henri");

        Assert.assertEquals(henry, henry);
        Assert.assertNotEquals(ivan, henri);
    }

    @Test
    public void testLabelSubtotalTaxAndTotal_normal() {
        AssignedPrice ap20 = new AssignedPrice(100, 20);
        AssignedPrice ap80 = new AssignedPrice(200, 80);

        PayerDebt frederick = new PayerDebt("Freddie");
        PayerDebt gillian = new PayerDebt("Gilly");

        Assert.assertEquals(0, gillian.getTotal(), epsilon);
        Assert.assertEquals(0, gillian.getTotalAndTip(), epsilon);
        Assert.assertEquals(0, frederick.getNumberInList());
        Assert.assertEquals(1, gillian.getNumberInList());
        Assert.assertFalse(gillian.isTotal());
        gillian.setPhoneNumber("7162288472");
        Assert.assertEquals("7162288472", gillian.getPhoneNumber());

        gillian.addItem(ap20);
        ap20.addPayer(gillian);
        gillian.addItem(ap80);
        ap80.addPayer(gillian);

        Assert.assertEquals(100, gillian.getSubtotal(), epsilon);
        gillian.setTipPercent(0.20f);
        Assert.assertEquals(20, gillian.getTip(), epsilon);
        Assert.assertEquals(109.25, gillian.getTotal(), epsilon);
        Assert.assertEquals(129.25, gillian.getTotalAndTip(), epsilon);

        Assert.assertEquals("Gilly", gillian.getFirstColumnString());
        Assert.assertEquals("109.25", gillian.getSecondColumnString());
        Assert.assertEquals("129.25", gillian.getThirdColumnString());

        gillian.removeItem(ap80);

        Assert.assertEquals(20, gillian.getSubtotal(), epsilon);
        Assert.assertEquals(4, gillian.getTip(), epsilon);
        Assert.assertEquals(21.85, gillian.getTotal(), epsilon);
        Assert.assertEquals(25.85, gillian.getTotalAndTip(), epsilon);
    }
}
