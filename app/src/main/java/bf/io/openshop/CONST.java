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
    public static final String splash_requests_tag = "splash_requests";
    public static final String drawer_requests_tag = "drawer_requests";
    public static final String banner_requests_tag = "banner_requests";
    public static final String category_requests_tag = "category_requests";
    public static final String product_requests_tag = "product_requests";
    public static final String login_dialog_requests_tag = "login_dialog_requests";
    public static final String account_requests_tag = "account_requests";
    public static final String cart_requests_tag = "cart_requests";
    public static final String cart_discounts_requests_tag = "cart_discounts_requests";
    public static final String order_create_requests_tag = "order_create_requests";
    public static final String delivery_dialog_requests_tag = "delivery_dialog_requests";
    public static final String wishlist_requests_tag = "wishlist_requests";
    public static final String account_edit_requests_tag = "account_edit_requests";
    public static final String settings_requests_tag = "settings_requests";
    public static final String update_cart_item_requests_tag = "update_cart_item_requests";
    public static final String main_activity_requests_tag = "main_activity_requests";
    public static final String page_requests_tag = "page_requests";
    public static final String orders_history_requests_tag = "orders_history_requests";
    public static final String orders_detail_requests_tag = "orders_detail_requests";

    // Bundle constants
    public static final String BUNDLE_PASS_TARGET = "target";
    public static final String BUNDLE_PASS_TITLE = "title";
    /**
     * Volley request unknown status code
     */
    public static int MissingStatusCode = 9999;

    /**
     * Possible visibility states of layout parts.
     */
    public enum VISIBLE {
        EMPTY, CONTENT, PROGRESS
    }
}
