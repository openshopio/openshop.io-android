package bf.io.openshop.ux.dialogs;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import bf.io.openshop.CONST;
import bf.io.openshop.MyApplication;
import bf.io.openshop.R;
import bf.io.openshop.SettingsMy;
import bf.io.openshop.api.EndPoints;
import bf.io.openshop.api.GsonRequest;
import bf.io.openshop.api.JsonRequest;
import bf.io.openshop.entities.User;
import bf.io.openshop.entities.cart.CartProductItem;
import bf.io.openshop.entities.product.Product;
import bf.io.openshop.entities.product.ProductColor;
import bf.io.openshop.entities.product.ProductQuantity;
import bf.io.openshop.entities.product.ProductVariant;
import bf.io.openshop.interfaces.RequestListener;
import bf.io.openshop.utils.JsonUtils;
import bf.io.openshop.utils.MsgUtils;
import bf.io.openshop.ux.adapters.ColorTextSpinnerAdapter;
import bf.io.openshop.ux.adapters.QuantitySpinnerAdapter;
import bf.io.openshop.ux.adapters.SizeVariantSpinnerAdapter;
import timber.log.Timber;


public class UpdateCartItemDialogFragment extends DialogFragment {
    
    private static final int QUANTITY_MAX = 15;
    private CartProductItem cartProductItem;
    private RequestListener requestListener;
    private View dialogProgress;
    private View dialogContent;
    private Spinner itemColorsSpinner;
    private Spinner itemSizesSpinner;
    private Spinner quantitySpinner;

