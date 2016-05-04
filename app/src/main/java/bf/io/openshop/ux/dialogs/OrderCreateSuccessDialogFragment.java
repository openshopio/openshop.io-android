package bf.io.openshop.ux.dialogs;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import bf.io.openshop.R;
import bf.io.openshop.ux.MainActivity;
import timber.log.Timber;


/**
 * Dialog display "Thank you" screen after order is finished.
 */
public class OrderCreateSuccessDialogFragment extends DialogFragment {

    private boolean sampleApplication = false;

    /**
     * Dialog display "Thank you" screen after order is finished.
     */
    public static OrderCreateSuccessDialogFragment newInstance(boolean sampleApplication) {
        OrderCreateSuccessDialogFragment orderCreateSuccessDialogFragment = new OrderCreateSuccessDialogFragment();
        orderCreateSuccessDialogFragment.sampleApplication = sampleApplication;
        return orderCreateSuccessDialogFragment;
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
        Timber.d("%s - OnCreateView", this.getClass().getSimpleName());
        View view = inflater.inflate(R.layout.dialog_order_create_success, container, false);

        Button okBtn = (Button) view.findViewById(R.id.order_create_success_ok);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof MainActivity)
                    ((MainActivity) getActivity()).onDrawerBannersSelected();
                dismiss();
            }
        });

        TextView title = (TextView) view.findViewById(R.id.order_create_success_title);
        TextView description = (TextView) view.findViewById(R.id.order_create_success_description);

        if (sampleApplication) {
            title.setText(R.string.This_is_a_sample_app);
            description.setTextColor(ContextCompat.getColor(getContext(), R.color.textSecondary));
            description.setText(R.string.Sample_app_description);
        } else {
            title.setText(R.string.Thank_you_for_your_order);
            description.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            description.setText(Html.fromHtml(getString(R.string.Wait_for_sms_or_email_order_confirmation)));
        }

        return view;
    }
}