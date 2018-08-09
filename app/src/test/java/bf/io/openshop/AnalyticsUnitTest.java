package bf.io.openshop;

import android.content.Context;
import android.os.Bundle;

import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bf.io.openshop.entities.Shop;
import bf.io.openshop.entities.cart.Cart;
import bf.io.openshop.entities.cart.CartProductItem;
import bf.io.openshop.entities.cart.CartProductItemVariant;
import bf.io.openshop.entities.delivery.Shipping;
import bf.io.openshop.utils.Analytics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Simple unit tests for {@link Analytics} class.
 * <p/>
 * Careful: Because it is testing static class with static methods and static fields.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Analytics.class, GoogleAnalytics.class, AppEventsLogger.class, SettingsMy.class})
public class AnalyticsUnitTest {

    @Mock
    Context mockContext;

    @Mock
    GoogleAnalytics mockAnalytics;

    @Mock
    Tracker mockTracker;

    @Mock
    Tracker mockTrackerApp;

    @Mock
    AppEventsLogger mockAppEventsLogger;

    Shop testShop = new Shop("testShop", "UA-test");

    @Before
    public void preparation() throws Exception {
        // clean up
        MockitoAnnotations.initMocks(this); // for case them used another runner
        PowerMockito.spy(Analytics.class);
        Whitebox.setInternalState(Analytics.class, "mTrackers", new HashMap<>());
        Whitebox.setInternalState(Analytics.class, "facebookLogger", (Object[]) null);
        Whitebox.setInternalState(Analytics.class, "campaignUri", (Object[]) null);
    }

    private void prepareMockedFields() throws Exception {
        // Mock responses
        PowerMockito.mockStatic(GoogleAnalytics.class);
        PowerMockito.mockStatic(AppEventsLogger.class);
        doReturn(mockAnalytics).when(GoogleAnalytics.class, "getInstance", mockContext);
        doReturn(mockAppEventsLogger).when(AppEventsLogger.class, "newLogger", anyObject());
        when(mockAnalytics.newTracker(R.xml.global_tracker)).thenReturn(mockTracker);
        when(mockAnalytics.newTracker(testShop.getGoogleUa())).thenReturn(mockTrackerApp);
    }


    @Test
    public void prepareGlobalTrackerAndFbLoggerTest() throws Exception {
        // Mock responses
        PowerMockito.mockStatic(GoogleAnalytics.class);
        PowerMockito.mockStatic(AppEventsLogger.class);
        doReturn(mockAnalytics).when(GoogleAnalytics.class, "getInstance", mockContext);
        doReturn(mockAppEventsLogger).when(AppEventsLogger.class, "newLogger", anyObject());
        when(mockAnalytics.newTracker(R.xml.global_tracker)).thenReturn(mockTracker);

        // Tested method invocation
        Analytics.prepareTrackersAndFbLogger(null, mockContext);

        // Verify results
        verifyStatic(times(1));
        GoogleAnalytics.getInstance(mockContext);
        verifyStatic(times(1));
        Analytics.deleteAppTrackers();

        verify(mockAnalytics, times(1)).newTracker(R.xml.global_tracker);

        verify(mockTracker, times(1)).enableAutoActivityTracking(true);
        verify(mockTracker, times(1)).enableExceptionReporting(true);
        verify(mockTracker, times(1)).enableAdvertisingIdCollection(true);
        verifyNoMoreInteractions(mockTracker);

        HashMap<String, Tracker> trackersField = Whitebox.getInternalState(Analytics.class, "mTrackers");
        assertEquals(trackersField.size(), 1);
        AppEventsLogger appEventsLoggerField = Whitebox.getInternalState(Analytics.class, "facebookLogger");
        assertNotEquals(appEventsLoggerField, null);
    }

