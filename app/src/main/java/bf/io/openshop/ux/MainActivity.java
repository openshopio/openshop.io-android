/*******************************************************************************
 * Copyright (C) 2016 Business Factory, s.r.o.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package bf.io.openshop.ux;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.MatrixCursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import bf.io.openshop.BuildConfig;
import bf.io.openshop.CONST;
import bf.io.openshop.MyApplication;
import bf.io.openshop.R;
import bf.io.openshop.SettingsMy;
import bf.io.openshop.api.EndPoints;
import bf.io.openshop.api.GsonRequest;
import bf.io.openshop.api.JsonRequest;
import bf.io.openshop.entities.Banner;
import bf.io.openshop.entities.User;
import bf.io.openshop.entities.cart.CartInfo;
import bf.io.openshop.entities.drawerMenu.DrawerItemCategory;
import bf.io.openshop.entities.drawerMenu.DrawerItemPage;
import bf.io.openshop.entities.order.Order;
import bf.io.openshop.interfaces.LoginDialogInterface;
import bf.io.openshop.utils.Analytics;
import bf.io.openshop.utils.JsonUtils;
import bf.io.openshop.utils.MsgUtils;
import bf.io.openshop.utils.MyRegistrationIntentService;
import bf.io.openshop.utils.Utils;
import bf.io.openshop.ux.dialogs.LoginDialogFragment;
import bf.io.openshop.ux.fragments.AccountEditFragment;
import bf.io.openshop.ux.fragments.AccountFragment;
import bf.io.openshop.ux.fragments.BannersFragment;
import bf.io.openshop.ux.fragments.CartFragment;
import bf.io.openshop.ux.fragments.CategoryFragment;
import bf.io.openshop.ux.fragments.DrawerFragment;
import bf.io.openshop.ux.fragments.OrderCreateFragment;
import bf.io.openshop.ux.fragments.OrderFragment;
import bf.io.openshop.ux.fragments.OrdersHistoryFragment;
import bf.io.openshop.ux.fragments.PageFragment;
import bf.io.openshop.ux.fragments.ProductFragment;
import bf.io.openshop.ux.fragments.SettingsFragment;
import bf.io.openshop.ux.fragments.WishlistFragment;
import timber.log.Timber;

/**
 * Application is based on one core activity, which handles fragment operations.
 */
public class MainActivity extends AppCompatActivity implements DrawerFragment.FragmentDrawerListener {

    public static final String MSG_MAIN_ACTIVITY_INSTANCE_IS_NULL = "MainActivity instance is null.";
    private static MainActivity mInstance = null;

    /**
     * Reference tied drawer menu, represented as fragment.
     */
    public DrawerFragment drawerFragment;
    /**
     * Indicate that app will be closed on next back press
     */
    private boolean isAppReadyToFinish = false;
    /**
     * Reference view showing number of products in shopping cart.
     */
    private TextView cartCountView;
    /**
     * Reference number of products in shopping cart.
     */
    private int cartCountNotificationValue = CONST.DEFAULT_EMPTY_ID;

    /**
     * BroadcastReceiver used in service for Gcm registration.
     */
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    // Fields used in searchView.
    private SimpleCursorAdapter searchSuggestionsAdapter;
    private ArrayList<String> searchSuggestionsList;

    /**
     * Refresh notification number of products in shopping cart.
     * Create action only if called from fragment attached to MainActivity.
     */
    public static void updateCartCountNotification() {
        MainActivity instance = MainActivity.getInstance();
        if (instance != null) {
            instance.getCartCount(false);
        } else {
            Timber.e(MSG_MAIN_ACTIVITY_INSTANCE_IS_NULL);
        }
    }

    /**
     * Update actionBar title.
     * Create action only if called from fragment attached to MainActivity.
     */
    public static void setActionBarTitle(String title) {
        MainActivity instance = MainActivity.getInstance();
        if (instance != null) {
            // TODO want different toolbar text font?
//            SpannableString s = new SpannableString(title);
//            s.setSpan(new TypefaceSpan("sans-serif-light"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            instance.setTitle(s);
            instance.setTitle(title);
        } else {
            Timber.e(MSG_MAIN_ACTIVITY_INSTANCE_IS_NULL);
        }
    }

