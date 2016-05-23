package bf.io.openshop;

public class CONST {

    // TODO update this variable
    /**
     * Specific organization ID, received by successful integration.
     */
    public static final int ORGANIZATION_ID = 4;
    /**
     * ID used for simulate empty/null value
     */
    public static final int DEFAULT_EMPTY_ID = -131;

    // Volley requests tags
    public static final String SPLASH_REQUESTS_TAG = "splash_requests";
    public static final String DRAWER_REQUESTS_TAG = "drawer_requests";
    public static final String BANNER_REQUESTS_TAG = "banner_requests";
    public static final String CATEGORY_REQUESTS_TAG = "category_requests";
    public static final String PRODUCT_REQUESTS_TAG = "product_requests";
    public static final String LOGIN_DIALOG_REQUESTS_TAG = "login_dialog_requests";
    public static final String ACCOUNT_REQUESTS_TAG = "account_requests";
    public static final String CART_REQUESTS_TAG = "cart_requests";
    public static final String CART_DISCOUNTS_REQUESTS_TAG = "cart_discounts_requests";
    public static final String ORDER_CREATE_REQUESTS_TAG = "order_create_requests";
    public static final String DELIVERY_DIALOG_REQUESTS_TAG = "delivery_dialog_requests";
    public static final String WISHLIST_REQUESTS_TAG = "wishlist_requests";
    public static final String ACCOUNT_EDIT_REQUESTS_TAG = "account_edit_requests";
    public static final String SETTINGS_REQUESTS_TAG = "settings_requests";
    public static final String UPDATE_CART_ITEM_REQUESTS_TAG = "update_cart_item_requests";
    public static final String MAIN_ACTIVITY_REQUESTS_TAG = "main_activity_requests";
    public static final String PAGE_REQUESTS_TAG = "page_requests";
    public static final String ORDERS_HISTORY_REQUESTS_TAG = "orders_history_requests";
    public static final String ORDERS_DETAIL_REQUESTS_TAG = "orders_detail_requests";

    // Bundle constants
    public static final String BUNDLE_PASS_TARGET = "target";
    public static final String BUNDLE_PASS_TITLE = "title";
    /**
     * Volley request unknown status code
     */
    public static final int MissingStatusCode = 9999;

    /**
     * Possible visibility states of layout parts.
     */
    public enum VISIBLE {
        EMPTY, CONTENT, PROGRESS
    }
}
