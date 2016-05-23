package bf.io.openshop.ux.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.MessageDialog;

import org.json.JSONException;
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
import bf.io.openshop.entities.User;
import bf.io.openshop.entities.product.Product;
import bf.io.openshop.entities.product.ProductColor;
import bf.io.openshop.entities.product.ProductSize;
import bf.io.openshop.entities.product.ProductVariant;
import bf.io.openshop.interfaces.LoginDialogInterface;
import bf.io.openshop.interfaces.ProductImagesRecyclerInterface;
import bf.io.openshop.interfaces.RelatedProductsRecyclerInterface;
import bf.io.openshop.interfaces.RequestListener;
import bf.io.openshop.listeners.OnSingleClickListener;
import bf.io.openshop.utils.Analytics;
import bf.io.openshop.utils.JsonUtils;
import bf.io.openshop.utils.MsgUtils;
import bf.io.openshop.utils.RecyclerMarginDecorator;
import bf.io.openshop.utils.Utils;
import bf.io.openshop.ux.MainActivity;
import bf.io.openshop.ux.adapters.ColorSpinnerAdapter;
import bf.io.openshop.ux.adapters.ProductImagesRecyclerAdapter;
import bf.io.openshop.ux.adapters.RelatedProductsRecyclerAdapter;
import bf.io.openshop.ux.adapters.SizeVariantSpinnerAdapter;
import bf.io.openshop.ux.dialogs.LoginDialogFragment;
import bf.io.openshop.ux.dialogs.LoginExpiredDialogFragment;
import bf.io.openshop.ux.dialogs.ProductImagesDialogFragment;
import mbanje.kurt.fabbutton.FabButton;
import timber.log.Timber;

/**
 * Fragment shows a detail of the product.
 */
public class ProductFragment extends Fragment {

    private static final String PRODUCT_ID = "product_id";

    private ProgressBar progressView;

    // Fields referencing complex screen layouts.
    private View layoutEmpty;
    private RelativeLayout productContainer;
    private ScrollView contentScrollLayout;

    // Fields referencing product related views.
    private TextView productNameTv;
    private TextView productPriceDiscountTv;
    private TextView productPriceTv;
    private TextView productInfoTv;
    private TextView productPriceDiscountPercentTv;

    /**
     * Refers to the displayed product.
     */
    private Product product;
    /**
     * Refers to a user-selected product variant
     */
    private ProductVariant selectedProductVariant = null;

    /**
     * Spinner offering all available product colors.
     */
    private Spinner colorSpinner;

    private SizeVariantSpinnerAdapter sizeVariantSpinnerAdapter;
    private ArrayList<String> productImagesUrls;
    private RecyclerView productImagesRecycler;
    private ProductImagesRecyclerAdapter productImagesAdapter;
    private RelatedProductsRecyclerAdapter relatedProductsAdapter;
    private ViewTreeObserver.OnScrollChangedListener scrollViewListener;

    // Indicates running add product to cart request.
    private ImageView addToCartImage;
    private ProgressBar addToCartProgress;

    /**
     * Floating button allowing add/remove product from wishlist.
     */
    private FabButton wishlistButton;
    /**
     * Determine if product is in wishlist.
     */
    private boolean inWishlist = false;
    /**
     * Id of the wishlist item representing product.
     */
    private long wishlistId = CONST.DEFAULT_EMPTY_ID;

