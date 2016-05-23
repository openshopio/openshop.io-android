package bf.io.openshop.ux.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import bf.io.openshop.CONST;
import bf.io.openshop.MyApplication;
import bf.io.openshop.R;
import bf.io.openshop.SettingsMy;
import bf.io.openshop.api.EndPoints;
import bf.io.openshop.api.GsonRequest;
import bf.io.openshop.entities.User;
import bf.io.openshop.entities.cart.Cart;
import bf.io.openshop.entities.cart.CartProductItem;
import bf.io.openshop.entities.delivery.Delivery;
import bf.io.openshop.entities.delivery.DeliveryRequest;
import bf.io.openshop.entities.delivery.Payment;
import bf.io.openshop.entities.delivery.Shipping;
import bf.io.openshop.entities.order.Order;
import bf.io.openshop.interfaces.PaymentDialogInterface;
import bf.io.openshop.interfaces.ShippingDialogInterface;
import bf.io.openshop.listeners.OnSingleClickListener;
import bf.io.openshop.utils.Analytics;
import bf.io.openshop.utils.JsonUtils;
import bf.io.openshop.utils.MsgUtils;
import bf.io.openshop.utils.Utils;
import bf.io.openshop.ux.MainActivity;
import bf.io.openshop.ux.dialogs.LoginExpiredDialogFragment;
import bf.io.openshop.ux.dialogs.OrderCreateSuccessDialogFragment;
import bf.io.openshop.ux.dialogs.PaymentDialogFragment;
import bf.io.openshop.ux.dialogs.ShippingDialogFragment;
import timber.log.Timber;

/**
 * Fragment allowing the user to create order.
 */
public class OrderCreateFragment extends Fragment {

    public static final String MSG_LOGIN_EXPIRED_DIALOG_FRAGMENT = "loginExpiredDialogFragment";
    private ProgressDialog progressDialog;

    private ScrollView scrollLayout;
    private LinearLayout cartItemsLayout;

    private Cart cart;
    private double orderTotalPrice;
    private TextView cartItemsTotalPrice;
    private TextView orderTotalPriceTv;

    // View with user information used to create order
    private TextInputLayout nameInputWrapper;
    private TextInputLayout streetInputWrapper;
    private TextInputLayout houseNumberInputWrapper;
    private TextInputLayout cityInputWrapper;
    private TextInputLayout zipInputWrapper;
    private TextInputLayout phoneInputWrapper;
    private TextInputLayout emailInputWrapper;
    private TextInputLayout noteInputWrapper;

    // Shipping and payment
    private Delivery delivery;
    private Payment selectedPayment;
    private Shipping selectedShipping;
    private ProgressBar deliveryProgressBar;
    private View deliveryShippingLayout;
    private View deliveryPaymentLayout;
    private TextView selectedShippingNameTv;
    private TextView selectedShippingPriceTv;
    private TextView selectedPaymentNameTv;
    private TextView selectedPaymentPriceTv;
    private GsonRequest<Order> postOrderRequest;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - onCreateView", this.getClass().getSimpleName());
        MainActivity.setActionBarTitle(getString(R.string.Order_summary));

        View view = inflater.inflate(R.layout.fragment_order_create, container, false);

        progressDialog = Utils.generateProgressDialog(getActivity(), false);

        scrollLayout = (ScrollView) view.findViewById(R.id.order_create_scroll_layout);
        cartItemsLayout = (LinearLayout) view.findViewById(R.id.order_create_cart_items_layout);
        cartItemsTotalPrice = (TextView) view.findViewById(R.id.order_create_total_price);

