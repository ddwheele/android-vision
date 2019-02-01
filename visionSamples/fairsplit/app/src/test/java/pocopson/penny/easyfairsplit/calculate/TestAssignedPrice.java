package pocopson.penny.easyfairsplit.calculate;

import org.junit.Assert;
import org.junit.Test;

public class TestAssignedPrice {
    double epsilon = 0.001;

    @Test
    public void testLabelSubtotalTaxAndTotal_normal() {
        AssignedPrice ap = new AssignedPrice(100, 50);
        Assert.assertTrue(ap.isItem());
        Assert.assertEquals( Category.ITEM.toString(), ap.getCategoryString());
        Assert.assertTrue(ap.hasNoPayers());
        Assert.assertEquals("50.00", ap.getPriceString());

        ap.labelAsTotal();
        Assert.assertEquals(Category.TOTAL.toString(), ap.getFirstColumnString());

        ap.labelAsItem();
        Assert.assertEquals(Category.ITEM.toString(), ap.getFirstColumnString());

        ap.updatePrice(60);
        ap.addPayer(new PayerDebt("Arthur Aardvark"));
        ap.addPayer(new PayerDebt("Bobby Bobolink"));
        PayerDebt chess = new PayerDebt("Cheshire Cat");
        PayerDebt donny = new PayerDebt("Donald Duck");
        PayerDebt elmo = new PayerDebt("Elmer Elephant");
        ap.addPayer(chess);
        ap.addPayer(donny);
        ap.addPayer(elmo);

        Assert.assertEquals(12, ap.getPricePerPayer(), epsilon);
        Assert.assertEquals("Elmer Elephant", ap.removePayer());
        Assert.assertEquals(15, ap.getPricePerPayer(),  epsilon);
        Assert.assertEquals("Donald Duck", ap.removePayer());
        Assert.assertEquals("Cheshire Cat", ap.removePayer());

        Assert.assertEquals("60.00", ap.getSecondColumnString());
        Assert.assertEquals("Arthur Aardvark, Bobby Bobolink", ap.getThirdColumnString() );
    }
}