    /**
     * Create a new fragment instance for product detail.
     *
     * @param productId id of the product to show.
     * @return new fragment instance.
     */
    public static ProductFragment newInstance(long productId) {
        Bundle args = new Bundle();
        args.putLong(PRODUCT_ID, productId);
        ProductFragment fragment = new ProductFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - onCreateView", this.getClass().getSimpleName());
        MainActivity.setActionBarTitle(getString(R.string.Product));

        View view = inflater.inflate(R.layout.fragment_product, container, false);

        progressView = (ProgressBar) view.findViewById(R.id.product_progress);

        productContainer = (RelativeLayout) view.findViewById(R.id.product_container);
        layoutEmpty = view.findViewById(R.id.product_empty_layout);
        contentScrollLayout = (ScrollView) view.findViewById(R.id.product_scroll_layout);

        productNameTv = (TextView) view.findViewById(R.id.product_name);
        productPriceDiscountPercentTv = (TextView) view.findViewById(R.id.product_price_discount_percent);
        productPriceDiscountTv = (TextView) view.findViewById(R.id.product_price_discount);
        productPriceTv = (TextView) view.findViewById(R.id.product_price);
        productInfoTv = (TextView) view.findViewById(R.id.product_info);

        colorSpinner = (Spinner) view.findViewById(R.id.product_color_spinner);
        prepareSizeSpinner(view);

        prepareButtons(view);
        prepareProductImagesLayout(view);
        prepareScrollViewAndWishlist(view);

        long productId = getArguments().getLong(PRODUCT_ID, 0);
        getProduct(productId);
        return view;
    }