    @Test
    public void prepareTrackersAndFbLoggerTest() throws Exception {
        prepareMockedFields();

        // Tested method invocation
        Analytics.prepareTrackersAndFbLogger(testShop, mockContext);

        // Verify results
        verifyStatic(times(1));
        GoogleAnalytics.getInstance(mockContext);
        verifyStatic(never());
        Analytics.deleteAppTrackers();

        verify(mockAnalytics, times(1)).newTracker(testShop.getGoogleUa());
        verify(mockAnalytics, times(1)).newTracker(R.xml.global_tracker);

        verify(mockTrackerApp, times(1)).enableAutoActivityTracking(true);
        verify(mockTrackerApp, times(1)).enableExceptionReporting(false);
        verify(mockTrackerApp, times(1)).enableAdvertisingIdCollection(true);
        verifyNoMoreInteractions(mockTrackerApp);

        verify(mockTracker, times(1)).enableAutoActivityTracking(true);
        verify(mockTracker, times(1)).enableExceptionReporting(true);
        verify(mockTracker, times(1)).enableAdvertisingIdCollection(true);
        verifyNoMoreInteractions(mockTracker);

        HashMap<String, Tracker> trackersField = Whitebox.getInternalState(Analytics.class, "mTrackers");
        assertEquals(trackersField.size(), 2);
        AppEventsLogger appEventsLoggerField = Whitebox.getInternalState(Analytics.class, "facebookLogger");
        assertNotEquals(appEventsLoggerField, null);
    }

    @Test
    public void deleteAppTrackersTest() throws Exception {
        prepareMockedFields();
        Analytics.prepareTrackersAndFbLogger(testShop, mockContext);

        // Check size before deletion
        HashMap<String, Tracker> trackersField = Whitebox.getInternalState(Analytics.class, "mTrackers");
        assertEquals(trackersField.size(), 2);

        // Tested method invocation
        Analytics.deleteAppTrackers();

        // Verify final size
        trackersField = Whitebox.getInternalState(Analytics.class, "mTrackers");
        assertEquals(trackersField.size(), 1);
    }

    @Test
    public void setCampaignUriStringTest() throws Exception {
        String testCampaignUri = "www.site.com&utm_medium=email&utm_source=Newsletter";
        doNothing().when(Analytics.class, "sendCampaignInfo");

        // Check before set
        String campaignUriField = Whitebox.getInternalState(Analytics.class, "campaignUri");
        assertEquals(campaignUriField, null);

        Analytics.setCampaignUriString(testCampaignUri);

        verifyPrivate(Analytics.class, never()).invoke("sendCampaignInfo");
        campaignUriField = Whitebox.getInternalState(Analytics.class, "campaignUri");
        assertEquals(campaignUriField, testCampaignUri);
    }

    @Test
    public void setCampaignUriStringTest2() throws Exception {
        prepareMockedFields();
        Analytics.prepareTrackersAndFbLogger(testShop, mockContext);

        String testCampaignUri = "www.site.com&utm_medium=email&utm_source=Newsletter";

        // Check before set
        String campaignUriField = Whitebox.getInternalState(Analytics.class, "campaignUri");
        assertEquals(campaignUriField, null);

        Analytics.setCampaignUriString(testCampaignUri);

        // Verify values
        campaignUriField = Whitebox.getInternalState(Analytics.class, "campaignUri");
        assertEquals(campaignUriField, testCampaignUri);
        verifyPrivate(Analytics.class, times(2)).invoke("sendCampaignInfo");

        // Verify corresponding tracker calls
        verify(mockTracker, times(1)).send(anyMap());
        verify(mockTrackerApp, times(1)).send(anyMap());
    }

    @Test
    public void logProductViewTest() throws Exception {
        prepareMockedFields();
        Analytics.prepareTrackersAndFbLogger(testShop, mockContext);

        long testRemoteId = 123456;
        String testName = "test product";

        Analytics.logProductView(testRemoteId, testName);

        verify(mockAppEventsLogger, times(1)).logEvent((String) notNull(), (Bundle) notNull());
        verify(mockTracker, times(1)).send((Map<String, String>) notNull());
        verify(mockTrackerApp, times(1)).send((Map<String, String>) notNull());
    }

    @Test
    public void logAddProductToCartTest() throws Exception {
        prepareMockedFields();
        Analytics.prepareTrackersAndFbLogger(testShop, mockContext);

        long testRemoteId = 123456;
        String testName = "test product";
        double testDiscountPrice = 52.35;

        Analytics.logAddProductToCart(testRemoteId, testName, testDiscountPrice);

        verify(mockAppEventsLogger, times(1)).logEvent((String) notNull(), anyDouble(), (Bundle) notNull());
        verify(mockTracker, times(1)).send((Map<String, String>) notNull());
        verify(mockTrackerApp, times(1)).send((Map<String, String>) notNull());
    }

