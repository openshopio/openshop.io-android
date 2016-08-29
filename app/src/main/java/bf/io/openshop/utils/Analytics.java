package bf.io.openshop.utils;

import android.content.Context;
import android.os.Bundle;

import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import bf.io.openshop.MyApplication;
import bf.io.openshop.R;
import bf.io.openshop.SettingsMy;
import bf.io.openshop.entities.Shop;
import bf.io.openshop.entities.cart.Cart;
import bf.io.openshop.entities.cart.CartProductItem;
import bf.io.openshop.entities.delivery.Shipping;
import timber.log.Timber;

public class Analytics {

    private static final String TRACKER_GLOBAL = "Global";
    private static final String TRACKER_APP = "App";
    public static final String PRODUCT = "product";
    public static final String POST_ORDER = "POST_ORDER";
    private static HashMap<String, Tracker> mTrackers = new HashMap<>();

    private static AppEventsLogger facebookLogger;
    private static String campaignUri;

    private Analytics() {}

    /**
     * Prepare Google analytics trackers and Facebook events logger.
     * Send UTM campaign if exist.
     *
     * @param shop    shop with app specific Google Ua or null, if global tracker is enough.
     * @param context application context.
     */
    public static synchronized void prepareTrackersAndFbLogger(Shop shop, Context context) {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG

        if (shop == null) {
            deleteAppTrackers();
        } else {
            if (!mTrackers.containsKey(TRACKER_APP) && analytics != null) {
                if (shop.getGoogleUa() != null && !shop.getGoogleUa().isEmpty()) {
                    Timber.d("Set new app tracker with id: %s", shop.getGoogleUa());
                    // App tracker determined by shop
                    Tracker appTracker = analytics.newTracker(shop.getGoogleUa());
                    appTracker.enableAutoActivityTracking(true);
                    appTracker.enableExceptionReporting(false);
                    appTracker.enableAdvertisingIdCollection(true);
                    mTrackers.put(TRACKER_APP, appTracker);
                } else {
                    Timber.e(new RuntimeException(), "Creating GA app tracker with empty Google UA");
                }
            } else {
                Timber.e("Trackers for this app already exist.");
            }
        }

        // Add global tracker only one time.
        if (!mTrackers.containsKey(TRACKER_GLOBAL) && analytics != null) {
            Timber.d("Set new global tracker.");
            // Global app tracker
            Tracker appTrackerGlobal = analytics.newTracker(R.xml.global_tracker);
            appTrackerGlobal.enableAutoActivityTracking(true);
            appTrackerGlobal.enableExceptionReporting(true);
            appTrackerGlobal.enableAdvertisingIdCollection(true);
            mTrackers.put(TRACKER_GLOBAL, appTrackerGlobal);
            // Send camping info only once time.
            sendCampaignInfo();
        }

        facebookLogger = AppEventsLogger.newLogger(MyApplication.getInstance());
    }

    /**
     * @return content of campaignUri private field.
     */
    public static String getCampaignUri(){
        return campaignUri;
    }

    /**
     * Method delete shop specific tracker if exist.
     */
    public static void deleteAppTrackers() {
        if (mTrackers != null && mTrackers.containsKey(TRACKER_APP)) {
            Timber.d("Removing GA app tracker.");
            mTrackers.remove(TRACKER_APP);
        }
    }

    private static void logFbEvent(String appEventConst, Double price, Bundle parameters) {
        if (facebookLogger != null) {
            if (parameters == null)
                facebookLogger.logEvent(appEventConst);
            else {
                if (price == null)
                    facebookLogger.logEvent(appEventConst, parameters);
                else
                    facebookLogger.logEvent(appEventConst, price, parameters);
            }
        } else {
            Timber.e(new RuntimeException(), "null FB facebookLogger");
        }
    }

    private static void sendEventToAppTrackers(Map<String, String> event) {
        if (mTrackers == null || mTrackers.isEmpty()) {
            Timber.e(new RuntimeException(), "SendEventToAppTrackers, ERROR empty app trackers set");
        } else {
            Set<String> keys = mTrackers.keySet();
            if (keys.contains(TRACKER_GLOBAL)) {
                Timber.d("Send event to GA global: %s", event.toString());
                mTrackers.get(TRACKER_GLOBAL).send(event);
            }
            if (keys.contains(TRACKER_APP)) {
                Timber.d("Send event to GA app: %s", event.toString());
                mTrackers.get(TRACKER_APP).send(event);
            }
        }
    }


