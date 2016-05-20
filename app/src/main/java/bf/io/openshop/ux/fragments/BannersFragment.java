package bf.io.openshop.ux.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import bf.io.openshop.CONST;
import bf.io.openshop.MyApplication;
import bf.io.openshop.R;
import bf.io.openshop.SettingsMy;
import bf.io.openshop.api.EndPoints;
import bf.io.openshop.api.GsonRequest;
import bf.io.openshop.entities.Banner;
import bf.io.openshop.entities.BannersResponse;
import bf.io.openshop.entities.Metadata;
import bf.io.openshop.interfaces.BannersRecyclerInterface;
import bf.io.openshop.listeners.OnSingleClickListener;
import bf.io.openshop.utils.EndlessRecyclerScrollListener;
import bf.io.openshop.utils.MsgUtils;
import bf.io.openshop.utils.Utils;
import bf.io.openshop.ux.MainActivity;
import bf.io.openshop.ux.adapters.BannersRecyclerAdapter;
import timber.log.Timber;

/**
 * Provides "welcome" screen customizable from web administration. Often contains banners with sales or best products.
 */
public class BannersFragment extends Fragment {

    private ProgressDialog progressDialog;

    // content related fields.
    private RecyclerView bannersRecycler;
    private BannersRecyclerAdapter bannersRecyclerAdapter;
    private EndlessRecyclerScrollListener endlessRecyclerScrollListener;
    private Metadata bannersMetadata;

    /**
     * Indicates if user data should be loaded from server or from memory.
     */
    private boolean mAlreadyLoaded = false;

    /**
     * Holds reference for empty view. Show to user when no data loaded.
     */
    private View emptyContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - OnCreateView", this.getClass().getSimpleName());
        MainActivity.setActionBarTitle(getString(R.string.Just_arrived));

        View view = inflater.inflate(R.layout.fragment_banners, container, false);

        progressDialog = Utils.generateProgressDialog(getActivity(), false);

        prepareEmptyContent(view);
        // Don't reload data when return from backStack. Reload if a new instance was created or data was empty.
        if ((savedInstanceState == null && !mAlreadyLoaded) || bannersRecyclerAdapter == null || bannersRecyclerAdapter.isEmpty()) {
            Timber.d("Reloading banners.");
            mAlreadyLoaded = true;

            // Prepare views and listeners
            prepareContentViews(view, true);
            loadBanners(null);
        } else {
            Timber.d("Banners already loaded.");
            prepareContentViews(view, false);
            // Already loaded
        }

        return view;
    }

    /**
     * Prepares views and listeners associated with content.
     *
     * @param view       fragment root view.
     * @param freshStart indicates when everything should be recreated.
     */
    private void prepareContentViews(View view, boolean freshStart) {
        bannersRecycler = (RecyclerView) view.findViewById(R.id.banners_recycler);
        if (freshStart) {
            bannersRecyclerAdapter = new BannersRecyclerAdapter(getActivity(), new BannersRecyclerInterface() {
                @Override
                public void onBannerSelected(Banner banner) {
                    Activity activity = getActivity();
                    if (activity instanceof MainActivity) {
                        ((MainActivity) activity).onBannerSelected(banner);
                    }
                }
            });
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(bannersRecycler.getContext());
        bannersRecycler.setLayoutManager(layoutManager);
        bannersRecycler.setItemAnimator(new DefaultItemAnimator());
        bannersRecycler.setHasFixedSize(true);
        bannersRecycler.setAdapter(bannersRecyclerAdapter);
        endlessRecyclerScrollListener = new EndlessRecyclerScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                if (bannersMetadata != null && bannersMetadata.getLinks() != null && bannersMetadata.getLinks().getNext() != null) {
                    loadBanners(bannersMetadata.getLinks().getNext());
                } else {
                    Timber.d("CustomLoadMoreDataFromApi NO MORE DATA");
                }
            }
        };
        bannersRecycler.addOnScrollListener(endlessRecyclerScrollListener);
    }

    /**
     * Prepares views and listeners associated with empty content. Visible only when no content loads.
     *
     * @param view fragment root view.
     */
    private void prepareEmptyContent(View view) {
        emptyContent = view.findViewById(R.id.banners_empty);
        TextView emptyContentAction = (TextView) view.findViewById(R.id.banners_empty_action);
        emptyContentAction.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                // Just open drawer menu.
                Activity activity = getActivity();
                if (activity instanceof MainActivity) {
                    MainActivity mainActivity = (MainActivity) activity;
                    if (mainActivity.drawerFragment != null)
                        mainActivity.drawerFragment.toggleDrawerMenu();
                }
            }
        });
    }

    /**
     * Endless content loader. Should be used after views inflated.
     *
     * @param url null for fresh load. Otherwise use URLs from response metadata.
     */
    private void loadBanners(String url) {
        progressDialog.show();
        if (url == null) {
            bannersRecyclerAdapter.clear();
            url = String.format(EndPoints.BANNERS, SettingsMy.getActualNonNullShop(getActivity()).getId());
        }
        GsonRequest<BannersResponse> getBannersRequest = new GsonRequest<>(Request.Method.GET, url, null, BannersResponse.class,
                new Response.Listener<BannersResponse>() {
                    @Override
                    public void onResponse(@NonNull BannersResponse response) {
                        Timber.d("response: %s", response.toString());
                        bannersMetadata = response.getMetadata();
                        bannersRecyclerAdapter.addBanners(response.getRecords());

                        if (bannersRecyclerAdapter.getItemCount() > 0) {
                            emptyContent.setVisibility(View.INVISIBLE);
                            bannersRecycler.setVisibility(View.VISIBLE);
                        } else {
                            emptyContent.setVisibility(View.VISIBLE);
                            bannersRecycler.setVisibility(View.INVISIBLE);
                        }

                        progressDialog.cancel();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog != null) progressDialog.cancel();
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
            }
        });
        getBannersRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        getBannersRequest.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(getBannersRequest, CONST.BANNER_REQUESTS_TAG);
    }

    @Override
    public void onStop() {
        if (progressDialog != null) {
            // Hide progress dialog if exist.
            if (progressDialog.isShowing() && endlessRecyclerScrollListener != null) {
                // Fragment stopped during loading data. Allow new loading on return.
                endlessRecyclerScrollListener.resetLoading();
            }
            progressDialog.cancel();
        }
        MyApplication.getInstance().cancelPendingRequests(CONST.BANNER_REQUESTS_TAG);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        if (bannersRecycler != null) bannersRecycler.clearOnScrollListeners();
        super.onDestroyView();
    }
}