    /**
     * Method checks if MainActivity instance exist. If so, then drawer menu header will be invalidated.
     */
    public static void invalidateDrawerMenuHeader() {
        MainActivity instance = MainActivity.getInstance();
        if (instance != null && instance.drawerFragment != null) {
            instance.drawerFragment.invalidateHeader();
        } else {
            Timber.e(MSG_MAIN_ACTIVITY_INSTANCE_IS_NULL);
        }
    }

    /**
     * Return MainActivity instance. Null if activity doesn't exist.
     *
     * @return activity instance.
     */
    private static synchronized MainActivity getInstance() {
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance = this;

        Timber.d("%s onCreate", MainActivity.class.getSimpleName());

        // Set app specific language localization by selected shop.
        String lang = SettingsMy.getActualNonNullShop(this).getLanguage();
        MyApplication.setAppLocale(lang);

        setContentView(R.layout.activity_main);

//        if (BuildConfig.DEBUG) {
//            // Only debug properties, used for checking image memory management.
//            Picasso.with(this).setIndicatorsEnabled(true);
//            Picasso.with(this).setLoggingEnabled(true);
//        }

        // Initialize trackers and fbLogger
        Analytics.prepareTrackersAndFbLogger(SettingsMy.getActualNonNullShop(this), getApplicationContext());

        // Prepare toolbar and navigation drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
        } else {
            Timber.e(new RuntimeException(), "GetSupportActionBar returned null.");
        }
        drawerFragment = (DrawerFragment) getSupportFragmentManager().findFragmentById(R.id.main_navigation_drawer_fragment);
        drawerFragment.setUp((DrawerLayout) findViewById(R.id.main_drawer_layout), toolbar, this);

        // Initialize list for search suggestions
        searchSuggestionsList = new ArrayList<>();

