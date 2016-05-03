package bf.io.openshop.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import bf.io.openshop.R;
import timber.log.Timber;

public class MsgUtils {

    public static final int TOAST_TYPE_MESSAGE = 1;
    public static final int TOAST_TYPE_INTERNAL_ERROR = 2;
    public static final int TOAST_TYPE_NO_NETWORK = 3;
    public static final int TOAST_TYPE_NO_SIZE_SELECTED = 5;

    public static void logErrorMessage(VolleyError error) {
        try {
            String errorData = new String(error.networkResponse.data);
            showMessage(null, new JSONObject(errorData));
        } catch (Exception e) {
            if (error.getMessage() != null && !error.getMessage().isEmpty())
                Timber.e(e, error.getMessage());
            else
                Timber.e(e, "Cannot parse error message");
        }
    }

    public static void logAndShowErrorMessage(Activity activity, VolleyError error) {
        try {
            String errorData = new String(error.networkResponse.data);
            showMessage(activity, new JSONObject(errorData));
        } catch (Exception e) {
            if (error.getMessage() != null && !error.getMessage().isEmpty())
                Timber.e(e, error.getMessage());
            else
                Timber.e(e, "Cannot parse error message");
            showToast(activity, TOAST_TYPE_INTERNAL_ERROR, null, ToastLength.SHORT);
        }
    }

    public static void showMessage(Activity activity, JSONObject message) {
        try {
            JSONArray body = message.getJSONArray("body");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < body.length(); i++) {
                sb.append(body.get(i));
                sb.append("\n");
            }
            String result = sb.toString();
            Timber.e("Error message: %s", result);
            if (activity != null)
                MsgUtils.showToast(activity, TOAST_TYPE_MESSAGE, result, ToastLength.LONG);
        } catch (Exception e) {
            Timber.e(e, "ShowMessage exception");
            if (activity != null)
                MsgUtils.showToast(activity, TOAST_TYPE_INTERNAL_ERROR, null, ToastLength.SHORT);
        }
    }

    /**
     * Show custom Toast Message.
     *
     * @param activity  Activity for show toast.
     * @param toastType Type of toast.
     * @param message   String to show.
     */
    public static void showToast(Activity activity, int toastType, String message, ToastLength toastLength) {
        if (activity == null) {
            Timber.e(new RuntimeException(), "Called showToast with null activity.");
            return;
        }
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_custom, (ViewGroup) activity.findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.toast_text);
        ImageView iv = (ImageView) layout.findViewById(R.id.toast_image);
        String str = "";
        int icon = 0;

        Toast toast = new Toast(activity);
        switch (toastLength) {
            case SHORT:
                toast.setDuration(Toast.LENGTH_SHORT);
                break;
            case LONG:
                toast.setDuration(Toast.LENGTH_LONG);
                break;
            default:
                Timber.e("Not implemented");
        }

        switch (toastType) {
            case TOAST_TYPE_MESSAGE:
                str = message;
                break;
            case TOAST_TYPE_INTERNAL_ERROR:
                str = activity.getString(R.string.Internal_error);
                break;
            case TOAST_TYPE_NO_NETWORK:
                str = activity.getString(R.string.No_network_connection);
                break;
            case TOAST_TYPE_NO_SIZE_SELECTED:
                str = activity.getString(R.string.Please_select_a_size);
                break;
        }

        text.setText(str);
        if (icon != 0) {
            iv.setImageResource(icon);
            iv.setVisibility(View.VISIBLE);
        } else {
            iv.setVisibility(View.GONE);
        }

        toast.setView(layout);
        toast.show();
    }

    public enum ToastLength {
        SHORT, LONG
    }
}
