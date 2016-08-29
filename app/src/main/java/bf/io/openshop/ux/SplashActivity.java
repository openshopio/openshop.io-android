/*******************************************************************************
 * Copyright (C) 2016 Business Factory, s.r.o.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package bf.io.openshop.ux;

import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.applinks.AppLinkData;

import java.util.List;
import java.util.Locale;

import bf.io.openshop.CONST;
import bf.io.openshop.MyApplication;
import bf.io.openshop.R;
import bf.io.openshop.SettingsMy;
import bf.io.openshop.api.EndPoints;
import bf.io.openshop.api.GsonRequest;
import bf.io.openshop.entities.Shop;
import bf.io.openshop.entities.ShopResponse;
import bf.io.openshop.testing.EspressoIdlingResource;
import bf.io.openshop.utils.Analytics;
import bf.io.openshop.utils.MsgUtils;
import bf.io.openshop.utils.Utils;
import bf.io.openshop.ux.adapters.ShopSpinnerAdapter;
import bf.io.openshop.ux.dialogs.LoginDialogFragment;
import timber.log.Timber;

/**
 * Initial activity. Handle install referrers, notifications and shop selection;
 * <p>
 * Created by Petr Melicherik.
 */
public class SplashActivity extends AppCompatActivity {
    public static final String REFERRER = "referrer";
    private static final String TAG = SplashActivity.class.getSimpleName();

    private Activity activity;
    private ProgressDialog progressDialog;

    /**
     * Indicates if layout has been already created.
     */
    private boolean layoutCreated = false;

    /**
     * Spinner offering all available shops.
     */
    private Spinner shopSelectionSpinner;

    /**
     * Button allowing selection of shop during fresh start.
     */
    private Button continueToShopBtn;

    /**
     * Indicates that window has been already detached.
     */
    private boolean windowDetached = false;

    // Possible layouts
    private View layoutIntroScreen;
    private View layoutContent;
    private View layoutContentNoConnection;
    private View layoutContentSelectShop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(TAG);
        activity = this;

        // init loading dialog
        progressDialog = Utils.generateProgressDialog(this, false);

