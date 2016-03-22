package bf.io.openshop.ux.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import bf.io.openshop.R;
import bf.io.openshop.ux.MainActivity;


public class OrderCreateSuccessDialogFragment extends DialogFragment {

    public static OrderCreateSuccessDialogFragment newInstance() {
        return new OrderCreateSuccessDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

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

        TextView description = (TextView) view.findViewById(R.id.order_create_success_description);
        description.setText(Html.fromHtml(getString(R.string.Wait_for_sms_or_email_order_confirmation)));

        builder.setView(view);
        return builder.create();
    }

}