    /**
     * Prepare product size views, adapters and listeners.
     *
     * @param view fragment base view.
     */
    private void prepareSizeSpinner(View view) {
        Spinner sizeSpinner = (Spinner) view.findViewById(R.id.product_size_spinner);
        sizeVariantSpinnerAdapter = new SizeVariantSpinnerAdapter(getActivity());
        sizeVariantSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizeSpinner.setAdapter(sizeVariantSpinnerAdapter);
        sizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ProductVariant selectedVariantSize = sizeVariantSpinnerAdapter.getItem(position);
                if (selectedVariantSize != null && selectedVariantSize.getSize() != null) {
                    Timber.d("selectedVariant: %d, selectedSize: %s", selectedVariantSize.getId(), selectedVariantSize.getSize().getValue());
                    if (selectedVariantSize.getId() == CONST.DEFAULT_EMPTY_ID && selectedVariantSize.getSize().getId() != CONST.DEFAULT_EMPTY_ID) {
                        selectedProductVariant = null;
                    } else {
                        selectedProductVariant = selectedVariantSize;
                    }
                } else {
                    selectedProductVariant = null;
                    Timber.e(new RuntimeException(), "User click on null product variant. WTF");
                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, "", MsgUtils.ToastLength.SHORT);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedProductVariant = null;
            }
        });
    }

    /**
     * Prepare buttons views and listeners.
     *
     * @param view fragment base view.
     */
    private void prepareButtons(View view) {
        addToCartImage = (ImageView) view.findViewById(R.id.product_add_to_cart_image);
        addToCartProgress = (ProgressBar) view.findViewById(R.id.product_add_to_cart_progress);
        addToCartProgress.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.textIconColorPrimary), PorterDuff.Mode.MULTIPLY);
        View addToCart = view.findViewById(R.id.product_add_to_cart_layout);

        addToCart.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                postProductToCart();
            }
        });

        Button sendToFriendBtn = (Button) view.findViewById(R.id.product_send_to_a_friend);
        sendToFriendBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (MyApplication.getInstance().isDataConnected()) {
                    Timber.d("FragmentProductDetail share link clicked");
                    // send message with prepared content
                    try {
                        MessageDialog messageDialog = new MessageDialog(getActivity());
                        if (MessageDialog.canShow(ShareLinkContent.class)) {
                            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                    .setContentTitle(product.getName())
                                    .setContentDescription(product.getDescription())
                                    .setContentUrl(Uri.parse(product.getUrl()))
                                    .setImageUrl(Uri.parse(product.getMainImage()))
                                    .build();
                            messageDialog.show(linkContent);
                        } else {
                            Timber.e("FragmentProductDetail - APP is NOT installed");
                            final String appPackageName = "com.facebook.orca"; // getPackageName() from Context or Activity object
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                        }
                    } catch (Exception e) {
                        Timber.e(e, "Create share dialog exception");
                    }
                } else {
                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_NO_NETWORK, null, MsgUtils.ToastLength.SHORT);
                }
            }
        });
    }

    /**
     * Prepare product images and related products views, adapters and listeners.
     *
     * @param view fragment base view.
     */
    private void prepareProductImagesLayout(View view) {
        productImagesRecycler = (RecyclerView) view.findViewById(R.id.product_images_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        productImagesRecycler.setLayoutManager(linearLayoutManager);
        productImagesAdapter = new ProductImagesRecyclerAdapter(getActivity(), new ProductImagesRecyclerInterface() {
            @Override
            public void onImageSelected(View v, int position) {
                ProductImagesDialogFragment imagesDialog = ProductImagesDialogFragment.newInstance(productImagesUrls, position);
                if (imagesDialog != null)
                    imagesDialog.show(getFragmentManager(), ProductImagesDialogFragment.class.getSimpleName());
                else {
                    Timber.e("%s Called with empty image list", ProductImagesDialogFragment.class.getSimpleName());
                }
            }
        });
        productImagesRecycler.setAdapter(productImagesAdapter);

        ViewGroup.LayoutParams params = productImagesRecycler.getLayoutParams();
        DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        int densityDpi = dm.densityDpi;

        // For small screen even smaller images.
        if (densityDpi <= DisplayMetrics.DENSITY_MEDIUM) {
            params.height = (int) (dm.heightPixels * 0.4);
        } else {
            params.height = (int) (dm.heightPixels * 0.48);
        }

        // Prepare related products
        RecyclerView relatedProductsRecycler = (RecyclerView) view.findViewById(R.id.product_recommended_images_recycler);
        relatedProductsRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        relatedProductsRecycler.addItemDecoration(new RecyclerMarginDecorator(getContext(), RecyclerMarginDecorator.ORIENTATION.HORIZONTAL));
        relatedProductsAdapter = new RelatedProductsRecyclerAdapter(getActivity(), new RelatedProductsRecyclerInterface() {
            @Override
            public void onRelatedProductSelected(View v, int position, Product product) {
                if (product != null && getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).onProductSelected(product.getId());
                }
            }

        });
        relatedProductsRecycler.setAdapter(relatedProductsAdapter);
    }

    /**
     * Prepare scroll view related animations and floating wishlist button.
     *
     * @param view fragment base view.
     */
    private void prepareScrollViewAndWishlist(View view) {
        final View productBackground = view.findViewById(R.id.product_background);
        wishlistButton = (FabButton) view.findViewById(R.id.product_add_to_wish_list);

        scrollViewListener = new ViewTreeObserver.OnScrollChangedListener() {
            private boolean alphaFull = false;

            @Override
            public void onScrollChanged() {
                int scrollY = contentScrollLayout.getScrollY();
                if (productImagesRecycler != null) {

                    if (wishlistButton.getWidth() * 2 > scrollY) {
                        wishlistButton.setTranslationX(scrollY / 4);
                    } else {
                        wishlistButton.setTranslationX(wishlistButton.getWidth() / 2);
                    }

                    float alphaRatio;
                    if (productImagesRecycler.getHeight() > scrollY) {
                        productImagesRecycler.setTranslationY(scrollY / 2);
                        alphaRatio = (float) scrollY / productImagesRecycler.getHeight();
                    } else {
                        alphaRatio = 1;
                    }
//                    Timber.e("scrollY:" + scrollY + ". Alpha:" + alphaRatio);

                    if (alphaFull) {
                        if (alphaRatio <= 0.99) alphaFull = false;
                    } else {
                        if (alphaRatio >= 0.9) alphaFull = true;
                        productBackground.setAlpha(alphaRatio);
                    }
                } else {
                    Timber.e("Null productImagesScroll");
                }
            }
        };
    }

    /**
     * Load product data.
     *
     * @param productId id of product.
     */
    private void getProduct(final long productId) {
        // Load product info
        String url = String.format(EndPoints.PRODUCTS_SINGLE_RELATED, SettingsMy.getActualNonNullShop(getActivity()).getId(), productId);
        setContentVisible(CONST.VISIBLE.PROGRESS);

        GsonRequest<Product> getProductRequest = new GsonRequest<>(Request.Method.GET, url, null, Product.class,
                new Response.Listener<Product>() {
                    @Override
                    public void onResponse(@NonNull Product response) {
                        MainActivity.setActionBarTitle(response.getName());
                        if (response.getVariants() != null && response.getVariants().size() > 0) {
                            getWishListInfo(productId);
                        }
                        addRecommendedProducts(response.getRelated());
                        refreshScreenData(response);
                        setContentVisible(CONST.VISIBLE.CONTENT);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setContentVisible(CONST.VISIBLE.EMPTY);
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
            }
        });
        getProductRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        getProductRequest.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(getProductRequest, CONST.PRODUCT_REQUESTS_TAG);
    }

    /**
     * Load product wishlist info. Determine state of wishlist button.
     * If a user is logged out, nothing will happen.
     *
     * @param productId id of product.
     */
    private void getWishListInfo(long productId) {
        User user = SettingsMy.getActiveUser();
        if (user != null) {
            // determine if product is in wishlist
            String wishlistUrl = String.format(EndPoints.WISHLIST_IS_IN_WISHLIST, SettingsMy.getActualNonNullShop(getActivity()).getId(), productId);
            JsonRequest getWishlistInfo = new JsonRequest(Request.Method.GET, wishlistUrl, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    prepareWishListButton(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    MsgUtils.logAndShowErrorMessage(getActivity(), error);
                }
            }, getFragmentManager(), user.getAccessToken());
            getWishlistInfo.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            getWishlistInfo.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(getWishlistInfo, CONST.PRODUCT_REQUESTS_TAG);
        }
    }

    private void prepareWishListButton(JSONObject response) {
        if ((response.has(JsonUtils.TAG_IS_IN_WISHLIST) && !response.isNull(JsonUtils.TAG_IS_IN_WISHLIST)) &&
                (response.has(JsonUtils.TAG_WISHLIST_PRODUCT_ID))) {
            // Load response data, if exist

            try {
                inWishlist = response.getBoolean(JsonUtils.TAG_IS_IN_WISHLIST);
                if (response.isNull(JsonUtils.TAG_WISHLIST_PRODUCT_ID))
                    wishlistId = CONST.DEFAULT_EMPTY_ID;
                else wishlistId = response.getLong(JsonUtils.TAG_WISHLIST_PRODUCT_ID);
            } catch (Exception e) {
                Timber.e(e, "Wishlist info parse exception");
                return;
            }

            // Check data consistence
            if (inWishlist && wishlistId == CONST.DEFAULT_EMPTY_ID) {
                Timber.e("Inconsistent data in is_in_wishlist response");
            } else {
                if (inWishlist)
                    wishlistButton.setIcon(R.drawable.wish_list_pressed, R.drawable.wish_list);
                else wishlistButton.setIcon(R.drawable.wish_list, R.drawable.wish_list_pressed);

                wishlistButton.setVisibility(View.VISIBLE);
                wishlistButton.setOnClickListener(new View.OnClickListener() {
                    private boolean running = false;

                    @Override
                    public void onClick(View v) {
                        if (!running) {
                            running = true;
                            User user = SettingsMy.getActiveUser();
                            if (user != null) {
                                if (inWishlist) {
                                    inWishlist = false;
                                    wishlistButton.setIcon(R.drawable.wish_list_pressed, R.drawable.wish_list);
                                    wishlistButton.showProgress(true);
                                    if (wishlistId != CONST.DEFAULT_EMPTY_ID) {
                                        WishlistFragment.removeFromWishList(getActivity(), wishlistId, user, CONST.PRODUCT_REQUESTS_TAG, new RequestListener() {
                                            @Override
                                            public void requestSuccess(long newWishlistId) {
                                                running = false;
                                                wishlistButton.onProgressCompleted();
                                                wishlistId = CONST.DEFAULT_EMPTY_ID;
                                            }

                                            @Override
                                            public void requestFailed(VolleyError error) {
                                                running = false;
                                                wishlistButton.showProgress(false);
                                            }
                                        });
                                    } else {
                                        running = false;
                                        wishlistButton.showProgress(false);
                                        Timber.e(new RuntimeException(), "Trying remove product from wishlist with error consta");
                                    }
                                } else {
                                    inWishlist = true;
                                    wishlistButton.setIcon(R.drawable.wish_list, R.drawable.wish_list_pressed);
                                    wishlistButton.showProgress(true);
                                    WishlistFragment.addToWishList(getActivity(), product.getVariants().get(0).getId(), user, CONST.PRODUCT_REQUESTS_TAG, new RequestListener() {
                                        @Override
                                        public void requestSuccess(long newWishlistId) {
                                            running = false;
                                            wishlistButton.onProgressCompleted();
                                            wishlistId = newWishlistId;

                                            String result = getString(R.string.Product_added_to_wishlist);
                                            Snackbar snackbar = Snackbar.make(productContainer, result, Snackbar.LENGTH_LONG)
                                                    .setActionTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent))
                                                    .setAction(R.string.Go_to_wishlist, new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            if (getActivity() instanceof MainActivity)
                                                                ((MainActivity) getActivity()).onWishlistSelected();
                                                        }
                                                    });
                                            TextView textView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                                            textView.setTextColor(Color.WHITE);
                                            snackbar.show();
                                        }

                                        @Override
                                        public void requestFailed(VolleyError error) {
                                            running = false;
                                            wishlistButton.showProgress(false);
                                        }
                                    });
                                }
                            } else {
                                running = false;
                                LoginExpiredDialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
                                loginExpiredDialogFragment.show(getFragmentManager(), "loginExpiredDialogFragment");
                            }
                        }
                    }
                });
            }
        } else {
            Timber.e("Missing is_in_wishlist data: %s", response);
        }
    }

    private void refreshScreenData(Product product) {
        if (product != null) {
            Analytics.logProductView(product.getRemoteId(), product.getName());

            productNameTv.setText(product.getName());

            // Determine if product is on sale
            double pr = product.getPrice();
            double dis = product.getDiscountPrice();
            if (pr == dis || Math.abs(pr - dis) / Math.max(Math.abs(pr), Math.abs(dis)) < 0.000001) {
                productPriceDiscountTv.setText(product.getDiscountPriceFormatted());
                productPriceDiscountTv.setTextColor(ContextCompat.getColor(getContext(), R.color.textPrimary));
                productPriceTv.setVisibility(View.GONE);
                productPriceDiscountPercentTv.setVisibility(View.GONE);
            } else {
                productPriceDiscountTv.setText(product.getDiscountPriceFormatted());
                productPriceDiscountTv.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                productPriceTv.setVisibility(View.VISIBLE);
                productPriceTv.setText(product.getPriceFormatted());
                productPriceTv.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                productPriceDiscountPercentTv.setVisibility(View.VISIBLE);
                productPriceDiscountPercentTv.setText(Utils.calculateDiscountPercent(getContext(), pr, dis));
            }
            if (product.getDescription() != null) {
                productInfoTv.setMovementMethod(LinkMovementMethod.getInstance());
                productInfoTv.setText(Utils.safeURLSpanLinks(Html.fromHtml(product.getDescription()), getActivity()));
            }

            setSpinners(product);
        } else {
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, getString(R.string.Internal_error), MsgUtils.ToastLength.LONG);
            Timber.e(new RuntimeException(), "Refresh product screen with null product");
        }
    }

    private void setSpinners(Product product) {
        if (product != null && product.getVariants() != null && product.getVariants().size() > 0) {
            this.product = product;
            List<ProductColor> productColors = new ArrayList<>();

            for (ProductVariant pv : product.getVariants()) {
                ProductColor pac = pv.getColor();
                if (!productColors.contains(pac)) {
                    productColors.add(pac);
                }
            }

            if (productColors.size() > 1) {
                colorSpinner.setVisibility(View.VISIBLE);
                ColorSpinnerAdapter colorSpinnerAdapter = new ColorSpinnerAdapter(getActivity());
                colorSpinnerAdapter.setProductColorList(productColors);
                colorSpinner.setAdapter(colorSpinnerAdapter);
                colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ProductColor productColor = (ProductColor) parent.getItemAtPosition(position);
                        if (productColor != null) {
                            Timber.d("ColorPicker selected color: %s", productColor.toString());
                            updateImagesAndSizeSpinner(productColor);
                        } else {
                            Timber.e("Retrieved null color from spinner.");
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Timber.d("Nothing selected in product colors spinner.");
                    }
                });
            } else {
                colorSpinner.setVisibility(View.GONE);
                updateImagesAndSizeSpinner(product.getVariants().get(0).getColor());
            }
        } else {
            Timber.e("Setting spinners for null product variants.");
        }
    }

    private void updateImagesAndSizeSpinner(ProductColor productColor) {
        if (product != null) {
            ArrayList<ProductVariant> variantSizeArrayList = new ArrayList<>();
            productImagesUrls = new ArrayList<>();

            for (ProductVariant pv : product.getVariants()) {
                if (pv.getColor().equals(productColor)) {
                    variantSizeArrayList.add(pv);

                    if (pv.getImages() != null && pv.getImages().length > 0) {
                        for (String s : pv.getImages()) {
                            if (!(productImagesUrls.contains(s))) {
                                productImagesUrls.add(s);
                                Timber.d("getAvailableSizesForColor image: %s", s);
                            }
                        }
                    } else {
                        if (!(productImagesUrls.contains(product.getMainImage()))) {
                            productImagesUrls.add(product.getMainImage());
                            Timber.d("getAvailableSizesForColor images[] == null, setMain_image()");
                        }
                    }
                }
            }

            // Show sizes
            if (variantSizeArrayList.size() > 1) {
                variantSizeArrayList.add(0, new ProductVariant(CONST.DEFAULT_EMPTY_ID, new ProductSize(CONST.DEFAULT_EMPTY_ID, CONST.DEFAULT_EMPTY_ID, getString(R.string.Select_size))));
            }
            sizeVariantSpinnerAdapter.setProductSizeList(variantSizeArrayList);

            // Show related products
            if (productImagesAdapter != null) {
                productImagesAdapter.clearAll();
                for (String url : productImagesUrls) {
                    productImagesAdapter.addLast(url);
                }
            }
        } else {
            Timber.e("UpdateImagesAndSizeSpinner with null product in memory.");
        }
    }

    private void addRecommendedProducts(List<Product> related) {
        if (related != null && !related.isEmpty()) {
            Timber.d("AddRecommendedProducts size : %d", related.size());
            for (Product prod : related) {
                relatedProductsAdapter.addLast(prod);
            }
        } else {
            Timber.d("Related products are null or empty.");
        }
    }

    private void postProductToCart() {
        if (selectedProductVariant == null || selectedProductVariant.getSize() == null ||
                (selectedProductVariant.getId() == CONST.DEFAULT_EMPTY_ID &&
                        selectedProductVariant.getSize().getId() == CONST.DEFAULT_EMPTY_ID)) {
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_NO_SIZE_SELECTED, null, MsgUtils.ToastLength.SHORT);
            return;
        }
        User user = SettingsMy.getActiveUser();
        if (user != null) {
            if (addToCartImage != null) addToCartImage.setVisibility(View.INVISIBLE);
            if (addToCartProgress != null) addToCartProgress.setVisibility(View.VISIBLE);

            // get selected radio button from radioGroup
            JSONObject jo = new JSONObject();
            try {
                jo.put(JsonUtils.TAG_PRODUCT_VARIANT_ID, selectedProductVariant.getId());
            } catch (JSONException e) {
                Timber.e(e, "Create json add product to cart exception");
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
                return;
            }

            String url = String.format(EndPoints.CART, SettingsMy.getActualNonNullShop(getActivity()).getId());
            JsonRequest addToCart = new JsonRequest(Request.Method.POST, url, jo, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (BuildConfig.DEBUG) Timber.d("AddToCartResponse: %s", response);
                    if (addToCartImage != null) addToCartImage.setVisibility(View.VISIBLE);
                    if (addToCartProgress != null)
                        addToCartProgress.setVisibility(View.INVISIBLE);

                    Analytics.logAddProductToCart(product.getRemoteId(), product.getName(), product.getDiscountPrice());
                    MainActivity.updateCartCountNotification();

                    String result = getString(R.string.Product) + " " + getString(R.string.added_to_cart);
                    Snackbar snackbar = Snackbar.make(productContainer, result, Snackbar.LENGTH_LONG)
                            .setActionTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent))
                            .setAction(R.string.Go_to_cart, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (getActivity() instanceof MainActivity)
                                        ((MainActivity) getActivity()).onCartSelected();
                                }
                            });
                    TextView textView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    snackbar.show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (addToCartImage != null) addToCartImage.setVisibility(View.VISIBLE);
                    if (addToCartProgress != null) addToCartProgress.setVisibility(View.INVISIBLE);
                    MsgUtils.logAndShowErrorMessage(getActivity(), error);
                }
            }, getFragmentManager(), user.getAccessToken());
            addToCart.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            addToCart.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(addToCart, CONST.PRODUCT_REQUESTS_TAG);
        } else {
            LoginDialogFragment loginDialog = LoginDialogFragment.newInstance(new LoginDialogInterface() {
                @Override
                public void successfulLoginOrRegistration(User user) {
                    postProductToCart();
                }
            });
            loginDialog.show(getFragmentManager(), LoginDialogFragment.class.getSimpleName());
        }
    }

    /**
     * Display content layout, progress bar or empty layout.
     *
     * @param visible enum value defining visible layout.
     */
    private void setContentVisible(CONST.VISIBLE visible) {
        if (layoutEmpty != null && contentScrollLayout != null && progressView != null) {
            switch (visible) {
                case EMPTY:
                    layoutEmpty.setVisibility(View.VISIBLE);
                    contentScrollLayout.setVisibility(View.INVISIBLE);
                    progressView.setVisibility(View.GONE);
                    break;
                case PROGRESS:
                    layoutEmpty.setVisibility(View.GONE);
                    contentScrollLayout.setVisibility(View.INVISIBLE);
                    progressView.setVisibility(View.VISIBLE);
                    break;
                default: // Content
                    layoutEmpty.setVisibility(View.GONE);
                    contentScrollLayout.setVisibility(View.VISIBLE);
                    progressView.setVisibility(View.GONE);
            }
        } else {
            Timber.e(new RuntimeException(), "Setting content visibility with null views.");
        }
    }

    @Override
    public void onResume() {
        if (contentScrollLayout != null) contentScrollLayout.getViewTreeObserver().addOnScrollChangedListener(scrollViewListener);
        super.onResume();
    }

    @Override
    public void onPause() {
        if (contentScrollLayout != null) contentScrollLayout.getViewTreeObserver().removeOnScrollChangedListener(scrollViewListener);
        super.onPause();
    }

    @Override
    public void onStop() {
        MyApplication.getInstance().cancelPendingRequests(CONST.PRODUCT_REQUESTS_TAG);
        setContentVisible(CONST.VISIBLE.CONTENT);
        if (addToCartImage != null) addToCartImage.setVisibility(View.VISIBLE);
        if (addToCartProgress != null) addToCartProgress.setVisibility(View.INVISIBLE);
        super.onStop();
    }
}
