package pocopson.penny.fairsplit;

import org.junit.Assert;
import org.junit.Test;


public class TestUtils {

    @Test
    public void testPhoneNumberOkay() {
        Assert.assertTrue(Utils.phoneNumberOkay("2342345678"));
        Assert.assertTrue(Utils.phoneNumberOkay("234.234.5678"));
        Assert.assertTrue(Utils.phoneNumberOkay("234-234-5678"));
        Assert.assertTrue(Utils.phoneNumberOkay("234 234 5678"));
        Assert.assertTrue(Utils.phoneNumberOkay("(234)234-5678"));
        Assert.assertTrue(Utils.phoneNumberOkay("(234) 234-5678"));
        Assert.assertTrue(Utils.phoneNumberOkay("12342345678"));
        Assert.assertTrue(Utils.phoneNumberOkay("+1(234)234-5678"));
        Assert.assertTrue(Utils.phoneNumberOkay("+1(234) 234-5678"));
        Assert.assertTrue(Utils.phoneNumberOkay("+1 (234) 234-5678"));
        Assert.assertTrue(Utils.phoneNumberOkay("+12342345678"));

        Assert.assertFalse(Utils.phoneNumberOkay("23423498798778"));
        Assert.assertFalse(Utils.phoneNumberOkay(""));
        Assert.assertFalse(Utils.phoneNumberOkay("aaaaaa"));
        Assert.assertFalse(Utils.phoneNumberOkay("+4412345123456"));
        Assert.assertFalse(Utils.phoneNumberOkay("+44 12345 123456"));
        Assert.assertFalse(Utils.phoneNumberOkay("     "));
        Assert.assertFalse(Utils.phoneNumberOkay("****"));
        Assert.assertFalse(Utils.phoneNumberOkay("++13++"));

    }
}
