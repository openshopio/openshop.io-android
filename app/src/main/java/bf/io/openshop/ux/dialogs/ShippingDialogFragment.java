package bf.io.openshop.ux.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import bf.io.openshop.CONST;
import bf.io.openshop.MyApplication;
import bf.io.openshop.R;
import bf.io.openshop.SettingsMy;
import bf.io.openshop.api.EndPoints;
import bf.io.openshop.api.GsonRequest;
import bf.io.openshop.entities.delivery.Branch;
import bf.io.openshop.entities.delivery.BranchesRequest;
import bf.io.openshop.entities.delivery.Delivery;
import bf.io.openshop.entities.delivery.DeliveryType;
import bf.io.openshop.entities.delivery.Shipping;
import bf.io.openshop.interfaces.ShippingDialogInterface;
import bf.io.openshop.listeners.OnSingleClickListener;
import bf.io.openshop.utils.MsgUtils;
import bf.io.openshop.ux.adapters.BranchesAdapter;
import bf.io.openshop.ux.adapters.ShippingSpinnerAdapter;
import timber.log.Timber;

/**
 * Dialog offers a shipping type selection or display all available shop branches with details and an interactive map.
 */
public class ShippingDialogFragment extends DialogFragment {

    private ProgressBar progressBar;

    private ShippingDialogInterface shippingDialogInterface;
    private Delivery delivery;

    private Fragment thisFragment;
    private View shippingEmpty;
    private ListView shippingList;
    private Shipping selectedShippingType;

    /**
     * Creates dialog which handles the shipping type selection.
     *
     * @param delivery                object with list of all possible shipping types.
     * @param selectedShipping        shipping which should be highlighted.
     * @param shippingDialogInterface listener indicating events that occurred.
     * @return new instance of dialog.
     */
    public static ShippingDialogFragment newInstance(Delivery delivery, Shipping selectedShipping, ShippingDialogInterface shippingDialogInterface) {
        ShippingDialogFragment frag = new ShippingDialogFragment();
        frag.delivery = delivery;
        frag.selectedShippingType = selectedShipping;
        frag.shippingDialogInterface = shippingDialogInterface;
        return frag;
    }

    /**
     * Creates dialog which show only existing branches.
     *
     * @param shippingDialogInterface listener indicating events that occurred.
     * @return new instance of dialog.
     */
    public static ShippingDialogFragment newInstance(ShippingDialogInterface shippingDialogInterface) {
        ShippingDialogFragment frag = new ShippingDialogFragment();
        frag.delivery = null;
        frag.shippingDialogInterface = shippingDialogInterface;
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisFragment = this;
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.dialogFullscreen);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Window window = d.getWindow();
            window.setLayout(width, height);
            window.setWindowAnimations(R.style.alertDialogAnimation);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - OnCreateView", this.getClass().getSimpleName());
        View view = inflater.inflate(R.layout.shipping_delivery, container, false);

        shippingList = (ListView) view.findViewById(R.id.shipping_dialog_list);
        progressBar = (ProgressBar) view.findViewById(R.id.shipping_dialog_progressBar);
        shippingEmpty = view.findViewById(R.id.shipping_dialog_empty);
        TextView dialogTitle = (TextView) view.findViewById(R.id.shipping_dialog_title);
        View closeBtn = view.findViewById(R.id.shipping_dialog_close);
        closeBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                dismiss();
            }
        });

        if (delivery == null) {
            dialogTitle.setText(R.string.Personal_pickup);
            getOnlyBranches();
        } else {
            setContentVisible(true);
            if (delivery != null) {
                final List<DeliveryType> deliveryTypes = new ArrayList<>();

                if (delivery.getShipping() != null && !delivery.getShipping().isEmpty()) {
                    DeliveryType deliveryType = new DeliveryType(CONST.DEFAULT_EMPTY_ID, getString(R.string.Shipping));
                    deliveryType.setShippingList(delivery.getShipping());
                    deliveryTypes.add(deliveryType);
                }

                if (delivery.getPersonalPickup() != null && !delivery.getPersonalPickup().isEmpty()) {
                    DeliveryType deliveryType = new DeliveryType(1, getString(R.string.Personal_pickup));
                    deliveryType.setShippingList(delivery.getPersonalPickup());
                    deliveryTypes.add(deliveryType);
                }

                final ShippingSpinnerAdapter deliverySpinnerAdapter = new ShippingSpinnerAdapter(getActivity(), this);
                deliverySpinnerAdapter.setData(deliveryTypes);
                deliverySpinnerAdapter.preselectShipping(selectedShippingType);

                shippingList.setAdapter(deliverySpinnerAdapter);
                shippingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Shipping selectedShipping = deliverySpinnerAdapter.getItem(position);
                        onShippingSelected(selectedShipping);
                    }
                });
            }
        }

        return view;
    }

    public void onShippingSelected(Shipping selectedShipping) {
        if (shippingDialogInterface != null)
            shippingDialogInterface.onShippingSelected(selectedShipping);
        Timber.d("Shipping click: %s", selectedShipping.toString());
        dismiss();
    }

    private void getOnlyBranches() {
        String url = String.format(EndPoints.BRANCHES, SettingsMy.getActualNonNullShop(getActivity()).getId());

        GsonRequest<BranchesRequest> getCart = new GsonRequest<>(Request.Method.GET, url, null, BranchesRequest.class,
                new Response.Listener<BranchesRequest>() {
                    @Override
                    public void onResponse(@NonNull BranchesRequest response) {
                        Timber.d("GetBranches response: %s", response.toString());
                        setContentVisible(true);

                        if (response.getBranches() != null && response.getBranches().size() >= 0) {
                            shippingEmpty.setVisibility(View.GONE);
                            shippingList.setVisibility(View.VISIBLE);
                            final BranchesAdapter branchesAdapter = new BranchesAdapter(getActivity(), response.getBranches());

                            shippingList.setAdapter(branchesAdapter);
                            shippingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                private long mLastClickTime = 0;

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000)
                                        return;
                                    mLastClickTime = SystemClock.elapsedRealtime();

                                    Branch branch = (Branch) shippingList.getItemAtPosition(position);
                                    if (branch != null) {
                                        FragmentManager fm = thisFragment.getFragmentManager();
                                        MapDialogFragment mapDialog = MapDialogFragment.newInstance(branch);
                                        mapDialog.setRetainInstance(true);
                                        mapDialog.show(fm, MapDialogFragment.class.getSimpleName());
                                    }
                                }
                            });
                        } else {
                            shippingEmpty.setVisibility(View.VISIBLE);
                            shippingList.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Timber.e("Get branches error: %s", error.getMessage());
                setContentVisible(true);
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
            }
        });
        getCart.setRetryPolicy(MyApplication.getDefaultRetryPolice());
        getCart.setShouldCache(false);
        MyApplication.getInstance().addToRequestQueue(getCart, CONST.DELIVERY_DIALOG_REQUESTS_TAG);
    }

    private void setContentVisible(boolean visible) {
        if (visible) {
            progressBar.setVisibility(View.GONE);
            shippingList.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            shippingList.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStop() {
        MyApplication.getInstance().cancelPendingRequests(CONST.DELIVERY_DIALOG_REQUESTS_TAG);
        super.onStop();
    }
}