        orderTotalPriceTv = (TextView) view.findViewById(R.id.order_create_summary_total_price);
        TextView termsAndConditionsTv = (TextView) view.findViewById(R.id.order_create_summary_terms_and_condition);
        termsAndConditionsTv.setText(Html.fromHtml(getString(R.string.Click_on_Order_to_allow_our_Terms_and_Conditions)));
        termsAndConditionsTv.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (getActivity() instanceof MainActivity)
                    ((MainActivity) getActivity()).onTermsAndConditionsSelected();
            }
        });

        prepareFields(view);
        prepareDeliveryLayout(view);

        Button finishOrder = (Button) view.findViewById(R.id.order_create_finish);
        finishOrder.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (isRequiredFieldsOk()) {
                    // Prepare data
                    Order order = new Order();
                    order.setName(Utils.getTextFromInputLayout(nameInputWrapper));
                    order.setCity(Utils.getTextFromInputLayout(cityInputWrapper));
                    order.setStreet(Utils.getTextFromInputLayout(streetInputWrapper));
                    order.setHouseNumber(Utils.getTextFromInputLayout(houseNumberInputWrapper));
                    order.setZip(Utils.getTextFromInputLayout(zipInputWrapper));
                    order.setEmail(Utils.getTextFromInputLayout(emailInputWrapper));
                    order.setShippingType(selectedShipping.getId());
                    if (selectedPayment != null) {
                        order.setPaymentType(selectedPayment.getId());
                    } else {
                        order.setPaymentType(-1);
                    }
                    order.setPhone(Utils.getTextFromInputLayout(phoneInputWrapper));
                    order.setNote(Utils.getTextFromInputLayout(noteInputWrapper));

                    // Hide keyboard
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    postOrder(order);
                }
            }
        });

        showSelectedShipping(selectedShipping);
        showSelectedPayment(selectedPayment);

        getUserCart();
        return view;
    }

    /**
     * Prepare content views, adapters and listeners.
     *
     * @param view fragment base view.
     */
    private void prepareFields(View view) {
        nameInputWrapper = (TextInputLayout) view.findViewById(R.id.order_create_name_wrapper);
        streetInputWrapper = (TextInputLayout) view.findViewById(R.id.order_create_street_wrapper);
        houseNumberInputWrapper = (TextInputLayout) view.findViewById(R.id.order_create_houseNumber_wrapper);
        cityInputWrapper = (TextInputLayout) view.findViewById(R.id.order_create_city_wrapper);
        zipInputWrapper = (TextInputLayout) view.findViewById(R.id.order_create_zip_wrapper);
        phoneInputWrapper = (TextInputLayout) view.findViewById(R.id.order_create_phone_wrapper);
        emailInputWrapper = (TextInputLayout) view.findViewById(R.id.order_create_email_wrapper);
        noteInputWrapper = (TextInputLayout) view.findViewById(R.id.order_create_note_wrapper);

        User user = SettingsMy.getActiveUser();
        if (user != null) {
            Utils.setTextToInputLayout(nameInputWrapper, user.getName());
            Utils.setTextToInputLayout(streetInputWrapper, user.getStreet());
            Utils.setTextToInputLayout(houseNumberInputWrapper, user.getHouseNumber());
            Utils.setTextToInputLayout(cityInputWrapper, user.getCity());
            Utils.setTextToInputLayout(zipInputWrapper, user.getZip());
            Utils.setTextToInputLayout(emailInputWrapper, user.getEmail());
            Utils.setTextToInputLayout(phoneInputWrapper, user.getPhone());
        } else {
            LoginExpiredDialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
            loginExpiredDialogFragment.show(getFragmentManager(), MSG_LOGIN_EXPIRED_DIALOG_FRAGMENT);
        }
    }

    /**
     * Check if all input fields are filled and also that is selected shipping and payment.
     * Method highlights all unfilled input fields.
     *
     * @return true if everything is Ok.
     */
    private boolean isRequiredFieldsOk() {
        // Check and show all missing values
        String fieldRequired = getString(R.string.Required_field);
        boolean nameCheck = Utils.checkTextInputLayoutValueRequirement(nameInputWrapper, fieldRequired);
        boolean streetCheck = Utils.checkTextInputLayoutValueRequirement(streetInputWrapper, fieldRequired);
        boolean houseNumberCheck = Utils.checkTextInputLayoutValueRequirement(houseNumberInputWrapper, fieldRequired);
        boolean cityCheck = Utils.checkTextInputLayoutValueRequirement(cityInputWrapper, fieldRequired);
        boolean zipCheck = Utils.checkTextInputLayoutValueRequirement(zipInputWrapper, fieldRequired);
        boolean phoneCheck = Utils.checkTextInputLayoutValueRequirement(phoneInputWrapper, fieldRequired);
        boolean emailCheck = Utils.checkTextInputLayoutValueRequirement(emailInputWrapper, fieldRequired);

        if (nameCheck && streetCheck && houseNumberCheck && cityCheck && zipCheck && phoneCheck && emailCheck) {
            // Check if shipping and payment is selected
            if (selectedShipping == null) {
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Choose_shipping_method), MsgUtils.ToastLength.SHORT);
                scrollLayout.smoothScrollTo(0, deliveryShippingLayout.getTop());
                return false;
            }

            if (selectedPayment == null) {
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Choose_payment_method), MsgUtils.ToastLength.SHORT);
                scrollLayout.smoothScrollTo(0, deliveryShippingLayout.getTop());
                return false;
            }
            return true;
        } else {
            return false;
        }
    }


    private void prepareDeliveryLayout(View view) {
        deliveryProgressBar = (ProgressBar) view.findViewById(R.id.delivery_progress);

//        final View deliveryShippingBtn = view.findViewById(R.id.order_create_delivery_shipping_button);
//        final View deliveryPaymentBtn = view.findViewById(R.id.order_create_delivery_payment_button);

        this.deliveryShippingLayout = view.findViewById(R.id.order_create_delivery_shipping_layout);
        this.deliveryPaymentLayout = view.findViewById(R.id.order_create_delivery_payment_layout);

        selectedShippingNameTv = (TextView) view.findViewById(R.id.order_create_delivery_shipping_name);
        selectedShippingPriceTv = (TextView) view.findViewById(R.id.order_create_delivery_shipping_price);
        selectedPaymentNameTv = (TextView) view.findViewById(R.id.order_create_delivery_payment_name);
        selectedPaymentPriceTv = (TextView) view.findViewById(R.id.order_create_delivery_payment_price);

        deliveryShippingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShippingDialogFragment shippingDialogFragment = ShippingDialogFragment.newInstance(delivery, selectedShipping, new ShippingDialogInterface() {
                    @Override
                    public void onShippingSelected(Shipping shipping) {
                        // Save selected value
                        selectedShipping = shipping;

                        // Update shipping related values
                        showSelectedShipping(shipping);

                        // Continue for payment
                        selectedPayment = null;
                        selectedPaymentNameTv.setText(getString(R.string.Choose_payment_method));
                        selectedPaymentPriceTv.setText("");
                        deliveryPaymentLayout.performClick();
                    }
                });
                shippingDialogFragment.show(getFragmentManager(), ShippingDialogFragment.class.getSimpleName());
            }
        });

        deliveryPaymentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentDialogFragment paymentDialogFragment = PaymentDialogFragment.newInstance(selectedShipping, selectedPayment, new PaymentDialogInterface() {
                    @Override
                    public void onPaymentSelected(Payment payment) {
                        selectedPayment = payment;
                        showSelectedPayment(payment);
                    }
                });
                paymentDialogFragment.show(getFragmentManager(), "PaymentDialog");
            }
        });
    }

    /**
     * Show and update shipping related values.
     *
     * @param shipping values to show.
     */
    private void showSelectedShipping(Shipping shipping) {
        if (shipping != null && selectedShippingNameTv != null && selectedShippingPriceTv != null) {
            selectedShippingNameTv.setText(shipping.getName());
            if (shipping.getPrice() != 0) {
                selectedShippingPriceTv.setText(shipping.getPriceFormatted());
            } else {
                selectedShippingPriceTv.setText(getText(R.string.free));
            }

            // Set total order price
            orderTotalPrice = shipping.getTotalPrice();
            orderTotalPriceTv.setText(shipping.getTotalPriceFormatted());
            deliveryPaymentLayout.setVisibility(View.VISIBLE);
        } else {
            Timber.e("Showing selected shipping with null values.");
        }
    }


    /**
     * Show and update payment related values.
     *
     * @param payment values to show.
     */
    private void showSelectedPayment(Payment payment) {
        if (payment != null && selectedPaymentNameTv != null && selectedPaymentPriceTv != null) {
            selectedPaymentNameTv.setText(payment.getName());
            if (payment.getPrice() != 0) {
                selectedPaymentPriceTv.setText(payment.getPriceFormatted());
            } else {
                selectedPaymentPriceTv.setText(getText(R.string.free));
            }

            // Set total order price
            orderTotalPrice = payment.getTotalPrice();
            orderTotalPriceTv.setText(payment.getTotalPriceFormatted());
        } else {
            Timber.e("Showing selected payment with null values.");
        }
    }

    private void getUserCart() {
        final User user = SettingsMy.getActiveUser();
        if (user != null) {
            String url = String.format(EndPoints.CART, SettingsMy.getActualNonNullShop(getActivity()).getId());

            progressDialog.show();
            GsonRequest<Cart> getCart = new GsonRequest<>(Request.Method.GET, url, null, Cart.class,
                    new Response.Listener<Cart>() {
                        @Override
                        public void onResponse(@NonNull Cart cart) {
                            if (progressDialog != null) progressDialog.cancel();
                            refreshScreenContent(cart, user);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (progressDialog != null) progressDialog.cancel();
                    Timber.e("Get request cart error: %s", error.getMessage());
                    MsgUtils.logAndShowErrorMessage(getActivity(), error);
                    if (getActivity() instanceof MainActivity) ((MainActivity) getActivity()).onDrawerBannersSelected();
                }
            }, getFragmentManager(), user.getAccessToken());
            getCart.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            getCart.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(getCart, CONST.ORDER_CREATE_REQUESTS_TAG);
        } else {
            LoginExpiredDialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
            loginExpiredDialogFragment.show(getFragmentManager(), MSG_LOGIN_EXPIRED_DIALOG_FRAGMENT);
        }
    }

    private void refreshScreenContent(@NonNull Cart cart, User user) {
        this.cart = cart;
        List<CartProductItem> cartProductItems = cart.getItems();
        if (cartProductItems == null || cartProductItems.isEmpty()) {
            Timber.e(new RuntimeException(), "Received null cart during order creation.");
            if (getActivity() instanceof MainActivity) ((MainActivity) getActivity()).onDrawerBannersSelected();
        } else {

            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0; i < cartProductItems.size(); i++) {
                LinearLayout llRow = (LinearLayout) inflater.inflate(R.layout.order_create_cart_item, cartItemsLayout, false);
                TextView tvItemName = (TextView) llRow.findViewById(R.id.order_create_cart_item_name);
                tvItemName.setText(cartProductItems.get(i).getVariant().getName());
                TextView tvItemPrice = (TextView) llRow.findViewById(R.id.order_create_cart_item_price);
                tvItemPrice.setText(cartProductItems.get(i).getTotalItemPriceFormatted());
                TextView tvItemQuantity = (TextView) llRow.findViewById(R.id.order_create_cart_item_quantity);
                tvItemQuantity.setText(getString(R.string.format_quantity, cartProductItems.get(i).getQuantity()));
                TextView tvItemDetails = (TextView) llRow.findViewById(R.id.order_create_cart_item_details);
                tvItemDetails.setText(getString(R.string.format_string_division, cartProductItems.get(i).getVariant().getColor().getValue(),
                        cartProductItems.get(i).getVariant().getSize().getValue()));
                cartItemsLayout.addView(llRow);
            }
            if (cart.getDiscounts() != null) {
                for (int i = 0; i < cart.getDiscounts().size(); i++) {
                    LinearLayout llRow = (LinearLayout) inflater.inflate(R.layout.order_create_cart_item, cartItemsLayout, false);
                    TextView tvItemName = (TextView) llRow.findViewById(R.id.order_create_cart_item_name);
                    TextView tvItemPrice = (TextView) llRow.findViewById(R.id.order_create_cart_item_price);
                    tvItemName.setText(cart.getDiscounts().get(i).getDiscount().getName());
                    tvItemPrice.setText(cart.getDiscounts().get(i).getDiscount().getValueFormatted());
                    tvItemPrice.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    cartItemsLayout.addView(llRow);
                }
            }

            cartItemsTotalPrice.setText(cart.getTotalPriceFormatted());
            orderTotalPriceTv.setText(cart.getTotalPriceFormatted());

            // TODO pull to scroll could be cool here
            String url = String.format(EndPoints.CART_DELIVERY_INFO, SettingsMy.getActualNonNullShop(getActivity()).getId());

            deliveryProgressBar.setVisibility(View.VISIBLE);
            GsonRequest<DeliveryRequest> getDelivery = new GsonRequest<>(Request.Method.GET, url, null, DeliveryRequest.class,
                    new Response.Listener<DeliveryRequest>() {
                        @Override
                        public void onResponse(@NonNull DeliveryRequest deliveryResp) {
                            Timber.d("GetDelivery: %s", deliveryResp.toString());
                            delivery = deliveryResp.getDelivery();
                            deliveryProgressBar.setVisibility(View.GONE);
                            deliveryShippingLayout.setVisibility(View.VISIBLE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Timber.e("Get request cart error: %s", error.getMessage());
                    MsgUtils.logAndShowErrorMessage(getActivity(), error);

                    deliveryProgressBar.setVisibility(View.GONE);
                    if (getActivity() instanceof MainActivity) ((MainActivity) getActivity()).onDrawerBannersSelected();
                }
            }, getFragmentManager(), user.getAccessToken());
            getDelivery.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            getDelivery.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(getDelivery, CONST.ORDER_CREATE_REQUESTS_TAG);
        }
    }

    private void postOrder(final Order order) {
        final User user = SettingsMy.getActiveUser();
        if (user != null) {
            JSONObject jo;
            try {
                jo = JsonUtils.createOrderJson(order);
            } catch (JSONException e) {
                Timber.e(e, "Post order Json exception.");
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
                return;
            }

            Timber.d("Post order jo: %s", jo.toString());
            String url = String.format(EndPoints.ORDERS, SettingsMy.getActualNonNullShop(getActivity()).getId());

            progressDialog.show();
            postOrderRequest = new GsonRequest<>(Request.Method.POST, url, jo.toString(), Order.class, new Response.Listener<Order>() {
                @Override
                public void onResponse(Order order) {
                    Timber.d("response: %s", order.toString());
                    progressDialog.cancel();

                    Analytics.logOrderCreatedEvent(cart, order.getRemoteId(), orderTotalPrice, selectedShipping);

                    updateUserData(user, order);
                    MainActivity.updateCartCountNotification();

                    DialogFragment thankYouDF = OrderCreateSuccessDialogFragment.newInstance(false);
                    thankYouDF.show(getFragmentManager(), OrderCreateSuccessDialogFragment.class.getSimpleName());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.cancel();
                    // Return 501 for sample application.
                    if (postOrderRequest != null && postOrderRequest.getStatusCode() == 501) {
                        DialogFragment thankYouDF = OrderCreateSuccessDialogFragment.newInstance(true);
                        thankYouDF.show(getFragmentManager(), OrderCreateSuccessDialogFragment.class.getSimpleName());
                    } else {
                        MsgUtils.logAndShowErrorMessage(getActivity(), error);
                    }
                }
            }, getFragmentManager(), user.getAccessToken());
            postOrderRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            postOrderRequest.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(postOrderRequest, CONST.ORDER_CREATE_REQUESTS_TAG);
        } else {
            LoginExpiredDialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
            loginExpiredDialogFragment.show(getFragmentManager(), MSG_LOGIN_EXPIRED_DIALOG_FRAGMENT);
        }
    }

    /**
     * Update user information after successful order.
     *
     * @param user  actual user which will be updated
     * @param order order response for obtain user information
     */
    private void updateUserData(User user, Order order) {
        if (user != null) {
            if (order.getName() != null && !order.getName().isEmpty()) {
                user.setName(order.getName());
            }
            user.setEmail(order.getEmail());
            user.setPhone(order.getPhone());
            user.setCity(order.getCity());
            user.setStreet(order.getStreet());
            user.setZip(order.getZip());
            user.setHouseNumber(order.getHouseNumber());
            SettingsMy.setActiveUser(user);
        } else {
            Timber.e(new NullPointerException(), "Null user after successful order.");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        MyApplication.getInstance().cancelPendingRequests(CONST.ORDER_CREATE_REQUESTS_TAG);
        if (progressDialog != null) progressDialog.cancel();
        if (deliveryProgressBar != null) deliveryProgressBar.setVisibility(View.GONE);
    }
}
