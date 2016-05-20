package bf.io.openshop.ux.fragments;


import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.List;

import bf.io.openshop.CONST;
import bf.io.openshop.MyApplication;
import bf.io.openshop.R;
import bf.io.openshop.SettingsMy;
import bf.io.openshop.api.EndPoints;
import bf.io.openshop.api.GsonRequest;
import bf.io.openshop.entities.drawerMenu.DrawerItemCategory;
import bf.io.openshop.entities.drawerMenu.DrawerItemPage;
import bf.io.openshop.entities.drawerMenu.DrawerResponse;
import bf.io.openshop.interfaces.DrawerRecyclerInterface;
import bf.io.openshop.interfaces.DrawerSubmenuRecyclerInterface;
import bf.io.openshop.utils.MsgUtils;
import bf.io.openshop.ux.adapters.DrawerRecyclerAdapter;
import bf.io.openshop.ux.adapters.DrawerSubmenuRecyclerAdapter;
import timber.log.Timber;

/**
 * Fragment handles the drawer menu.
 */
public class DrawerFragment extends Fragment {

    private static final int BANNERS_ID = -123;
    public static final String NULL_DRAWER_LISTENER_WTF = "Null drawer listener. WTF.";

    private ProgressBar drawerProgress;

    /**
     * Button to reload drawer menu content (used when content failed to load).
     */
    private Button drawerRetryBtn;
    /**
     * Indicates that menu is currently loading.
     */
    private boolean drawerLoading = false;

    /**
     * Listener indicating events that occurred on the menu.
     */
    private FragmentDrawerListener drawerListener;

    private ActionBarDrawerToggle mDrawerToggle;

    // Drawer top menu fields.
    private DrawerLayout mDrawerLayout;
    private RecyclerView drawerRecycler;
    private DrawerRecyclerAdapter drawerRecyclerAdapter;