    @Test
    public void logShopChangeTest() throws Exception {
        prepareMockedFields();
        Analytics.prepareTrackersAndFbLogger(testShop, mockContext);

        Shop testShop1 = new Shop("shop1", null);
        Shop testShop2 = new Shop("shop2", null);

        Analytics.logShopChange(testShop1, testShop2);

        verify(mockAppEventsLogger, times(1)).logEvent((String) notNull(), (Bundle) notNull());
        verify(mockTracker, times(1)).send((Map<String, String>) notNull());
        verify(mockTrackerApp, times(1)).send((Map<String, String>) notNull());
    }

    @Test
    public void logOpenedByNotificationTest() throws Exception {
        prepareMockedFields();
        Analytics.prepareTrackersAndFbLogger(testShop, mockContext);

        Analytics.logOpenedByNotification("testContent");

        PowerMockito.verifyNoMoreInteractions(mockAppEventsLogger);
        verify(mockTracker, times(1)).send((Map<String, String>) notNull());
        verify(mockTrackerApp, times(1)).send((Map<String, String>) notNull());
    }

    @Test
    public void logCategoryViewBadFormatTest() throws Exception {
        prepareMockedFields();
        Analytics.prepareTrackersAndFbLogger(testShop, mockContext);

        String testName = "test product";

        Analytics.logCategoryView(0, testName, false);

        PowerMockito.verifyNoMoreInteractions(mockAppEventsLogger);
        verify(mockTracker, never()).send((Map<String, String>) notNull());
        verify(mockTrackerApp, never()).send((Map<String, String>) notNull());
    }

    @Test
    public void logCategoryViewOkTest() throws Exception {
        prepareMockedFields();
        Analytics.prepareTrackersAndFbLogger(testShop, mockContext);

        String testName = "test product";

        Analytics.logCategoryView(123, testName, false);

        verify(mockAppEventsLogger, times(1)).logEvent((String) notNull(), (Bundle) notNull());
        verify(mockTracker, times(1)).send((Map<String, String>) notNull());
        verify(mockTrackerApp, times(1)).send((Map<String, String>) notNull());
    }

    @Test
    public void logOrderCreatedEventTest() throws Exception {
        prepareMockedFields();
        Analytics.prepareTrackersAndFbLogger(testShop, mockContext);

        PowerMockito.mockStatic(SettingsMy.class);
        doReturn(testShop).when(SettingsMy.class, "getActualNonNullShop", any());

        // Prepare data
        Shipping testShipping = new Shipping();
        testShipping.setPrice(100);
        Cart testCart = new Cart();
        testCart.setCurrency("EUR");

        List<CartProductItem> testCartProductItems = new ArrayList<>();
        CartProductItem cartProductItem1 = new CartProductItem();
        cartProductItem1.setId(111);
        cartProductItem1.setQuantity(2);
        CartProductItemVariant cartProductItemVariant1 = new CartProductItemVariant();
        cartProductItemVariant1.setName("variant1");
        cartProductItemVariant1.setPrice(100);
        cartProductItemVariant1.setDiscountPrice(50);
        cartProductItemVariant1.setRemoteId(1111);
        cartProductItemVariant1.setCategory(11);
        cartProductItem1.setVariant(cartProductItemVariant1);
        testCartProductItems.add(cartProductItem1);

        CartProductItem cartProductItem2 = new CartProductItem();
        cartProductItem2.setId(112);
        cartProductItem2.setQuantity(2);
        CartProductItemVariant cartProductItemVariant2 = new CartProductItemVariant();
        cartProductItemVariant2.setName("variant2");
        cartProductItemVariant2.setPrice(150);
        cartProductItemVariant2.setDiscountPrice(0);
        cartProductItemVariant2.setRemoteId(1122);
        cartProductItemVariant2.setCategory(11);
        cartProductItem2.setVariant(cartProductItemVariant2);
        testCartProductItems.add(cartProductItem2);

        testCart.setItems(testCartProductItems);

        // Execute
        Analytics.logOrderCreatedEvent(testCart, "123456", (double) 500, testShipping);

        // Verify
        verify(mockAppEventsLogger, times(4)).logEvent((String) notNull(), anyDouble(), (Bundle) notNull());
        verify(mockTracker, times(4)).send((Map<String, String>) notNull());
        verify(mockTrackerApp, times(4)).send((Map<String, String>) notNull());
    }

}
