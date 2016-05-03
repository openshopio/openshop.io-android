package bf.io.openshop.ux.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.View;
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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Timber.d("%s - OnCreateDialog", this.getClass().getSimpleName());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.alertDialogNoTitle);

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_order_create_success, null);
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

        builder.setView(view);
        return builder.create();
    }

}