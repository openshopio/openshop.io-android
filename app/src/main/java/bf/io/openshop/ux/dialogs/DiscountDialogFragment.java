package bf.io.openshop.ux.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import bf.io.openshop.CONST;
import bf.io.openshop.MyApplication;
import bf.io.openshop.R;
import bf.io.openshop.SettingsMy;
import bf.io.openshop.api.EndPoints;
import bf.io.openshop.api.JsonRequest;
import bf.io.openshop.entities.User;
import bf.io.openshop.interfaces.RequestListener;
import bf.io.openshop.utils.JsonUtils;
import bf.io.openshop.utils.MsgUtils;
import timber.log.Timber;

/**
 * Dialog provides a discount adding functionality.
 */
public class DiscountDialogFragment extends DialogFragment {

    private RequestListener requestListener;
    private TextInputLayout discountCodeInput;
    private View progressLayout;

    /**
     * Creates dialog which handles the discount adding functionality.
     *
     * @param requestListener listener indicating events that occurred.
     * @return new instance of dialog.
     */
    public static DiscountDialogFragment newInstance(RequestListener requestListener) {
        DiscountDialogFragment frag = new DiscountDialogFragment();
        frag.requestListener = requestListener;
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.dialogNoTitle);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), getTheme()) {
            @Override
            public void dismiss() {
                // Remove soft keyboard
                if (getActivity() != null && getView() != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                }

                requestListener = null;

                super.dismiss();
            }
        };
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.dialogFragmentAnimation);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - OnCreateView", this.getClass().getSimpleName());
        View view = inflater.inflate(R.layout.dialog_discount_fragment, container, false);

        progressLayout = view.findViewById(R.id.discount_code_progress);
        discountCodeInput = (TextInputLayout) view.findViewById(R.id.discount_code_input_wrapper);
        Button confirmDiscountCode = (Button) view.findViewById(R.id.discount_code_confirm);
        confirmDiscountCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRequiredFieldsOk()) {
                    sendDiscountCode(discountCodeInput.getEditText());
                }
            }
        });

        View closeBtn = view.findViewById(R.id.discount_code_close);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    private void sendDiscountCode(EditText discountCodeInput) {
        User user = SettingsMy.getActiveUser();
        if (user != null) {
            String url = String.format(EndPoints.CART_DISCOUNTS, SettingsMy.getActualNonNullShop(getActivity()).getId());

            JSONObject jo = new JSONObject();
            try {
                jo.put(JsonUtils.TAG_CODE, discountCodeInput.getText().toString().trim());
            } catch (JSONException e) {
                Timber.e(e, "Creating code json failed");
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
                return;
            }
            Timber.d("Sending discount code: %s", jo.toString());

            progressLayout.setVisibility(View.VISIBLE);
            final JsonRequest req = new JsonRequest(Request.Method.POST, url, jo, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Timber.d("Update item in cart: %s", response.toString());
                    MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Ok), MsgUtils.ToastLength.SHORT);
                    if (requestListener != null) requestListener.requestSuccess(0);

                    // Don't have to hide progress, because of dismiss.
                    dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (requestListener != null) requestListener.requestFailed(error);

                    // Don't have to hide progress, because of dismiss.
                    dismiss();
                }
            }, getFragmentManager(), user.getAccessToken());

            req.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            req.setShouldCache(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MyApplication.getInstance().addToRequestQueue(req, CONST.CART_DISCOUNTS_REQUESTS_TAG);
                }
            }, 150);
        } else {
            LoginExpiredDialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
            loginExpiredDialogFragment.show(getFragmentManager(), "loginExpiredDialogFragment");
        }
    }

    /**
     * Check if user set all required fields.
     *
     * @return true if ok.
     */
    private boolean isRequiredFieldsOk() {
        boolean discountCode = false;

        if (discountCodeInput.getEditText() == null || discountCodeInput.getEditText().getText().toString().equalsIgnoreCase("")) {
            discountCodeInput.setErrorEnabled(true);
            discountCodeInput.setError(getString(R.string.Required_field));
        } else {
            Timber.d("Some fields are required.");
            discountCodeInput.setErrorEnabled(false);
            discountCode = true;
        }
        return discountCode;
    }

    @Override
    public void onStop() {
        MyApplication.getInstance().getRequestQueue().cancelAll(CONST.CART_DISCOUNTS_REQUESTS_TAG);
        super.onStop();
    }
}
