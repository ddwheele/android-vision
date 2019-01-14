package pocopson.penny.fairsplit.calculate;

import org.junit.Assert;
import org.junit.Test;

public class TestPayerDebt {
    double epsilon = 0.001;

    @Test
    public void testEquals() {
        PayerDebt henri = new PayerDebt("Henri");
        PayerDebt ivan = new PayerDebt("ivan");
        PayerDebt henry = new PayerDebt("Henri");

        Assert.assertEquals(henri, henry);
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
