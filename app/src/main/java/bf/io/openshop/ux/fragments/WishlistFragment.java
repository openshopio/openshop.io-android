package bf.io.openshop.ux.fragments;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import bf.io.openshop.CONST;
import bf.io.openshop.MyApplication;
import bf.io.openshop.R;
import bf.io.openshop.SettingsMy;
import bf.io.openshop.api.EndPoints;
import bf.io.openshop.api.GsonRequest;
import bf.io.openshop.api.JsonRequest;
import bf.io.openshop.entities.User;
import bf.io.openshop.entities.wishlist.WishlistItem;
import bf.io.openshop.entities.wishlist.WishlistResponse;
import bf.io.openshop.interfaces.RequestListener;
import bf.io.openshop.interfaces.WishlistInterface;
import bf.io.openshop.utils.JsonUtils;
import bf.io.openshop.utils.MsgUtils;
import bf.io.openshop.utils.RecyclerMarginDecorator;
import bf.io.openshop.utils.Utils;
import bf.io.openshop.ux.MainActivity;
import bf.io.openshop.ux.adapters.WishListRecyclerAdapter;
import timber.log.Timber;

/**
 * Fragment shows user's wishlist. Wishlist is represented by a list of products.
 * Fragment should be created only if user is logged in.
 * Class provides two static operations: remove product from wishlist and add product to wishlist.
 */
public class WishlistFragment extends Fragment {

    private ProgressDialog progressDialog;

    // Fields referencing complex screen layouts.
    private View rootLayout;
    private View emptyLayout;
    private View contentLayout;

    // List related fields
    private WishListRecyclerAdapter wishlistAdapter;
    private GridLayoutManager recyclerLayoutManaged;

    /**
     * Field for recovering scroll position.
     */
    private Parcelable recyclerState;

