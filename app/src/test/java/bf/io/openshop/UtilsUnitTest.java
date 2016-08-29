package bf.io.openshop;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import com.google.gson.Gson;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import bf.io.openshop.utils.Utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

/**
 * Simple unit tests for {@link Utils} class.
 */
@RunWith(MockitoJUnitRunner.class)
public class UtilsUnitTest {

    @Mock
    Context mMockContext;

    @Mock
    Activity mMockActivity;

    @Test
    public void gsonConstructorNotNull() {
        Gson gson = Utils.getGsonParser();
        assertNotEquals(gson, null);
    }

    @Test
    public void progressDialogConstructorNotNull() {
        ProgressDialog progressDialog = Utils.generateProgressDialog(mMockContext, false);
        assertNotEquals(progressDialog, null);
    }

    @Test
    public void parseDateOK() {
        String startDate = "2016-04-13 13:21:04";
        String result = Utils.parseDate(startDate);
        String expected = "13.04.2016";
        assertEquals(result, expected);
    }

    @Test
    public void parseDateFailed() {
        String startDate = "201604-13 13:21:04";
        String result = Utils.parseDate(startDate);
        assertEquals(result, startDate);

        startDate = "2016-04-1313:21:04";
        result = Utils.parseDate(startDate);
        assertEquals(result, startDate);
    }

    @Test
    public void testDiscountCalculation() {
        when(mMockContext.getString(R.string.format_price_discount_percents)).thenReturn("-%1$d %%");
        double startBasePrice = 45;
        double startDiscountPrice = 40;
        String expected = "-11 %";
        String result = Utils.calculateDiscountPercent(mMockContext, startBasePrice, startDiscountPrice);
        assertEquals(result, expected);

        startBasePrice = 45.80;
        startDiscountPrice = 33.16;
        expected = "-28 %";
        result = Utils.calculateDiscountPercent(mMockContext, startBasePrice, startDiscountPrice);
        assertEquals(result, expected);

        startBasePrice = 33.16;
        startDiscountPrice = 45.8;
        expected = "-0 %";
        result = Utils.calculateDiscountPercent(mMockContext, startBasePrice, startDiscountPrice);
        assertEquals(result, expected);
    }
}