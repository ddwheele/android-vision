package pocopson.penny.easyfairsplit.calculate;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import pocopson.penny.easyfairsplit.Utils;

public class TestPayerDebtCoordinator {

    double epsilon = 0.001;

    @Test
    public void test_everyone() {
        AssignedPrice ap60 = new AssignedPrice(200, 60);

        PayerDebt nana = new PayerDebt("Nana");
        PayerDebt ophelia = new PayerDebt("Ophelia");
        PayerDebt penny = new PayerDebt("Penelope");
        PayerDebt quast = new PayerDebt("Quast");

        ArrayList<PayerDebt> pdList = new ArrayList<>();
        pdList.add(nana);
        pdList.add(ophelia);
        pdList.add(penny);
        pdList.add(quast);

        PayerDebtCoordinator pdc = new PayerDebtCoordinator(pdList);
        PayerDebt total = pdc.getTotals();
        Assert.assertEquals(0, nana.getSubtotal(), epsilon);
        Assert.assertEquals(0, ophelia.getSubtotal(), epsilon);
        Assert.assertEquals(0, penny.getSubtotal(), epsilon);
        Assert.assertEquals(0, quast.getSubtotal(), epsilon);
        Assert.assertEquals(0, total.getSubtotal(), epsilon);
        Assert.assertTrue(total.isTotal());

        pdc.addEveryoneToItem(ap60);
        Assert.assertEquals(15, nana.getSubtotal(), epsilon);
        Assert.assertEquals(15, ophelia.getSubtotal(), epsilon);
        Assert.assertEquals(15, penny.getSubtotal(), epsilon);
        Assert.assertEquals(15, quast.getSubtotal(), epsilon);
        Assert.assertEquals(60, total.getSubtotal(), epsilon);

        pdc.removeEveryoneFromItem(ap60);
        Assert.assertEquals(0, nana.getSubtotal(), epsilon);
        Assert.assertEquals(0, ophelia.getSubtotal(), epsilon);
        Assert.assertEquals(0, penny.getSubtotal(), epsilon);
        Assert.assertEquals(0, quast.getSubtotal(), epsilon);
        Assert.assertEquals(0, total.getSubtotal(), epsilon);

        pdc.addPayerToItem(new PayerDebtEveryone(), ap60);
        Assert.assertEquals(15, nana.getSubtotal(), epsilon);
        Assert.assertEquals(15, ophelia.getSubtotal(), epsilon);
        Assert.assertEquals(15, penny.getSubtotal(), epsilon);
        Assert.assertEquals(15, quast.getSubtotal(), epsilon);
        Assert.assertEquals(60, total.getSubtotal(), epsilon);

        pdc.removePayerFromItem(ophelia, ap60);
        Assert.assertEquals(20, nana.getSubtotal(), epsilon);
        Assert.assertEquals(0, ophelia.getSubtotal(), epsilon);
        Assert.assertEquals(20, penny.getSubtotal(), epsilon);
        Assert.assertEquals(20, quast.getSubtotal(), epsilon);
        Assert.assertEquals(60, total.getSubtotal(), epsilon);

        pdc.removePayerFromItem(penny, ap60);
        Assert.assertEquals(30, nana.getSubtotal(), epsilon);
        Assert.assertEquals(0, ophelia.getSubtotal(), epsilon);
        Assert.assertEquals(0, penny.getSubtotal(), epsilon);
        Assert.assertEquals(30, quast.getSubtotal(), epsilon);
        Assert.assertEquals(60, total.getSubtotal(), epsilon);

        pdc.addEveryoneToItem(ap60);
        Assert.assertEquals(15, nana.getSubtotal(), epsilon);
        Assert.assertEquals(15, ophelia.getSubtotal(), epsilon);
        Assert.assertEquals(15, penny.getSubtotal(), epsilon);
        Assert.assertEquals(15, quast.getSubtotal(), epsilon);
        Assert.assertEquals(60, total.getSubtotal(), epsilon);

        pdc.removePayerFromItem(Utils.EVERYONE, ap60);
        Assert.assertEquals(0, nana.getSubtotal(), epsilon);
        Assert.assertEquals(0, ophelia.getSubtotal(), epsilon);
        Assert.assertEquals(0, penny.getSubtotal(), epsilon);
        Assert.assertEquals(0, quast.getSubtotal(), epsilon);
        Assert.assertEquals(0, total.getSubtotal(), epsilon);

        pdc.togglePayerOnItem(new PayerDebtEveryone(), ap60);
        Assert.assertEquals(15, nana.getSubtotal(), epsilon);
        Assert.assertEquals(15, ophelia.getSubtotal(), epsilon);
        Assert.assertEquals(15, penny.getSubtotal(), epsilon);
        Assert.assertEquals(15, quast.getSubtotal(), epsilon);
        Assert.assertEquals(60, total.getSubtotal(), epsilon);

        pdc.togglePayerOnItem(new PayerDebtEveryone(), ap60);
        Assert.assertEquals(0, nana.getSubtotal(), epsilon);
        Assert.assertEquals(0, ophelia.getSubtotal(), epsilon);
        Assert.assertEquals(0, penny.getSubtotal(), epsilon);
        Assert.assertEquals(0, quast.getSubtotal(), epsilon);
        Assert.assertEquals(0, total.getSubtotal(), epsilon);
    }

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
