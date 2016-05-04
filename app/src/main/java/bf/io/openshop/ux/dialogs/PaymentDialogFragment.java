package bf.io.openshop.ux.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import bf.io.openshop.R;
import bf.io.openshop.entities.delivery.Payment;
import bf.io.openshop.entities.delivery.Shipping;
import bf.io.openshop.interfaces.PaymentDialogInterface;
import bf.io.openshop.listeners.OnSingleClickListener;
import bf.io.openshop.ux.adapters.PaymentSpinnerAdapter;
import timber.log.Timber;

/**
 * Dialog offers a payment type selection. It is used during order creation.
 */
public class PaymentDialogFragment extends DialogFragment {

    private PaymentDialogInterface paymentDialogInterface;
    private Shipping selectedShipping;
    private Payment selectedPaymentType;

    /**
     * Creates dialog which handles the payment type selection.
     *
     * @param selectedShipping       object with list of all possible payment types.
     * @param selectedPayment        payment which should be highlighted.
     * @param paymentDialogInterface listener indicating events that occurred.  @return new instance of dialog.
     */
    public static PaymentDialogFragment newInstance(Shipping selectedShipping, Payment selectedPayment, PaymentDialogInterface paymentDialogInterface) {
        PaymentDialogFragment frag = new PaymentDialogFragment();
        frag.paymentDialogInterface = paymentDialogInterface;
        frag.selectedShipping = selectedShipping;
        frag.selectedPaymentType = selectedPayment;
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        View view = inflater.inflate(R.layout.payment_dialog, container, false);

        ListView paymentList = (ListView) view.findViewById(R.id.payment_dialog_list);
        View closeBtn = view.findViewById(R.id.payment_dialog_close);
        closeBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                dismiss();
            }
        });

        if (selectedShipping != null) {
            final PaymentSpinnerAdapter paymentSpinnerAdapter = new PaymentSpinnerAdapter(getActivity(), selectedShipping.getPayment());
            paymentSpinnerAdapter.preselectPayment(selectedPaymentType);
            paymentList.setAdapter(paymentSpinnerAdapter);
            paymentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Payment selectedPayment = paymentSpinnerAdapter.getItem(position);
                    if (paymentDialogInterface != null)
                        paymentDialogInterface.onPaymentSelected(selectedPayment);
                    Timber.d("Payment click: %s", selectedPayment.toString());
                    dismiss();
                }
            });
        }
        return view;
    }
}
