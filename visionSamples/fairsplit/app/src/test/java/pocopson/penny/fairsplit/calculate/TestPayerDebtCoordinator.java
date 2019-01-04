package pocopson.penny.fairsplit.calculate;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class TestPayerDebtCoordinator {

    double epsilon = 0.001;

    @Test
    public void test_normal() {
        AssignedPrice ap12 = new AssignedPrice(100, 12);
        AssignedPrice ap60 = new AssignedPrice(200, 60);

        PayerDebt henny = new PayerDebt("Henrietta");
        PayerDebt inigo = new PayerDebt("Inigo");
        PayerDebt jules = new PayerDebt("Julius");
        PayerDebt kitty = new PayerDebt("Catherine");

        ArrayList<PayerDebt> pdList = new ArrayList<>();
        pdList.add(henny);
        pdList.add(inigo);
        pdList.add(jules);
        pdList.add(kitty);

        PayerDebtCoordinator pdc = new PayerDebtCoordinator(pdList);
        PayerDebt total = pdc.getTotals();
        Assert.assertEquals(0, henny.getSubtotal(), epsilon);
        Assert.assertEquals(0, total.getSubtotal(), epsilon);
        Assert.assertTrue(total.isTotal());

        pdc.addPayerToItem(henny, ap60);
        Assert.assertEquals(60, henny.getSubtotal(), epsilon);
        Assert.assertEquals(60, total.getSubtotal(), epsilon);

        pdc.addPayerToItem(jules, ap60);
        Assert.assertEquals(30, henny.getSubtotal(), epsilon);
        Assert.assertEquals(30, jules.getSubtotal(), epsilon);
        Assert.assertEquals(60, total.getSubtotal(), epsilon);

        pdc.addPayerToItem(kitty, ap60);
        Assert.assertEquals(20, henny.getSubtotal(), epsilon);
        Assert.assertEquals(20, jules.getSubtotal(), epsilon);
        Assert.assertEquals(20, kitty.getSubtotal(), epsilon);
        Assert.assertEquals(60, total.getSubtotal(), epsilon);

        pdc.addPayerToItem(inigo, ap12);
        Assert.assertEquals(12, inigo.getSubtotal(), epsilon);
        Assert.assertEquals(72, total.getSubtotal(), epsilon);

        pdc.removePayerFromItem(henny.getName(), ap60);
        Assert.assertEquals(30, jules.getSubtotal(), epsilon);
        Assert.assertEquals(12, inigo.getSubtotal(), epsilon);
        Assert.assertEquals(30, kitty.getSubtotal(), epsilon);
        Assert.assertEquals( 0, henny.getSubtotal(), epsilon);
        Assert.assertEquals(72, total.getSubtotal(), epsilon);

        pdc.changeTipPercent(0.5f);
        Assert.assertEquals(15, jules.getTip(), epsilon);
        Assert.assertEquals( 6, inigo.getTip(), epsilon);
        Assert.assertEquals(15, kitty.getTip(), epsilon);
        Assert.assertEquals( 0, henny.getSubtotal(), epsilon);
        Assert.assertEquals(72, total.getSubtotal(), epsilon);

        pdc.removeLastPayerFromItem(ap60);
        Assert.assertEquals(60, jules.getSubtotal(), epsilon);
        Assert.assertEquals(12, inigo.getSubtotal(), epsilon);
        Assert.assertEquals( 0, henny.getSubtotal(), epsilon);
        Assert.assertEquals(72, total.getSubtotal(), epsilon);

        pdc.removeLastPayerFromItem(ap60);
        Assert.assertEquals(12, inigo.getSubtotal(), epsilon);
        Assert.assertEquals( 0, henny.getSubtotal(), epsilon);
        Assert.assertEquals(12, total.getSubtotal(), epsilon);
    }

    @Test
    public void test_removeFromTotal() {
        AssignedPrice ap60 = new AssignedPrice(200, 60);

        PayerDebt letty = new PayerDebt("Leticia");
        PayerDebt marc = new PayerDebt("Marcus");

        ArrayList<PayerDebt> pdList = new ArrayList<>();
        pdList.add(letty);
        pdList.add(marc);

        PayerDebtCoordinator pdc = new PayerDebtCoordinator(pdList);
        PayerDebt total = pdc.getTotals();
        Assert.assertEquals(0, letty.getSubtotal(), epsilon);
        Assert.assertEquals(0, total.getSubtotal(), epsilon);
        Assert.assertTrue(total.isTotal());

        pdc.addPayerToItem(letty, ap60);
        Assert.assertEquals(60, letty.getSubtotal(), epsilon);
        Assert.assertEquals(60, total.getSubtotal(), epsilon);

        pdc.addPayerToItem(marc, ap60);
        Assert.assertEquals(30, marc.getSubtotal(), epsilon);
        Assert.assertEquals(30, letty.getSubtotal(), epsilon);
        Assert.assertEquals(60, total.getSubtotal(), epsilon);
    }
}
