package pocopson.penny.easyfairsplit.calculate;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class TestCalcUtils {

    @Test
    public void testLabelSubtotalTaxAndTotal_normal() {
        ArrayList<AssignedPrice> pr = new ArrayList<>();
        pr.add(new AssignedPrice(0, 1.0f)); // item
        pr.add(new AssignedPrice(10, 1.0f));// item
        pr.add(new AssignedPrice(20, 1.0f));// item
        pr.add(new AssignedPrice(30, 1.0f));// item
        pr.add(new AssignedPrice(40, 1.0f));// item
        pr.add(new AssignedPrice(50, 5.0f));// sub
        pr.add(new AssignedPrice(60, 0.5f));// tax
        pr.add(new AssignedPrice(70, 5.5f));// total
        pr.add(new AssignedPrice(80, 5.5f));// total

        boolean expTrue = CalcUtils.labelSubtotalTaxAndTotal(pr);
        Assert.assertTrue(expTrue);
        for(int i=0; i<5; i++) {
            Assert.assertEquals(pr.get(i).getCategoryString(), Category.ITEM.toString());
        }
        Assert.assertEquals(pr.get(5).getCategoryString(), Category.SUBTOTAL.toString());
        Assert.assertEquals(pr.get(6).getCategoryString(), Category.TAX.toString());
        Assert.assertEquals(pr.get(7).getCategoryString(), Category.TOTAL.toString());
        Assert.assertEquals(pr.get(8).getCategoryString(), Category.TOTAL.toString());
    }

    @Test
    public void testLabelSubtotalTaxAndTotal_negativeItem() {
        // make list of AssignedPrices
        ArrayList<AssignedPrice> pr = new ArrayList<>();
        pr.add(new AssignedPrice(0, 1.0f));
        pr.add(new AssignedPrice(10, 1.0f));
        pr.add(new AssignedPrice(20, -1.0f));
        pr.add(new AssignedPrice(30, 1.0f));
        pr.add(new AssignedPrice(40, 1.0f));
        pr.add(new AssignedPrice(50, 3.0f));
        pr.add(new AssignedPrice(60, 0.3f));
        pr.add(new AssignedPrice(70, 3.3f));

        boolean expTrue = CalcUtils.labelSubtotalTaxAndTotal(pr);
        Assert.assertTrue(expTrue);
        for(int i=0; i<5; i++) {
            Assert.assertEquals(pr.get(i).getCategoryString(), Category.ITEM.toString());
        }
        Assert.assertEquals(pr.get(5).getCategoryString(), Category.SUBTOTAL.toString());
        Assert.assertEquals(pr.get(6).getCategoryString(), Category.TAX.toString());
        Assert.assertEquals(pr.get(7).getCategoryString(), Category.TOTAL.toString());
    }

    @Test
    public void testLabelSubtotalTaxAndTotal_oneItem() {
        // make list of AssignedPrices
        ArrayList<AssignedPrice> pr = new ArrayList<>();
        pr.add(new AssignedPrice(0, 1.0f));  // item
        pr.add(new AssignedPrice(10, 1.0f)); // sub
        pr.add(new AssignedPrice(15, 1.0f)); // sub
        pr.add(new AssignedPrice(20, 0.1f)); // tax
        pr.add(new AssignedPrice(30, 1.1f)); // tot
        pr.add(new AssignedPrice(50, 1.1f)); // tot

        boolean expTrue = CalcUtils.labelSubtotalTaxAndTotal(pr);
        Assert.assertTrue(expTrue);
        Assert.assertEquals(pr.get(0).getCategoryString(), Category.ITEM.toString());
        Assert.assertEquals(pr.get(1).getCategoryString(), Category.SUBTOTAL.toString());
        Assert.assertEquals(pr.get(2).getCategoryString(), Category.SUBTOTAL.toString());
        Assert.assertEquals(pr.get(3).getCategoryString(), Category.TAX.toString());
        Assert.assertEquals(pr.get(4).getCategoryString(), Category.TOTAL.toString());
        Assert.assertEquals(pr.get(5).getCategoryString(), Category.TOTAL.toString());
    }

    @Test
    public void testLabelSubtotalTaxAndTotal_badSubtotal() {
        ArrayList<AssignedPrice> pr = new ArrayList<>();
        pr.add(new AssignedPrice(0, 1.0f)); // item
        pr.add(new AssignedPrice(10, 1.0f));// item
        pr.add(new AssignedPrice(20, 1.0f));// item
        pr.add(new AssignedPrice(30, 1.0f));// item
        pr.add(new AssignedPrice(40, 1.0f));// item
        pr.add(new AssignedPrice(50, 6.0f));// sub
        pr.add(new AssignedPrice(60, 0.5f));// tax
        pr.add(new AssignedPrice(70, 5.5f));// total
        pr.add(new AssignedPrice(80, 5.5f));// total

        boolean expFalse = CalcUtils.labelSubtotalTaxAndTotal(pr);
        Assert.assertFalse(expFalse);
        for(int i=0; i<9; i++) {
            Assert.assertEquals(pr.get(i).getCategoryString(), Category.ITEM.toString());
        }
    }

    @Test
    public void testLabelSubtotalTaxAndTotal_badTotal() {
        ArrayList<AssignedPrice> pr = new ArrayList<>();
        pr.add(new AssignedPrice(0, 1.0f)); // item
        pr.add(new AssignedPrice(10, 1.0f));// item
        pr.add(new AssignedPrice(20, 1.0f));// item
        pr.add(new AssignedPrice(30, 1.0f));// item
        pr.add(new AssignedPrice(40, 1.0f));// item
        pr.add(new AssignedPrice(50, 6.0f));// sub
        pr.add(new AssignedPrice(60, 0.5f));// tax
        pr.add(new AssignedPrice(70, 5.75f));// total
        pr.add(new AssignedPrice(80, 5.75f));// total

        boolean expFalse = CalcUtils.labelSubtotalTaxAndTotal(pr);
        Assert.assertFalse(expFalse);
        for(int i=0; i<9; i++) {
            Assert.assertEquals(pr.get(i).getCategoryString(), Category.ITEM.toString());
        }
    }

    @Test
    public void testLabelSubtotalTaxAndTotal_noSubtotal() {
        ArrayList<AssignedPrice> pr = new ArrayList<>();
        pr.add(new AssignedPrice(0, 1.0f)); // item
        pr.add(new AssignedPrice(10, 1.0f));// item
        pr.add(new AssignedPrice(20, 1.0f));// item
        pr.add(new AssignedPrice(30, 1.0f));// item
        pr.add(new AssignedPrice(40, 1.0f));// item
        pr.add(new AssignedPrice(60, 0.5f));// tax
        pr.add(new AssignedPrice(70, 5.75f));// total
        pr.add(new AssignedPrice(80, 5.75f));// total

        boolean expFalse = CalcUtils.labelSubtotalTaxAndTotal(pr);
        Assert.assertFalse(expFalse);
        for(int i=0; i<8; i++) {
            Assert.assertEquals(pr.get(i).getCategoryString(), Category.ITEM.toString());
        }
    }

}