        init();
    }

    /**
     * Prepares activity view and handles incoming intent(Notification, utm data).
     */

    private void init() {
        // Check if data connected.
        if (!MyApplication.getInstance().isDataConnected()) {
            progressDialog.hide();
            Timber.d("No network connection.");

            initSplashLayout();

            // Skip intro screen.
            layoutContent.setVisibility(View.VISIBLE);
            layoutIntroScreen.setVisibility(View.GONE);

            // Show retry button.
            layoutContentNoConnection.setVisibility(View.VISIBLE);
            layoutContentSelectShop.setVisibility(View.GONE);
        } else {
            progressDialog.hide();

            // Google Install referrer is handled by CampaignTrackingService and CampaignTrackingReceiver defined in Manifest.
            // Referrer is sent with first event.

            // Search for analytics data. General GA Campaign, and Facebook app links (if app links implemented on server side too).
            Intent intent = this.getIntent();
            if (intent != null) {
                Uri uri = intent.getData();
                if (uri != null && uri.isHierarchical() && (uri.getQueryParameter("utm_source") != null || uri.getQueryParameter(REFERRER) != null)) {
                    // GA General Campaign & Traffic Source Attribution. Save camping data.
                    // https://developers.google.com/analytics/devguides/collection/android/v3/campaigns
                    Timber.d("UTM source detected. - General Campaign & Traffic Source Attribution.");
                    if (uri.getQueryParameter("utm_source") != null) {
                        Analytics.setCampaignUriString(uri.toString());
                    } else if (uri.getQueryParameter(REFERRER) != null) {
                        Analytics.setCampaignUriString(uri.getQueryParameter(REFERRER));
                    }
                } else if (intent.getExtras() != null) {
                    // FB app link. For function needs server side implementation also. https://developers.facebook.com/docs/applinks
                    Timber.d("Extra bundle detected.");
                    try {
                        Bundle bundleApplinkData = getIntent().getExtras();
                        if (bundleApplinkData != null) {
                            Bundle applinkData = bundleApplinkData.getBundle("al_applink_data");
                            if (applinkData != null) {
                                String targetUrl = applinkData.getString("target_url");
                                if (targetUrl != null && !targetUrl.isEmpty()) {
                                    Timber.d("TargetUrl: %s", targetUrl);
                                    Analytics.setCampaignUriString(targetUrl);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Timber.e(e, "Parsing FB deepLink exception");
                    }
                } else {
                    // FB deferred app link. For function needs server side implementation also. https://developers.facebook.com/docs/applinks
                    try {
                        AppLinkData.fetchDeferredAppLinkData(this, new AppLinkData.CompletionHandler() {
                            @Override
                            public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                                try {
                                    if (appLinkData != null) {
                                        String targetUrl = appLinkData.getTargetUri().toString();
                                        if (targetUrl != null && !targetUrl.isEmpty()) {
                                            Timber.e("TargetUrl: %s", targetUrl);
                                            Analytics.setCampaignUriString(targetUrl);
                                        }
                                    }
                                } catch (Exception e) {
                                    Timber.e(e, "AppLinkData exception");
                                }
                            }
                        });
                    } catch (Exception e) {
                        Timber.e(e, "Fetch deferredAppLinkData  exception");
                    }
                }
            }

            // If opened by notification. Try load shop defined by notification data. If error, just start shop with last used shop.
            if (this.getIntent() != null && this.getIntent().getExtras() != null && this.getIntent().getExtras().getString(EndPoints.NOTIFICATION_LINK) != null) {
                Timber.d("Run by notification.");
                String type = this.getIntent().getExtras().getString(EndPoints.NOTIFICATION_LINK, "");
                final String title = this.getIntent().getExtras().getString(EndPoints.NOTIFICATION_TITLE, "");
                try {
                    String[] linkParams = type.split(":");
                    if (linkParams.length != 3) {
                        Timber.e("Bad notification format. NotifyType: %s", type);
                        throw new Exception("Bad notification format. NotifyType:" + type);
                    } else {
                        final String target = linkParams[1] + ":" + linkParams[2];
                        int shopId = Integer.parseInt(linkParams[0]);
                        String url = String.format(EndPoints.SHOPS_SINGLE, shopId);
                        Analytics.setCampaignUriString(this.getIntent().getExtras().getString(EndPoints.NOTIFICATION_UTM, ""));

                        progressDialog.show();
                        GsonRequest<Shop> req = new GsonRequest<>(Request.Method.GET, url, null, Shop.class, new Response.Listener<Shop>() {
                            @Override
                            public void onResponse(Shop shop) {
                                progressDialog.cancel();
                                Bundle bundle = new Bundle();
                                bundle.putString(CONST.BUNDLE_PASS_TARGET, target);
                                bundle.putString(CONST.BUNDLE_PASS_TITLE, title);

                                // Logout user if shop changed
                                Shop actualShop = SettingsMy.getActualShop();
                                if (actualShop != null && shop.getId() != actualShop.getId())
                                    LoginDialogFragment.logoutUser();

                                setShopInformationAndStartMainActivity(shop, bundle);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.cancel();
                                MsgUtils.logErrorMessage(error);
                                startMainActivity(null);
                            }
                        });
                        req.setRetryPolicy(MyApplication.getDefaultRetryPolice());
                        req.setShouldCache(false);
                        MyApplication.getInstance().addToRequestQueue(req, CONST.SPLASH_REQUESTS_TAG);
                    }
                } catch (Exception e) {
                    Timber.e(e, "Skip Splash activity after notification error.");
                    startMainActivity(null);
                }
            } else {
                // Nothing special. try continue to MainActivity.
                Timber.d("Nothing special.");
                startMainActivity(null);
            }
        }
    }

    /**
     * Save selected/received shop, and try continue to MainActivity.
     *
     * @param shop   selected shop for persist.
     * @param bundle notification specific data.
     */
    private void setShopInformationAndStartMainActivity(Shop shop, Bundle bundle) {
        // Save selected shop
        SettingsMy.setActualShop(shop);
        startMainActivity(bundle);
    }

    /**
     * SetContentView to activity and prepare layout views.
     */
    private void initSplashLayout() {
        if (!layoutCreated) {
            setContentView(R.layout.activity_splash);

            layoutContent = findViewById(R.id.splash_content);
            layoutIntroScreen = findViewById(R.id.splash_intro_screen);
            layoutContentNoConnection = findViewById(R.id.splash_content_no_connection);
            layoutContentSelectShop = findViewById(R.id.splash_content_select_shop);

            shopSelectionSpinner = (Spinner) findViewById(R.id.splash_shop_selection_spinner);
            continueToShopBtn = (Button) findViewById(R.id.splash_continue_to_shop_btn);
            continueToShopBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Shop selectedShop = (Shop) shopSelectionSpinner.getSelectedItem();
                    if (selectedShop != null && selectedShop.getId() != CONST.DEFAULT_EMPTY_ID)
                        setShopInformationAndStartMainActivity(selectedShop, null);
                    else
                        Timber.e("Cannot continue. Shop is not selected or is null.");
                }
            });
            Button reRunButton = (Button) findViewById(R.id.splash_re_run_btn);
            if (reRunButton != null) {
                reRunButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressDialog.show();
                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                init();
                            }
                        }, 600);
                    }
                });
            } else {
                Timber.e(new RuntimeException(), "ReRunButton didn't found");
            }
            layoutCreated = true;
        } else {
            Timber.d("%s screen is already created.", this.getClass().getSimpleName());
        }
    }

    /**
     * Check if shop is selected. If so then start {@link MainActivity}. If no then show form with selection.
     *
     * @param bundle notification specific data.
     */
    private void startMainActivity(Bundle bundle) {
        if (SettingsMy.getActualShop() == null) {
            // First run, allow user choose desired shop.
            Timber.d("Missing active shop. Show shop selection.");
            initSplashLayout();
            layoutContentNoConnection.setVisibility(View.GONE);
            layoutContentSelectShop.setVisibility(View.VISIBLE);
            requestShops();
        } else {
            Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
            if (bundle != null) {
                Timber.d("Pass bundle to main activity");
                mainIntent.putExtras(bundle);
            }
            startActivity(mainIntent);
            finish();
        }
    }

    /**
     * Load available shops from server.
     */
    private void requestShops() {
        if (layoutIntroScreen.getVisibility() != View.VISIBLE)
            progressDialog.show();
        GsonRequest<ShopResponse> getShopsRequest = new GsonRequest<>(Request.Method.GET, EndPoints.SHOPS, null, ShopResponse.class,
                new Response.Listener<ShopResponse>() {
                    @Override
                    public void onResponse(@NonNull ShopResponse response) {
                        Timber.d("Get shops response: %s", response.toString());
                        setSpinShops(response.getShopList());
                        if (progressDialog != null) progressDialog.cancel();
                        animateContentVisible();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog != null) progressDialog.cancel();
                MsgUtils.logAndShowErrorMessage(activity, error);
                finish();
            }
        });
        getShopsRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        getShopsRequest.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(getShopsRequest, CONST.SPLASH_REQUESTS_TAG);
    }

    /**
     * Prepare spinner with shops and pre-select the most appropriate.
     *
     * @param shopList list of shops received from server.
     */
    private void setSpinShops(List<Shop> shopList) {
        if (shopList != null && !shopList.isEmpty()) {
            // preset shop selection title.
            Shop defaultEmptyValue = new Shop();
            defaultEmptyValue.setId(CONST.DEFAULT_EMPTY_ID);
            defaultEmptyValue.setName(getString(R.string.Select_shop));
            shopList.add(0, defaultEmptyValue);

            ShopSpinnerAdapter shopSpinnerAdapter = new ShopSpinnerAdapter(this, shopList, true);
            shopSelectionSpinner.setAdapter(shopSpinnerAdapter);
            shopSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Shop item = (Shop) parent.getItemAtPosition(position);
                    if (item.getId() == CONST.DEFAULT_EMPTY_ID)
                        continueToShopBtn.setVisibility(View.INVISIBLE);
                    else {
                        continueToShopBtn.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Timber.d("No shop selected.");
                }
            });

            // pre-select shop if only 1 exist
            if (shopList.size() == 2) {
                shopSelectionSpinner.setSelection(1);
                Timber.d("Only one shop exist.");
            } else {
                // pre-select shop based on language
                String defaultLanguage = Locale.getDefault().getLanguage();
                Timber.d("Default language: %s", defaultLanguage);
                long tempShopId = 0; // DEFAULT no language

                // Find corresponding shop and language
                for (int i = 0; i < shopList.size(); i++) {
                    if (shopList.get(i).getLanguage() != null && shopList.get(i).getLanguage().equalsIgnoreCase(defaultLanguage)) {
                        tempShopId = shopList.get(i).getId();
                        break;
                    }
                }

                // Select founded shop
                for (int i = 0; i < shopList.size(); i++) {
                    if (shopList.get(i).getId() == tempShopId) {
                        Timber.d("Preselect language position: %s", i);
                        shopSelectionSpinner.setSelection(i);
                        break;
                    }
                }
            }
        } else {
            Timber.e(new RuntimeException(), "Trying to set empty shops array.");
        }
    }

    /**
     * Hide intro screen and display content layout with animation.
     */
    private void animateContentVisible() {
        if (layoutIntroScreen != null && layoutContent != null && layoutIntroScreen.getVisibility() == View.VISIBLE) {
            EspressoIdlingResource.increment();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (windowDetached) {
                                if (layoutContent != null) layoutContent.setVisibility(View.VISIBLE);
                            } else {
//                            // If lollipop use reveal animation. On older phones use fade animation.
                                if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                                    Timber.d("Circular animation.");
                                    // get the center for the animation circle
                                    final int cx = (layoutContent.getLeft() + layoutContent.getRight()) / 2;
                                    final int cy = (layoutContent.getTop() + layoutContent.getBottom()) / 2;

                                    // get the final radius for the animation circle
                                    int dx = Math.max(cx, layoutContent.getWidth() - cx);
                                    int dy = Math.max(cy, layoutContent.getHeight() - cy);
                                    float finalRadius = (float) Math.hypot(dx, dy);

                                    Animator animator = ViewAnimationUtils.createCircularReveal(layoutContent, cx, cy, 0, finalRadius);
                                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                                    animator.setDuration(1250);
                                    layoutContent.setVisibility(View.VISIBLE);
                                    animator.start();
                                } else {
                                    Timber.d("Alpha animation.");
                                    layoutContent.setAlpha(0f);
                                    layoutContent.setVisibility(View.VISIBLE);
                                    layoutContent.animate()
                                            .alpha(1f)
                                            .setDuration(1000)
                                            .setListener(null);
                                }
                            }
                            EspressoIdlingResource.decrement();
                        }
                    }, 330);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onStop() {
        if (progressDialog != null) progressDialog.cancel();
        if (layoutIntroScreen != null) layoutIntroScreen.setVisibility(View.GONE);
        if (layoutContent != null) layoutContent.setVisibility(View.VISIBLE);
        MyApplication.getInstance().cancelPendingRequests(CONST.SPLASH_REQUESTS_TAG);
        super.onStop();
    }

    @Override
    public void onAttachedToWindow() {
        windowDetached = false;
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        windowDetached = true;
        super.onDetachedFromWindow();
    }
}
