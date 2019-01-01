package com.google.android.gms.samples.vision.ocrreader.calculate;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class TestAssignedPrice {
    double epsilon = 0.001;
    @Test
    public void testLabelSubtotalTaxAndTotal_normal() {
        AssignedPrice ap = new AssignedPrice(100, 50);
        Assert.assertTrue(ap.isItem());
        Assert.assertEquals(ap.getCategoryString(), Category.ITEM.toString());
        Assert.assertTrue(ap.hasNoPayers());
        Assert.assertEquals(ap.getPriceString(), "50.00");

        ap.labelAsTotal();
        Assert.assertEquals(ap.getFirstColumnString(), Category.TOTAL.toString());

        ap.labelAsItem();
        Assert.assertEquals(ap.getFirstColumnString(), Category.ITEM.toString());

        ap.updatePrice(60);
        ap.addPayer("Arthur Aardvark");
        ap.addPayer("Bobby Bobolink");
        ap.addPayer("Cheshire Cat");
        ap.addPayer("Donald Duck");
        ap.addPayer("Elmer Elephant");

        Assert.assertEquals(ap.getPricePerPayer(), 12, epsilon);
        Assert.assertEquals(ap.removePayer(), "Elmer Elephant");
        Assert.assertEquals(ap.getPricePerPayer(), 15, epsilon);
        Assert.assertEquals(ap.removePayer(), "Donald Duck");
        Assert.assertEquals(ap.removePayer(), "Cheshire Cat");

        Assert.assertEquals(ap.getSecondColumnString(), "60.00");
        Assert.assertEquals(ap.getThirdColumnString(), "Arthur Aardvark, Bobby Bobolink");
    }
}