    /**
     * Method sets new UTM campaign.
     * If analytics trackers exist, method sends events with UTM.
     * If analytics trackers doesn't exist, event with UTM will be send when they are created.
     *
     * @param campaignUriString UTM string.
     */
    public static synchronized void setCampaignUriString(String campaignUriString) {
        Timber.d("Set campaign uri: %s", campaignUriString);
        campaignUri = campaignUriString;
        if (mTrackers != null && !mTrackers.isEmpty()) {
            sendCampaignInfo();
        }
    }

    private static synchronized void sendCampaignInfo() {
        if (campaignUri != null && !campaignUri.isEmpty()) {
            try {
                Timber.d("Sending campaign uri.");
                if (mTrackers.isEmpty())
                    Timber.e("Empty app trackers set");
                else {
                    Set<String> keys = mTrackers.keySet();
                    for (String key : keys) {
                        Tracker t = mTrackers.get(key);
                        t.setScreenName("OpenShop");

                        Map<String, String> hit = new HitBuilders.ScreenViewBuilder()
                                .setCampaignParamsFromUrl(campaignUri)
                                .build();

                        Timber.e("Send campaign: %s", hit.toString());
                        // Campaign data sent with this hit.
                        t.send(hit);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Timber.e(e, "Send campaign info exception.");
            }
        } else {
            Timber.e("Campaign uri is null");
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////
    ////////////// Custom logging methods. //////////////////////////////////////////////

    /**
     * Method sends product view event to defined Analytics.
     *
     * @param remoteId remote id of the viewed product.
     * @param name     name of the viewed product.
     */
    public static void logProductView(long remoteId, String name) {
        // FB event log
        Bundle parameters = new Bundle();
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, PRODUCT);
        parameters.putLong(AppEventsConstants.EVENT_PARAM_CONTENT_ID, remoteId);
        parameters.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, name);
        logFbEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, null, parameters);

        // GA event log
        Map<String, String> event = new HitBuilders.EventBuilder()
                .setCategory(PRODUCT)
                .setAction("view")
                .setLabel("product with id: " + remoteId + ", name: " + name)
                .build();
        sendEventToAppTrackers(event);
    }

    /**
     * Method sends "product add to cart" event to defined Analytics.
     *
     * @param remoteId       remote id of the viewed product.
     * @param name           name of the viewed product.
     * @param discountPrice product price.
     */
    public static void logAddProductToCart(long remoteId, String name, double discountPrice) {
        // FB facebookLogger
        Bundle parameters = new Bundle();
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, PRODUCT);
        parameters.putLong(AppEventsConstants.EVENT_PARAM_CONTENT_ID, remoteId);
        parameters.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, name);
        logFbEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, discountPrice, parameters);

        // Ga
        Map<String, String> event = new HitBuilders.EventBuilder()
                .setCategory("ADDED_TO_CART")
                .setAction("ADDED_TO_CART")
                .setLabel("ADDED TO CART" + " product id: " + remoteId + " product name: " + name + " price: " + discountPrice)
                .build();
        sendEventToAppTrackers(event);
    }

    /**
     * Method sends "user changed shop" event to defined Analytics.
     *
     * @param actualNonNullShop active shop before change.
     * @param newShopSelected   active shop after change.
     */
    public static void logShopChange(Shop actualNonNullShop, Shop newShopSelected) {
        if (actualNonNullShop != null && newShopSelected != null) {
            String description = "From (id=" + actualNonNullShop.getId() + ",name=" + actualNonNullShop.getName() +
                    ") to (id=" + newShopSelected.getId() + ",name=" + newShopSelected.getId() + ")";
            // FB facebookLogger
            Bundle parameters = new Bundle();
            parameters.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, description);
            logFbEvent(AppEventsConstants.EVENT_NAME_UNLOCKED_ACHIEVEMENT, null, parameters);

            // Ga
            Map<String, String> event = new HitBuilders.EventBuilder()
                    .setCategory("CHANGE_SHOP")
                    .setAction("CHANGE_SHOP")
                    .setLabel(description)
                    .build();
            sendEventToAppTrackers(event);
        } else {
            Timber.e(new RuntimeException(), "Try log shop change with null parameters");
        }
    }

    /**
     * Method sends "app opened by notification" event to Google Analytics.
     *
     * @param target specific notification data.
     */
    public static void logOpenedByNotification(String target) {
        // Ga
        Map<String, String> event = new HitBuilders.EventBuilder()
                .setAction("OPENED_BY_NOTIFICATION")
                .setLabel("OPENED_BY_NOTIFICATION with link:" + target + ".")
                .build();
        sendEventToAppTrackers(event);
    }

    /**
     * Method sends "order created" event to Google Analytics.
     *
     * @param orderCart        ordered cart content.
     * @param orderRemoteId    remote order id.
     * @param orderTotalPrice  total order price.
     * @param selectedShipping selected shipping to log its price.
     */
    public static void logOrderCreatedEvent(Cart orderCart, String orderRemoteId, Double orderTotalPrice, Shipping selectedShipping) {
        //GA
        Map<String, String> eventPostOrder = new HitBuilders.EventBuilder()
                .setCategory(POST_ORDER)
                .setAction(POST_ORDER)
                .setLabel(POST_ORDER)
                .build();
        sendEventToAppTrackers(eventPostOrder);

        // Send GA whole cart
        Map<String, String> event = new HitBuilders.TransactionBuilder()
                .setTransactionId(orderRemoteId)
                .setAffiliation(SettingsMy.getActualNonNullShop(null).getName())
                .setRevenue(orderTotalPrice)
                .setTax(0.0)
                .setShipping(selectedShipping.getPrice())
                .setCurrencyCode(orderCart.getCurrency())
                .build();
        sendEventToAppTrackers(event);

        // Fb event whole cart
        Bundle parametersCheckout = new Bundle();
        parametersCheckout.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "cart");
        parametersCheckout.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, orderRemoteId);
        parametersCheckout.putInt(AppEventsConstants.EVENT_PARAM_NUM_ITEMS, orderCart.getItems().size());  // Unique products/events
        parametersCheckout.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, orderCart.getCurrency());
        logFbEvent(AppEventsConstants.EVENT_NAME_INITIATED_CHECKOUT, orderTotalPrice, parametersCheckout);

        // Fb event shipping
        Bundle parametersShip = new Bundle();
        parametersShip.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "shipping");
        parametersShip.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, orderRemoteId);
        parametersShip.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, orderCart.getCurrency());
        logFbEvent(AppEventsConstants.EVENT_NAME_PURCHASED, (double) selectedShipping.getPrice(), parametersShip);

        // Send single products in cart to GA and FB
        for (int i = 0; i < orderCart.getItems().size(); i++) {
            CartProductItem item = orderCart.getItems().get(i);

            Double price = item.getVariant().getPrice();
            if (item.getVariant().getDiscountPrice() > 0) {
                price = item.getVariant().getDiscountPrice();
            }
            Map<String, String> eventSingle = new HitBuilders.ItemBuilder()
                    .setTransactionId(orderRemoteId)
                    .setName(item.getVariant().getName())
                    .setSku("Product id: " + item.getVariant().getRemoteId())
                    .setCategory("Category id: " + item.getVariant().getCategory())
                    .setPrice(price)
                    .setQuantity(item.getQuantity())
                    .setCurrencyCode(orderCart.getCurrency())
                    .build();
            sendEventToAppTrackers(eventSingle);

            // Fb events purchased
            Bundle parameters = new Bundle();
            parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, PRODUCT);
            parameters.putLong(AppEventsConstants.EVENT_PARAM_CONTENT_ID, item.getVariant().getRemoteId());
            parameters.putInt(AppEventsConstants.EVENT_PARAM_NUM_ITEMS, item.getQuantity());
            parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, orderCart.getCurrency());
            logFbEvent(AppEventsConstants.EVENT_NAME_PURCHASED, price * item.getQuantity(), parameters);
        }
    }

    /**
     * Method sends "category view" event to Google Analytics.
     *
     * @param categoryId   id category for logging.
     * @param categoryName category name for logging.
     * @param isSearch     determine if normal category or search category.
     */
    public static void logCategoryView(long categoryId, String categoryName, boolean isSearch) {
        Bundle parameters = new Bundle();
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "category");

        if (categoryId == 0) {
            Timber.e("Is categoryId = 0.");
        } else {
            parameters.putLong(AppEventsConstants.EVENT_PARAM_CONTENT_ID, categoryId);
            parameters.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, categoryName);
            logFbEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, null, parameters);

            Map<String, String> event = new HitBuilders.EventBuilder()
                    .setCategory("VIEW_CATEGORY")
                    .setAction(isSearch ? "SEARCH" : "VIEW_CATEGORY")
                    .setLabel(isSearch ? "Search: " + categoryName : "CategoryId: " + categoryId + ". CategoryName: " + categoryName)
                    .build();

            sendEventToAppTrackers(event);
        }
    }

    ////////////// end of custom logging methods. ///////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////

}