    /**
     * Method add concrete product to the wishlist.
     * Expected all non-null parameters.
     *
     * @param activity        related activity.
     * @param variantId       id of the product variant which should be added.
     * @param user            related user account.
     * @param requestTag      string identifying concrete request. Useful for request cancellation.
     * @param requestListener listener for operation results.
     */
    public static void addToWishList(final FragmentActivity activity, long variantId, User user, String requestTag, final RequestListener requestListener) {
        if (activity != null && variantId != 0 && user != null && requestTag != null && requestListener != null) {
            JSONObject jo = new JSONObject();
            try {
                jo.put(JsonUtils.TAG_PRODUCT_VARIANT_ID, variantId);
            } catch (Exception e) {
                requestListener.requestFailed(null);
                Timber.e(e, "Add to wishlist null product.");
                return;
            }
            String url = String.format(EndPoints.WISHLIST, SettingsMy.getActualNonNullShop(activity).getId());
            JsonRequest req = new JsonRequest(Request.Method.POST, url, jo, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Timber.d("AddToWishlist response: %s", response.toString());
                    try {
                        final long responseId = response.getLong(JsonUtils.TAG_ID);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                requestListener.requestSuccess(responseId);
                            }
                        }, 500);
                    } catch (Exception e) {
                        Timber.e(e, "Parsing addToWishList response failed. Response: %s", response);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                requestListener.requestFailed(null);
                            }
                        }, 500);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            requestListener.requestFailed(error);
                        }
                    }, 500);
                    MsgUtils.logAndShowErrorMessage(activity, error);
                }
            }, activity.getSupportFragmentManager(), user.getAccessToken());
            req.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            req.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(req, requestTag);
        } else {
            if (requestListener != null) requestListener.requestFailed(null);
            Timber.e(new RuntimeException(), "Add to wishlist product with null parameters.");
        }
    }

    /**
     * Method remove concrete product from the wishlist by wishlistId.
     * Expected all non-null parameters.
     *
     * @param activity        related activity.
     * @param wishlistId      id of the wishlist item representing product.
     * @param user            related user account.
     * @param requestTag      string identifying concrete request. Useful for request cancellation.
     * @param requestListener listener for operation results.
     */
    public static void removeFromWishList(final FragmentActivity activity, long wishlistId, User user, String requestTag, final RequestListener requestListener) {
        if (activity != null && wishlistId != 0 && user != null && requestTag != null && requestListener != null) {
            String url = String.format(EndPoints.WISHLIST_SINGLE, SettingsMy.getActualNonNullShop(activity).getId(), wishlistId);
            JsonRequest req = new JsonRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Timber.d("RemoveFromWishlist response: %s", response.toString());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            requestListener.requestSuccess(0);
                        }
                    }, 500);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(final VolleyError error) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            requestListener.requestFailed(error);
                        }
                    }, 500);
                    MsgUtils.logAndShowErrorMessage(activity, error);
                }
            }, activity.getSupportFragmentManager(), user.getAccessToken());
            req.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            req.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(req, requestTag);
        } else {
            if (requestListener != null) requestListener.requestFailed(null);
            Timber.e(new RuntimeException(), "Remove from wishlist product with null parameters.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - onCreateView", this.getClass().getSimpleName());
        MainActivity.setActionBarTitle(getString(R.string.Wishlist));

        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);

        progressDialog = Utils.generateProgressDialog(getActivity(), false);

        rootLayout = view.findViewById(R.id.wishlist_root);
        emptyLayout = view.findViewById(R.id.wishlist_empty);
        contentLayout = view.findViewById(R.id.wishlist_content);

        User user = SettingsMy.getActiveUser();
        if (user != null) {
            prepareWishlistRecycler(view);
            getWishlistContent(user);
        } else {
            Timber.e(new RuntimeException(), "Wishlist fragment created with no logged user. ");
            getFragmentManager().popBackStackImmediate();
        }
        return view;
    }

    /**
     * Prepare content views, adapters and listeners.
     *
     * @param view fragment base view.
     */
    private void prepareWishlistRecycler(View view) {
        RecyclerView wishlistRecycler = (RecyclerView) view.findViewById(R.id.wishlist_recycler);
        wishlistRecycler.addItemDecoration(new RecyclerMarginDecorator(getActivity(), RecyclerMarginDecorator.ORIENTATION.BOTH));
        wishlistRecycler.setItemAnimator(new DefaultItemAnimator());
        wishlistRecycler.setHasFixedSize(true);
        // TODO A better solution would be to dynamically determine the number of columns.
        recyclerLayoutManaged = new GridLayoutManager(getActivity(), 2);
        wishlistRecycler.setLayoutManager(recyclerLayoutManaged);
        if (recyclerState != null) recyclerLayoutManaged.onRestoreInstanceState(recyclerState);

        wishlistAdapter = new WishListRecyclerAdapter(getActivity(), new WishlistInterface() {
            @Override
            public void onWishlistItemSelected(View view, WishlistItem wishlistItem) {
                if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    setReenterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
                }
                if (getActivity() != null) ((MainActivity) getActivity()).onProductSelected(wishlistItem.getVariant().getProductId());
            }

            @Override
            public void onRemoveItemFromWishList(View caller, final WishlistItem wishlistItem, final int adapterPosition) {
                if (wishlistItem != null) {
                    progressDialog.show();
                    removeFromWishList(getActivity(), wishlistItem.getId(), SettingsMy.getActiveUser(), CONST.WISHLIST_REQUESTS_TAG, new RequestListener() {
                        @Override
                        public void requestSuccess(long newWishlistId) {
                            progressDialog.hide();
                            wishlistAdapter.remove(adapterPosition);
                            checkIfEmpty();

                            // Show snackBar for possibility to revert an action
                            Snackbar snackbar = Snackbar.make(rootLayout, R.string.Product_deleted_from_wishlist, Snackbar.LENGTH_LONG)
                                    .setActionTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent))
                                    .setAction(R.string.Undo, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            progressDialog.show();
                                            addToWishList(getActivity(), wishlistItem.getVariant().getId(), SettingsMy.getActiveUser(), CONST.WISHLIST_REQUESTS_TAG, new RequestListener() {
                                                @Override
                                                public void requestSuccess(long newWishlistId) {
                                                    progressDialog.hide();
                                                    wishlistItem.setId(newWishlistId);
                                                    wishlistAdapter.add(adapterPosition, wishlistItem);
                                                    checkIfEmpty();
                                                }

                                                @Override
                                                public void requestFailed(VolleyError error) {
                                                    progressDialog.hide();
                                                }
                                            });
                                        }
                                    });
                            TextView textView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.textIconColorPrimary));
                            snackbar.show();
                        }

                        @Override
                        public void requestFailed(VolleyError error) {
                            progressDialog.hide();
                        }
                    });
                }
            }
        });
        wishlistRecycler.setAdapter(wishlistAdapter);
    }

    /**
     * Load wishlist content.
     *
     * @param user logged user.
     */
    private void getWishlistContent(@NonNull User user) {
        String url = String.format(EndPoints.WISHLIST, SettingsMy.getActualNonNullShop(getActivity()).getId());

        progressDialog.show();
        GsonRequest<WishlistResponse> getWishlist = new GsonRequest<>(Request.Method.GET, url, null, WishlistResponse.class,
                new Response.Listener<WishlistResponse>() {
                    @Override
                    public void onResponse(@NonNull WishlistResponse wishlistResponse) {
                        if (progressDialog != null) progressDialog.cancel();

                        for (int i = 0; i < wishlistResponse.getItems().size(); i++) {
                            wishlistAdapter.add(i, wishlistResponse.getItems().get(i));
                        }
                        checkIfEmpty();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog != null) progressDialog.cancel();
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
            }
        }, getFragmentManager(), user.getAccessToken());
        getWishlist.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        getWishlist.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(getWishlist, CONST.CART_REQUESTS_TAG);
    }

    private void checkIfEmpty() {
        if (wishlistAdapter != null && !wishlistAdapter.isEmpty()) {
            emptyLayout.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
        } else {
            emptyLayout.setVisibility(View.VISIBLE);
            contentLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (recyclerLayoutManaged != null)
            recyclerState = recyclerLayoutManaged.onSaveInstanceState();
    }

    @Override
    public void onStop() {
        MyApplication.getInstance().cancelPendingRequests(CONST.WISHLIST_REQUESTS_TAG);
        if (progressDialog != null) progressDialog.cancel();
        super.onStop();
    }
}