        // GCM registration //
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean sentToken = SettingsMy.getTokenSentToServer();
                if (sentToken) {
                    Timber.d("Gcm registration success.");
                } else {
                    Timber.e("Gcm registration failed. Device isn't registered on server.");
                }
            }
        };
        registerGcmOnServer();
        // end of GCM registration //

        addInitialFragment();

        // Opened by notification with some data
        if (this.getIntent() != null && this.getIntent().getExtras() != null) {
            String target = this.getIntent().getExtras().getString(CONST.BUNDLE_PASS_TARGET, "");
            String title = this.getIntent().getExtras().getString(CONST.BUNDLE_PASS_TITLE, "");
            Timber.d("Start notification with banner target: %s", target);

            Banner banner = new Banner();
            banner.setTarget(target);
            banner.setName(title);
            onBannerSelected(banner);

            Analytics.logOpenedByNotification(target);
        }
    }

    /**
     * Run service for Gcm token generation and registering device on servers.
     * Registration is needed for notification messages.
     */
    public void registerGcmOnServer() {
        if (Utils.checkPlayServices(this)) {
            Intent intent = new Intent(this, MyRegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Prepare search view
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            prepareSearchView(searchItem);
        }

        // Prepare cart count info
        MenuItem cartItem = menu.findItem(R.id.action_cart);
        MenuItemCompat.setActionView(cartItem, R.layout.action_icon_shopping_cart);
        View view = MenuItemCompat.getActionView(cartItem);
        cartCountView = (TextView) view.findViewById(R.id.shopping_cart_notify);
        showNotifyCount(cartCountNotificationValue);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCartSelected();
            }
        });
        if (cartCountNotificationValue == CONST.DEFAULT_EMPTY_ID) {
            // If first cart count check, then sync server cart data.
            getCartCount(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Loads cart count from server.
     *
     * @param initialize if true, then server run cart synchronization . Useful during app starts.
     */
    private void getCartCount(boolean initialize) {
        Timber.d("Obtaining cart count.");
        if (cartCountView != null) {
            User user = SettingsMy.getActiveUser();
            if (user == null) {
                Timber.d("Cannot update notify count. User is logged out.");
                showNotifyCount(0);
            } else {
                // If cart count is loaded for the first time, we need to load whole cart because of synchronization.
                if (initialize) {
                    String url = String.format(EndPoints.CART, SettingsMy.getActualNonNullShop(this).getId());
                    JsonRequest req = new JsonRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Timber.d("getCartCount: %s", response.toString());
                            try {
                                showNotifyCount(response.getInt(JsonUtils.TAG_PRODUCT_COUNT));
                            } catch (Exception e) {
                                Timber.e(e, "Obtain cart count from response failed.");
                                showNotifyCount(0);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            MsgUtils.logErrorMessage(error);
                            showNotifyCount(0);
                        }
                    }, getSupportFragmentManager(), user.getAccessToken());
                    req.setRetryPolicy(MyApplication.getDefaultRetryPolice());
                    req.setShouldCache(false);
                    MyApplication.getInstance().addToRequestQueue(req, CONST.MAIN_ACTIVITY_REQUESTS_TAG);
                } else {
                    String url = String.format(EndPoints.CART_INFO, SettingsMy.getActualNonNullShop(this).getId());
                    GsonRequest<CartInfo> req = new GsonRequest<>(Request.Method.GET, url, null, CartInfo.class, new Response.Listener<CartInfo>() {
                        @Override
                        public void onResponse(CartInfo response) {
                            Timber.d("getCartCount: %s", response.toString());
                            showNotifyCount(response.getProductCount());
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            MsgUtils.logErrorMessage(error);
                            showNotifyCount(0);
                        }
                    }, getSupportFragmentManager(), user.getAccessToken());
                    req.setRetryPolicy(MyApplication.getDefaultRetryPolice());
                    req.setShouldCache(false);
                    MyApplication.getInstance().addToRequestQueue(req, CONST.MAIN_ACTIVITY_REQUESTS_TAG);
                }
            }
        }
    }

    /**
     * Method display cart count notification. Cart count notification remains hide if cart count is negative number.
     *
     * @param newCartCount cart count to show.
     */
    private void showNotifyCount(int newCartCount) {
        cartCountNotificationValue = newCartCount;
        Timber.d("Update cart count notification: %d", cartCountNotificationValue);
        if (cartCountView != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (cartCountNotificationValue != 0 && cartCountNotificationValue != CONST.DEFAULT_EMPTY_ID) {
                        cartCountView.setText(getString(R.string.format_number, cartCountNotificationValue));
                        cartCountView.setVisibility(View.VISIBLE);
                    } else {
                        cartCountView.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            Timber.e("Cannot update cart count notification. Cart count view is null.");
        }
    }

    /**
     * Prepare toolbar search view. Invoke search suggestions and handle search queries.
     *
     * @param searchItem corresponding menu item.
     */
    private void prepareSearchView(@NonNull final MenuItem searchItem) {
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSubmitButtonEnabled(true);
        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                Timber.d("Search query text changed to: %s", newText);
                showSearchSuggestions(newText, searchView);
                return false;
            }

            public boolean onQueryTextSubmit(String query) {
                // Submit search query and hide search action view.
                onSearchSubmitted(query);
                searchView.setQuery("", false);
                searchView.setIconified(true);
                searchItem.collapseActionView();
                return true;
            }
        };

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                // Submit search suggestion query and hide search action view.
                MatrixCursor c = (MatrixCursor) searchSuggestionsAdapter.getItem(position);
                onSearchSubmitted(c.getString(1));
                searchView.setQuery("", false);
                searchView.setIconified(true);
                searchItem.collapseActionView();
                return true;
            }
        });
        searchView.setOnQueryTextListener(queryTextListener);
    }

    /**
     * Show user search whisperer with generated suggestions.
     *
     * @param query      actual search query
     * @param searchView corresponding search action view.
     */
    private void showSearchSuggestions(String query, SearchView searchView) {
        if (searchSuggestionsAdapter != null && searchSuggestionsList != null) {
            Timber.d("Populate search adapter - mySuggestions.size(): %d", searchSuggestionsList.size());
            final MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, "categories"});
            for (int i = 0; i < searchSuggestionsList.size(); i++) {
                if (searchSuggestionsList.get(i) != null && searchSuggestionsList.get(i).toLowerCase().startsWith(query.toLowerCase()))
                    c.addRow(new Object[]{i, searchSuggestionsList.get(i)});
            }
            searchView.setSuggestionsAdapter(searchSuggestionsAdapter);
            searchSuggestionsAdapter.changeCursor(c);
        } else {
            Timber.e("Search adapter is null or search data suggestions missing");
        }
    }

    @Override
    public void prepareSearchSuggestions(List<DrawerItemCategory> navigation) {
        final String[] from = new String[]{"categories"};
        final int[] to = new int[]{android.R.id.text1};

        searchSuggestionsAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,
                null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        if (navigation != null && !navigation.isEmpty()) {
            for (int i = 0; i < navigation.size(); i++) {
                if (!searchSuggestionsList.contains(navigation.get(i).getName())) {
                    searchSuggestionsList.add(navigation.get(i).getName());
                }

                if (navigation.get(i).hasChildren()) {
                    for (int j = 0; j < navigation.get(i).getChildren().size(); j++) {
                        if (!searchSuggestionsList.contains(navigation.get(i).getChildren().get(j).getName())) {
                            searchSuggestionsList.add(navigation.get(i).getChildren().get(j).getName());
                        }
                    }
                }
            }
            searchSuggestionsAdapter.notifyDataSetChanged();
        } else {
            Timber.e("Search suggestions loading failed.");
            searchSuggestionsAdapter = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_wish_list) {
            onWishlistSelected();
            return true;
        } else if (id == R.id.action_cart) {
            onCartSelected();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Add first fragment to the activity. This fragment will be attached to the bottom of the fragments stack.
     * When fragment stack is cleared {@link #clearBackStack}, this fragment will be shown.
     */
    private void addInitialFragment() {
        Fragment fragment = new BannersFragment();
        FragmentManager frgManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = frgManager.beginTransaction();
        fragmentTransaction.add(R.id.main_content_frame, fragment).commit();
        frgManager.executePendingTransactions();
    }

    /**
     * Method creates fragment transaction and replace current fragment with new one.
     *
     * @param newFragment    new fragment used for replacement.
     * @param transactionTag text identifying fragment transaction.
     */
    private void replaceFragment(Fragment newFragment, String transactionTag) {
        if (newFragment != null) {
            FragmentManager frgManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = frgManager.beginTransaction();
            fragmentTransaction.setAllowOptimization(false);
            fragmentTransaction.addToBackStack(transactionTag);
            fragmentTransaction.replace(R.id.main_content_frame, newFragment).commit();
            frgManager.executePendingTransactions();
        } else {
            Timber.e(new RuntimeException(), "Replace fragments with null newFragment parameter.");
        }
    }

    /**
     * Method clear fragment backStack (back history). On bottom of stack will remain Fragment added by {@link #addInitialFragment()}.
     */
    private void clearBackStack() {
        Timber.d("Clearing backStack");
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            if (BuildConfig.DEBUG) {
                for (int i = 0; i < manager.getBackStackEntryCount(); i++) {
                    Timber.d("BackStack content_%d= id: %d, name: %s", i, manager.getBackStackEntryAt(i).getId(), manager.getBackStackEntryAt(i).getName());
                }
            }
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStackImmediate(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        Timber.d("backStack cleared.");
//        TODO maybe implement own fragment backStack handling to prevent banner fragment recreation during clearing.
//        http://stackoverflow.com/questions/12529499/problems-with-android-fragment-back-stack
    }

    /**
     * Method create new {@link CategoryFragment} with defined search query.
     *
     * @param searchQuery text used for products search.
     */
    private void onSearchSubmitted(String searchQuery) {
        clearBackStack();
        Timber.d("Called onSearchSubmitted with text: %s", searchQuery);
        Fragment fragment = CategoryFragment.newInstance(searchQuery);
        replaceFragment(fragment, CategoryFragment.class.getSimpleName());
    }

    @Override
    public void onDrawerBannersSelected() {
        clearBackStack();
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.main_content_frame);
        if (f == null || !(f instanceof BannersFragment)) {
            Fragment fragment = new BannersFragment();
            replaceFragment(fragment, BannersFragment.class.getSimpleName());
        } else {
            Timber.d("Banners already displayed.");
        }
    }

    @Override
    public void onDrawerItemCategorySelected(DrawerItemCategory drawerItemCategory) {
        clearBackStack();
        Fragment fragment = CategoryFragment.newInstance(drawerItemCategory);
        replaceFragment(fragment, CategoryFragment.class.getSimpleName());
    }

    @Override
    public void onDrawerItemPageSelected(DrawerItemPage drawerItemPage) {
        clearBackStack();
        Fragment fragment = PageFragment.newInstance(drawerItemPage.getId());
        replaceFragment(fragment, PageFragment.class.getSimpleName());
    }

    @Override
    public void onAccountSelected() {
        AccountFragment fragment = new AccountFragment();
        replaceFragment(fragment, AccountFragment.class.getSimpleName());
    }

    /**
     * Launch {@link PageFragment} with default values. It leads to load terms and conditions defined on server.
     */
    public void onTermsAndConditionsSelected() {
        Fragment fragment = PageFragment.newInstance();
        replaceFragment(fragment, PageFragment.class.getSimpleName());
    }

    /**
     * Method parse selected banner and launch corresponding fragment.
     * If banner type is 'list' then launch {@link CategoryFragment}.
     * If banner type is 'detail' then launch {@link ProductFragment}.
     *
     * @param banner selected banner for display.
     */
    public void onBannerSelected(Banner banner) {
        if (banner != null) {
            String target = banner.getTarget();
            Timber.d("Open banner with target: %s", target);
            String[] targetParams = target.split(":");
            if (targetParams.length >= 2) {
                switch (targetParams[0]) {
                    case "list": {
                        Fragment fragment = CategoryFragment.newInstance(Long.parseLong(targetParams[1]), banner.getName(), null);
                        replaceFragment(fragment, CategoryFragment.class.getSimpleName() + " - banner");
                        break;
                    }
                    case "detail": {
                        Fragment fragment = ProductFragment.newInstance(Long.parseLong(targetParams[1]));
                        replaceFragment(fragment, ProductFragment.class.getSimpleName() + " - banner select");
                        break;
                    }
                    default:
                        Timber.e("Unknown banner target type.");
                        break;
                }
            } else {
                Timber.e(new RuntimeException(), "Parsed banner target has too less parameters.");
            }
        } else {
            Timber.e("onBannerSelected called with null parameters.");
        }
    }

    /**
     * Launch {@link ProductFragment}.
     *
     * @param productId id of product for display.
     */
    public void onProductSelected(long productId) {
        Fragment fragment = ProductFragment.newInstance(productId);
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            fragment.setReturnTransition(TransitionInflater.from(this).inflateTransition(android.R.transition.fade));
        }
        replaceFragment(fragment, ProductFragment.class.getSimpleName());
    }

    /**
     * Launch {@link SettingsFragment}.
     */
    public void startSettingsFragment() {
        Fragment fragment = new SettingsFragment();
        replaceFragment(fragment, SettingsFragment.class.getSimpleName());
    }

    /**
     * If user is logged in then {@link CartFragment} is launched . Otherwise is showed a login dialog.
     */
    public void onCartSelected() {
        launchUserSpecificFragment(new CartFragment(), CartFragment.class.getSimpleName(), new LoginDialogInterface() {
            @Override
            public void successfulLoginOrRegistration(User user) {
                // If login was successful launch CartFragment.
                onCartSelected();
            }
        });
    }

    /**
     * If user is logged in then {@link WishlistFragment} is launched . Otherwise is showed a login dialog.
     */
    public void onWishlistSelected() {
        launchUserSpecificFragment(new WishlistFragment(), WishlistFragment.class.getSimpleName(), new LoginDialogInterface() {
            @Override
            public void successfulLoginOrRegistration(User user) {
                // If login was successful launch WishlistFragment.
                onWishlistSelected();
            }
        });
    }

    /**
     * If user is logged in then {@link OrderCreateFragment} is launched . Otherwise is showed a login dialog.
     */
    public void onOrderCreateSelected() {
        launchUserSpecificFragment(new OrderCreateFragment(), OrderCreateFragment.class.getSimpleName(), new LoginDialogInterface() {
            @Override
            public void successfulLoginOrRegistration(User user) {
                // If login was successful launch CartFragment.
                onCartSelected();
            }
        });
    }

    /**
     * If user is logged in then {@link AccountEditFragment} is launched . Otherwise is showed a login dialog.
     */
    public void onAccountEditSelected() {
        launchUserSpecificFragment(new AccountEditFragment(), AccountEditFragment.class.getSimpleName(), new LoginDialogInterface() {
            @Override
            public void successfulLoginOrRegistration(User user) {
                // If login was successful launch AccountEditFragment.
                onAccountEditSelected();
            }
        });
    }

    /**
     * If user is logged in then {@link OrdersHistoryFragment} is launched . Otherwise is showed a login dialog.
     */
    public void onOrdersHistory() {
        launchUserSpecificFragment(new OrdersHistoryFragment(), OrdersHistoryFragment.class.getSimpleName(), new LoginDialogInterface() {
            @Override
            public void successfulLoginOrRegistration(User user) {
                // If login was successful launch orderHistoryFragment.
                onOrdersHistory();
            }
        });
    }

    /**
     * Check if user is logged in. If so then start defined fragment, otherwise show login dialog.
     *
     * @param fragment       fragment to launch.
     * @param transactionTag text identifying fragment transaction.
     * @param loginListener  listener on successful login.
     */
    private void launchUserSpecificFragment(Fragment fragment, String transactionTag, LoginDialogInterface loginListener) {
        if (SettingsMy.getActiveUser() != null) {
            replaceFragment(fragment, transactionTag);
        } else {
            DialogFragment loginDialogFragment = LoginDialogFragment.newInstance(loginListener);
            loginDialogFragment.show(getSupportFragmentManager(), LoginDialogFragment.class.getSimpleName());
        }
    }

    /**
     * Launch {@link OrderFragment}.
     *
     * @param order order to show
     */
    public void onOrderSelected(Order order) {
        if (order != null) {
            Fragment fragment = OrderFragment.newInstance(order.getId());
            replaceFragment(fragment, OrderFragment.class.getSimpleName());
        } else {
            Timber.e("Creating order detail with null data.");
        }
    }

    @Override
    public void onBackPressed() {
        // If back button pressed, try close drawer if exist and is open. If drawer is already closed continue.
        if (drawerFragment == null || !drawerFragment.onBackHide()) {
            // If app should be finished or some fragment transaction still remains on backStack, let the system do the job.
            if (getSupportFragmentManager().getBackStackEntryCount() > 0 || isAppReadyToFinish)
                super.onBackPressed();
            else {
                // BackStack is empty. For closing the app user have to tap the back button two times in two seconds.
                MsgUtils.showToast(this, MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Another_click_for_leaving_app), MsgUtils.ToastLength.SHORT);
                isAppReadyToFinish = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isAppReadyToFinish = false;
                    }
                }, 2000);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // FB base events logging
        AppEventsLogger.activateApp(this);

        // GCM registration
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(SettingsMy.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // FB base events logging
        AppEventsLogger.deactivateApp(this);
        MyApplication.getInstance().cancelPendingRequests(CONST.MAIN_ACTIVITY_REQUESTS_TAG);

        // GCM registration
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }
}