    public static UpdateCartItemDialogFragment newInstance(CartProductItem cartProductItem, RequestListener requestListener) {
        if (cartProductItem == null) {
            Timber.e(new RuntimeException(), "Created UpdateCartItemDialogFragment with null parameters.");
            return null;
        }
        UpdateCartItemDialogFragment updateCartItemDialogFragment = new UpdateCartItemDialogFragment();
        updateCartItemDialogFragment.cartProductItem = cartProductItem;
        updateCartItemDialogFragment.requestListener = requestListener;
        return updateCartItemDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.dialogFragmentAnimation);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_update_cart_item, container, false);

        dialogProgress = view.findViewById(R.id.dialog_update_cart_item_progress);
        dialogContent = view.findViewById(R.id.dialog_update_cart_item_content);
        itemColorsSpinner = (Spinner) view.findViewById(R.id.dialog_update_cart_item_color_spin);
        itemSizesSpinner = (Spinner) view.findViewById(R.id.dialog_update_cart_item_size_spin);
        TextView itemName = (TextView) view.findViewById(R.id.dialog_update_cart_item_title);
        itemName.setText(cartProductItem.getVariant().getName());

        View btnSave = view.findViewById(R.id.dialog_update_cart_item_save_btn);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantitySpinner != null && itemSizesSpinner != null) {
                    ProductVariant productVariant = (ProductVariant) itemSizesSpinner.getSelectedItem();
                    ProductQuantity productQuantity = (ProductQuantity) quantitySpinner.getSelectedItem();
                    Timber.e("Selected: " + productVariant + ". Quantity: " + productQuantity);
                    if (productVariant != null && productVariant.getSize() != null && productQuantity != null) {
                        updateProductInCart(cartProductItem.getId(),
                                productVariant.getId(), productQuantity.getQuantity());
//                        if (updateCartItemListener != null)
//                            updateCartItemListener.updateProductInCart(cartProductItem.getId(),
//                                    productVariant.getId(), productQuantity.getQuantity());
                    } else {
                        Timber.e(new RuntimeException(), "Cannot obtain info about edited cart item.");
                        MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Internal_error_reload_cart_please), MsgUtils.ToastLength.SHORT);
                        dismiss();
                    }
                } else {
                    Timber.e(new NullPointerException(), "Null spinners in editing item in cart");
                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Internal_error_reload_cart_please), MsgUtils.ToastLength.SHORT);
                    dismiss();
                }
            }
        });

        View btnCancel = view.findViewById(R.id.dialog_update_cart_item_cancel_btn);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // Set item quantity
        QuantitySpinnerAdapter adapterQuantity = new QuantitySpinnerAdapter(getActivity(), android.R.layout.simple_spinner_item, getQuantities());
        adapterQuantity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quantitySpinner = (Spinner) view.findViewById(R.id.dialog_update_cart_item_quantity_spin);
        quantitySpinner.setAdapter(adapterQuantity);

        getProductDetail(cartProductItem);
        return view;
    }

    // Prepare quantity spinner layout
    private List<ProductQuantity> getQuantities() {
        List<ProductQuantity> quantities = new ArrayList<>();
        for (int i = 1; i <= QUANTITY_MAX; i++) {
            ProductQuantity q = new ProductQuantity(i, i + "x");
            quantities.add(q);
        }
        return quantities;
    }

    private void getProductDetail(CartProductItem cartProductItem) {
        String url = String.format(EndPoints.PRODUCTS_SINGLE, SettingsMy.getActualNonNullShop(getActivity()).getId(), cartProductItem.getVariant().getProductId());

        setProgressActive(true);

        GsonRequest<Product> getProductRequest = new GsonRequest<>(Request.Method.GET, url, null, Product.class,
                new Response.Listener<Product>() {
                    @Override
                    public void onResponse(@NonNull Product response) {
                        setProgressActive(false);
                        setSpinners(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setProgressActive(false);
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
            }
        });
        getProductRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        getProductRequest.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(getProductRequest, CONST.update_cart_item_requests_tag);
    }


    private void setSpinners(final Product product) {
        if (product != null) {
            ProductColor actualItemColor = cartProductItem.getVariant().getColor();

            List<ProductColor> colorsList = new ArrayList<>();
            List<ProductVariant> variantSizeArrayList = new ArrayList<>();

            for (ProductVariant pv : product.getVariants()) {
                ProductColor pac = pv.getColor();
                // Fill available colors colors
                if (!colorsList.contains(pac)) {
                    colorsList.add(pac);
                }
                // Fill available sizes
                if (pv.getColor().equalsColors(actualItemColor)) {
                    variantSizeArrayList.add(pv);
                }
            }

            if (colorsList.isEmpty() || colorsList.size() == 1) {
                itemColorsSpinner.setVisibility(View.GONE);
            } else {
                itemColorsSpinner.setVisibility(View.VISIBLE);
                ColorTextSpinnerAdapter adapterColor = new ColorTextSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_item, colorsList);
                adapterColor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                itemColorsSpinner.setAdapter(adapterColor);
                if (actualItemColor != null) {
                    int index = colorsList.indexOf(actualItemColor);
                    Timber.d("SetSpinners selectedColor: " + actualItemColor.toString());
                    if (index == -1) {
                        itemColorsSpinner.setSelection(0);
                        Timber.e(new NullPointerException(), "Actual item color didn't match server received item colors");
                    } else {
                        itemColorsSpinner.setSelection(index);
                    }
                }
            }

            SizeVariantSpinnerAdapter adapterSize = new SizeVariantSpinnerAdapter(getActivity(), variantSizeArrayList);
            adapterSize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            itemSizesSpinner.setAdapter(adapterSize);
            // Select actual size
            if (variantSizeArrayList.size() > 0) {
                int sizeSelection = 0;
                for (int i = 0; i < variantSizeArrayList.size(); i++) {
                    Timber.e("Compare list: " + variantSizeArrayList.get(i).getId() + " == " + cartProductItem.getVariant().getId() + " as actual");
                    if (variantSizeArrayList.get(i).getId() == cartProductItem.getVariant().getId()) {
                        sizeSelection = i;
                    }
                }
                itemSizesSpinner.setSelection(sizeSelection);
            }
        }

        int selectedPosition = cartProductItem.getQuantity() - 1;
        if (selectedPosition < 0) selectedPosition = 0;
        if (selectedPosition > (quantitySpinner.getCount() - 1))
            Timber.e(new RuntimeException(), "More item quantity that can be. Quantity:" + (selectedPosition + 1) + ", max:" + quantitySpinner.getCount());
        else
            quantitySpinner.setSelection(selectedPosition);
    }

    private void updateProductInCart(long productCartId, long newVariantId, int newQuantity) {
        User user = SettingsMy.getActiveUser();
        if (user != null) {
            JSONObject jo = new JSONObject();
            try {
                jo.put(JsonUtils.TAG_QUANTITY, newQuantity);
                jo.put(JsonUtils.TAG_PRODUCT_VARIANT_ID, newVariantId);
            } catch (JSONException e) {
                Timber.e(e, "Create update object exception");
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
                return;
            }
            Timber.d("update product: " + jo.toString());

            String url = String.format(EndPoints.CART_ITEM, SettingsMy.getActualNonNullShop(getActivity()).getId(), productCartId);

            setProgressActive(true);
            JsonRequest req = new JsonRequest(Request.Method.PUT, url, jo, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Timber.d("Update item in cart: " + response.toString());
                    if (requestListener != null) requestListener.requestSuccess(0);
                    setProgressActive(false);
                    dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    setProgressActive(false);
                    if (requestListener != null) requestListener.requestFailed(error);
                    dismiss();
                }
            }, getFragmentManager(), user.getAccessToken());
            req.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            req.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(req, CONST.update_cart_item_requests_tag);
        } else {
            LoginExpiredDialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
            loginExpiredDialogFragment.show(getFragmentManager(), "loginExpiredDialogFragment");
        }
    }


    private void setProgressActive(boolean active) {
        if (active) {
            dialogProgress.setVisibility(View.VISIBLE);
            dialogContent.setVisibility(View.INVISIBLE);
        } else {
            dialogProgress.setVisibility(View.GONE);
            dialogContent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStop() {
        MyApplication.getInstance().getRequestQueue().cancelAll(CONST.update_cart_item_requests_tag);
        super.onStop();
    }
}