    // Drawer sub menu fields
    private LinearLayout drawerSubmenuLayout;
    private TextView drawerSubmenuTitle;
    private DrawerSubmenuRecyclerAdapter drawerSubmenuRecyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - onCreateView", this.getClass().getSimpleName());
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_drawer, container, false);

        drawerSubmenuLayout = (LinearLayout) layout.findViewById(R.id.drawer_submenu_layout);
        drawerSubmenuTitle = (TextView) layout.findViewById(R.id.drawer_submenu_title);
        drawerProgress = (ProgressBar) layout.findViewById(R.id.drawer_progress);

        drawerRetryBtn = (Button) layout.findViewById(R.id.drawer_retry_btn);
        drawerRetryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawerLoading)
                    getDrawerItems();
            }
        });

        prepareDrawerRecycler(layout);

        Button backBtn = (Button) layout.findViewById(R.id.drawer_submenu_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            private long mLastClickTime = 0;

            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000)
                    return;
                mLastClickTime = SystemClock.elapsedRealtime();

                animateSubListHide();
            }
        });

        getDrawerItems();

        return layout;
    }

    /**
     * Prepare drawer menu content views, adapters and listeners.
     *
     * @param view fragment base view.
     */
    private void prepareDrawerRecycler(View view) {
        drawerRecycler = (RecyclerView) view.findViewById(R.id.drawer_recycler);
        drawerRecyclerAdapter = new DrawerRecyclerAdapter(getContext(), new DrawerRecyclerInterface() {
            @Override
            public void onCategorySelected(View v, DrawerItemCategory drawerItemCategory) {
                if (drawerItemCategory.getChildren() == null || drawerItemCategory.getChildren().isEmpty()) {
                    if (drawerListener != null) {
                        if (drawerItemCategory.getId() == BANNERS_ID)
                            drawerListener.onDrawerBannersSelected();
                        else
                            drawerListener.onDrawerItemCategorySelected(drawerItemCategory);
                        closeDrawerMenu();
                    } else {
                        Timber.e(new RuntimeException(), NULL_DRAWER_LISTENER_WTF);
                    }
                } else
                    animateSubListShow(drawerItemCategory);
            }

            @Override
            public void onPageSelected(View v, DrawerItemPage drawerItemPage) {
                if (drawerListener != null) {
                    drawerListener.onDrawerItemPageSelected(drawerItemPage);
                    closeDrawerMenu();
                } else {
                    Timber.e(new RuntimeException(), NULL_DRAWER_LISTENER_WTF);
                }
            }

            @Override
            public void onHeaderSelected() {
                if (drawerListener != null) {
                    drawerListener.onAccountSelected();
                    closeDrawerMenu();
                } else {
                    Timber.e(new RuntimeException(), NULL_DRAWER_LISTENER_WTF);
                }
            }
        });
        drawerRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        drawerRecycler.setHasFixedSize(true);
        drawerRecycler.setAdapter(drawerRecyclerAdapter);

        RecyclerView drawerSubmenuRecycler = (RecyclerView) view.findViewById(R.id.drawer_submenu_recycler);
        drawerSubmenuRecyclerAdapter = new DrawerSubmenuRecyclerAdapter(new DrawerSubmenuRecyclerInterface() {
            @Override
            public void onSubCategorySelected(View v, DrawerItemCategory drawerItemCategory) {
                if (drawerListener != null) {
                    drawerListener.onDrawerItemCategorySelected(drawerItemCategory);
                    closeDrawerMenu();
                }
            }
        });
        drawerSubmenuRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        drawerSubmenuRecycler.setItemAnimator(new DefaultItemAnimator());
        drawerSubmenuRecycler.setHasFixedSize(true);
        drawerSubmenuRecycler.setAdapter(drawerSubmenuRecyclerAdapter);
    }

    /**
     * Base method for layout preparation. Also set a listener that will respond to events that occurred on the menu.
     *
     * @param drawerLayout   drawer layout, which will be managed.
     * @param toolbar        toolbar bundled with a side menu.
     * @param eventsListener corresponding listener class.
     */
    public void setUp(DrawerLayout drawerLayout, final Toolbar toolbar, FragmentDrawerListener eventsListener) {
        mDrawerLayout = drawerLayout;
        this.drawerListener = eventsListener;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.content_description_open_navigation_drawer, R.string.content_description_close_navigation_drawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
//                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDrawerMenu();
            }
        });

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    /**
     * When the drawer menu is open, close it. Otherwise open it.
     */
    public void toggleDrawerMenu() {
        if (mDrawerLayout != null) {
            if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        }
    }

    /**
     * When the drawer menu is open, close it.
     */
    public void closeDrawerMenu() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    /**
     * Check if drawer is open. If so close it.
     *
     * @return false if drawer was already closed
     */
    public boolean onBackHide() {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
            if (drawerSubmenuLayout.getVisibility() == View.VISIBLE)
                animateSubListHide();
            else
                mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

    /**
     * Method invalidates a drawer menu header. It is used primarily on a login state change.
     */
    public void invalidateHeader() {
        if (drawerRecyclerAdapter != null) {
            Timber.d("Invalidate drawer menu header.");
            drawerRecyclerAdapter.notifyItemChanged(0);
        }
    }

    private void getDrawerItems() {
        drawerLoading = true;
        drawerProgress.setVisibility(View.VISIBLE);
        drawerRetryBtn.setVisibility(View.GONE);

        String url = String.format(EndPoints.NAVIGATION_DRAWER, SettingsMy.getActualNonNullShop(getActivity()).getId());
        GsonRequest<DrawerResponse> getDrawerMenu = new GsonRequest<>(Request.Method.GET, url, null, DrawerResponse.class, new Response.Listener<DrawerResponse>() {
            @Override
            public void onResponse(@NonNull DrawerResponse drawerResponse) {
                drawerRecyclerAdapter.addDrawerItem(new DrawerItemCategory(BANNERS_ID, BANNERS_ID, getString(R.string.Just_arrived)));
                drawerRecyclerAdapter.addDrawerItemList(drawerResponse.getNavigation());
                drawerRecyclerAdapter.addPageItemList(drawerResponse.getPages());
                drawerRecyclerAdapter.notifyDataSetChanged();

                if (drawerListener != null)
                    drawerListener.prepareSearchSuggestions(drawerResponse.getNavigation());

                drawerLoading = false;
                if (drawerRecycler != null) drawerRecycler.setVisibility(View.VISIBLE);
                if (drawerProgress != null) drawerProgress.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
                drawerLoading = false;
                if (drawerProgress != null) drawerProgress.setVisibility(View.GONE);
                if (drawerRetryBtn != null) drawerRetryBtn.setVisibility(View.VISIBLE);
            }
        });
        getDrawerMenu.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        getDrawerMenu.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(getDrawerMenu, CONST.DRAWER_REQUESTS_TAG);
    }

    private void animateSubListHide() {
        Animation slideAwayDisappear = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_away_disappear);
        final Animation slideAwayAppear = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_away_appear);
        slideAwayDisappear.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                drawerRecycler.setVisibility(View.VISIBLE);
                drawerRecycler.startAnimation(slideAwayAppear);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                drawerSubmenuLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        drawerSubmenuLayout.startAnimation(slideAwayDisappear);
    }

    private void animateSubListShow(DrawerItemCategory drawerItemCategory) {
        if (drawerItemCategory != null) {
            drawerSubmenuTitle.setText(drawerItemCategory.getName());
            drawerSubmenuRecyclerAdapter.changeDrawerItems(drawerItemCategory.getChildren());
            Animation slideInDisappear = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_disappear);
            final Animation slideInAppear = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_appear);
            slideInDisappear.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    drawerSubmenuLayout.setVisibility(View.VISIBLE);
                    drawerSubmenuLayout.startAnimation(slideInAppear);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    drawerRecycler.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            drawerRecycler.startAnimation(slideInDisappear);
        } else {
            Timber.e("Populate submenu with null category drawer item.");
        }
    }

    @Override
    public void onPause() {
        // Cancellation during onPause is needed because of app restarting during changing shop.
        MyApplication.getInstance().cancelPendingRequests(CONST.DRAWER_REQUESTS_TAG);
        if (drawerLoading) {
            if (drawerProgress != null) drawerProgress.setVisibility(View.GONE);
            if (drawerRetryBtn != null) drawerRetryBtn.setVisibility(View.VISIBLE);
            drawerLoading = false;
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mDrawerLayout.removeDrawerListener(mDrawerToggle);
        super.onDestroy();
    }

    /**
     * Interface defining events initiated by {@link DrawerFragment}.
     */
    public interface FragmentDrawerListener {

        /**
         * Launch {@link BannersFragment}. If fragment is already launched nothing happen.
         */
        void onDrawerBannersSelected();

        /**
         * Launch {@link CategoryFragment}.
         *
         * @param drawerItemCategory object specifying selected item in the drawer.
         */
        void onDrawerItemCategorySelected(DrawerItemCategory drawerItemCategory);

        /**
         * Launch {@link PageFragment}, with downloadable content.
         *
         * @param drawerItemPage id of page for download and display. (Define in OpenShop server administration)
         */
        void onDrawerItemPageSelected(DrawerItemPage drawerItemPage);

        /**
         * Launch {@link AccountFragment}.
         */
        void onAccountSelected();

        /**
         * Prepare all search strings for search whisperer.
         *
         * @param navigation items for suggestions generating.
         */
        void prepareSearchSuggestions(List<DrawerItemCategory> navigation);
    }
}
