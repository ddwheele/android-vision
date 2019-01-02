package com.google.android.gms.samples.vision.ocrreader.calculate;

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
        ap.addPayer("Arthur Aardvark");
        ap.addPayer("Bobby Bobolink");
        ap.addPayer("Cheshire Cat");
        ap.addPayer("Donald Duck");
        ap.addPayer("Elmer Elephant");

        Assert.assertEquals(12, ap.getPricePerPayer(), epsilon);
        Assert.assertEquals("Elmer Elephant", ap.removePayer());
        Assert.assertEquals(15, ap.getPricePerPayer(),  epsilon);
        Assert.assertEquals("Donald Duck", ap.removePayer());
        Assert.assertEquals("Cheshire Cat", ap.removePayer());

        Assert.assertEquals("60.00", ap.getSecondColumnString());
        Assert.assertEquals("Arthur Aardvark, Bobby Bobolink", ap.getThirdColumnString() );
    }
}
