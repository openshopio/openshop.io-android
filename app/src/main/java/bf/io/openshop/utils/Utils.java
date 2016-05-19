package bf.io.openshop.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Browser;
import android.support.design.widget.TextInputLayout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import bf.io.openshop.R;
import bf.io.openshop.entities.filtr.DeserializerFilters;
import bf.io.openshop.entities.filtr.Filters;
import timber.log.Timber;

public class Utils {

    private static Gson gson;

    private Utils() {}

    /**
     * Add specific parsing to gson
     *
     * @return new instance of {@link Gson}
     */
    public static Gson getGsonParser() {
        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Filters.class, new DeserializerFilters());
            gson = gsonBuilder.create();
        }
        return gson;
    }

    /**
     * Generate top layer progress indicator.
     *
     * @param context    activity context
     * @param cancelable can be progress layer canceled
     * @return dialog
     */
    public static ProgressDialog generateProgressDialog(Context context, boolean cancelable) {
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.ProgressTheme);
        progressDialog.setMessage(context.getString(R.string.Loading));
        progressDialog.setCancelable(cancelable);
        return progressDialog;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public static boolean checkPlayServices(Activity activity) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            Timber.e("Google play services don't working.");
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, 9000)
                        .show();
            } else {
                Timber.e("GCM - This device is not supported.");
            }
            return false;
        }
        return true;
    }

    /**
     * Method converts iso date string to better readable form.
     *
     * @param isoDate input iso date. Example: "2016-04-13 13:21:04".
     * @return processed date string.
     */
    public static String parseDate(String isoDate) {
        try {
            String[] parts = isoDate.split("-");

            String year = parts[0];
            String month = parts[1];

            String dayTemp = parts[2];
            String[] parts2 = dayTemp.split(" ");
            String day = parts2[0].trim();
            if (day.length() > 2) throw new RuntimeException("String with day number unexpected length.");

            return day + "." + month + "." + year;
        } catch (Exception e) {
            Timber.e(e, "Parsing order date created failed.");
            return isoDate;
        }
    }

    /**
     * Method replace ordinary {@link URLSpan} with {@link DefensiveURLSpan}.
     *
     * @param spannedText text, where link spans should be replaced.
     * @param activity    activity for displaying problems.
     * @return text, where link spans are replaced.
     */
    public static SpannableString safeURLSpanLinks(Spanned spannedText, Activity activity) {
        final SpannableString current = new SpannableString(spannedText);
        final URLSpan[] spans = current.getSpans(0, current.length(), URLSpan.class);
        int start, end;

        for (URLSpan span : spans) {
            start = current.getSpanStart(span);
            end = current.getSpanEnd(span);
            current.removeSpan(span);
            current.setSpan(new DefensiveURLSpan(span.getURL(), activity), start, end, 0);
        }
        return current;
    }

    public static int dpToPx(Context context, int dp) {
        return Math.round(dp * getPixelScaleFactor(context));
    }

    public static int pxToDp(Context context, int px) {
        return Math.round(px / getPixelScaleFactor(context));
    }

    private static float getPixelScaleFactor(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT;
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable)
            return ((BitmapDrawable) drawable).getBitmap();

        // We ask for the bounds if they have been set as they would be most
        // correct, then we check we are  > 0
        final int width = !drawable.getBounds().isEmpty() ? drawable.getBounds().width() : drawable.getIntrinsicWidth();

        final int height = !drawable.getBounds().isEmpty() ? drawable.getBounds().height() : drawable.getIntrinsicHeight();

        // Now we check we are > 0
        final Bitmap bitmap = Bitmap.createBitmap(width <= 0 ? 1 : width, height <= 0 ? 1 : height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * Method calculates the percentage discounts.
     *
     * @param context       simple context.
     * @param price         Base product price.
     * @param discountPrice Product price after discount.
     * @return percentage discount with percent symbol.
     */
    public static String calculateDiscountPercent(Context context, double price, double discountPrice) {
        int percent;
        if (discountPrice >= price) {
            percent = 0;
        } else {
            percent = (int) Math.round(100 - ((discountPrice / price) * 100));
        }
        return String.format(context.getString(R.string.format_price_discount_percents), percent);
    }

    /**
     * Check if textInputLayout contains editText view. If so, then set text value to the view.
     *
     * @param textInputLayout wrapper for the editText view where the text value should be set.
     * @param text            text value to display.
     */
    public static void setTextToInputLayout(TextInputLayout textInputLayout, String text) {
        if (textInputLayout != null && textInputLayout.getEditText() != null) {
            textInputLayout.getEditText().setText(text);
        } else {
            Timber.e("Setting text to null input wrapper, or without editText");
        }
    }

    /**
     * Check if textInputLayout contains editText view. If so, then return text value of the view.
     *
     * @param textInputLayout wrapper for the editText view.
     * @return text value of the editText view.
     */
    public static String getTextFromInputLayout(TextInputLayout textInputLayout) {
        if (textInputLayout != null && textInputLayout.getEditText() != null) {
            return textInputLayout.getEditText().getText().toString();
        } else {
            return null;
        }
    }


    /**
     * Method checks if text input layout exist and contains some value.
     * If layout is empty, then show error value under the textInputLayout.
     *
     * @param textInputLayout textInputFiled for check.
     * @param errorValue      value displayed when ext input is empty.
     * @return true if everything ok.
     */
    public static boolean checkTextInputLayoutValueRequirement(TextInputLayout textInputLayout, String errorValue) {
        if (textInputLayout != null && textInputLayout.getEditText() != null) {
            String text = Utils.getTextFromInputLayout(textInputLayout);
            if (text == null || text.isEmpty()) {
                textInputLayout.setErrorEnabled(true);
                textInputLayout.setError(errorValue);
                Timber.d("Input field %s missing text.", textInputLayout.getHint());
                return false;
            } else {
                textInputLayout.setErrorEnabled(false);
                Timber.d("Input field: %s OK.", textInputLayout.getHint());
                return true;
            }
        } else {
            Timber.e(new RuntimeException(), "Checking null input field during order send.");
            return false;
        }
    }

    /**
     * URLSpan which handles bad url format exception.
     */
    private static class DefensiveURLSpan extends URLSpan {

        Activity activity;

        public DefensiveURLSpan(String url, Activity activity) {
            super(url);
            this.activity = activity;
        }

        @Override
        public void onClick(View widget) {
            Uri uri = Uri.parse(getURL());
            Context context = widget.getContext();
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                if (activity != null && !activity.isFinishing()) {
                    MsgUtils.showToast(activity, MsgUtils.TOAST_TYPE_MESSAGE, activity.getString(R.string.Link_is_invalid), MsgUtils.ToastLength.SHORT);
                    Timber.e(e, "Invoked invalid web link: %s", uri);
                }
            }
        }
    }
}
