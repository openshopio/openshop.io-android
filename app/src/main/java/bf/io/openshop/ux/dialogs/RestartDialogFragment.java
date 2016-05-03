package bf.io.openshop.ux.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import bf.io.openshop.R;
import bf.io.openshop.SettingsMy;
import bf.io.openshop.entities.Shop;
import bf.io.openshop.utils.Analytics;
import bf.io.openshop.ux.RestartAppActivity;
import timber.log.Timber;

public class RestartDialogFragment extends DialogFragment {

    private Shop newShopSelected;

    public static RestartDialogFragment newInstance(Shop selectedShop) {
        RestartDialogFragment frag = new RestartDialogFragment();
        frag.newShopSelected = selectedShop;
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Timber.d("%s - OnCreateView", this.getClass().getSimpleName());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.alertDialogNoTitle);
        builder.setTitle(R.string.Restart);
        builder.setMessage(R.string.App_needs_to_be_restarted);

        builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Analytics.logShopChange(SettingsMy.getActualNonNullShop(getActivity()), newShopSelected);
                Analytics.deleteAppTrackers();

                // Log out user, change actual shop and restart
                SettingsMy.setActiveUser(null);
                SettingsMy.setActualShop(newShopSelected);
                Intent i = new Intent(getActivity(), RestartAppActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(i);
                getActivity().finish();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

}
