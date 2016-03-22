package bf.io.openshop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import bf.io.openshop.entities.Shop;
import bf.io.openshop.entities.User;
import bf.io.openshop.utils.MsgUtils;
import bf.io.openshop.utils.Utils;
import bf.io.openshop.ux.SplashActivity;
import timber.log.Timber;

/**
 * Class providing app specific sharedPreference settings.
 */
public class SettingsMy {
    public static final String PREF_ACTUAL_SHOP = "pref_actual_shop";
    public static final String PREF_ACTIVE_USER = "pref_active_user";
    public static final String PREF_USER_EMAIL = "pref_user_email";
    public static final String PREF_VERSION_CODE = "version_code";
    public static final String SHOW_HINT = "show_hint";

    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";

    private static final String TAG = SettingsMy.class.getSimpleName();
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static Shop actualShop;
    private static User activeUser;
    private static SharedPreferences sharedPref;

    /**
     * Get actually selected shop.
     *
     * @return actual shop or null if not selected.
     */
    public static Shop getActualShop() {
        if (actualShop != null) {
            Timber.d(TAG + " - Returned actual shop");
            return actualShop;
        } else {
            SharedPreferences prefs = getSettings();
            String json = prefs.getString(PREF_ACTUAL_SHOP, "");
            if (json.isEmpty() || "null".equals(json)) {
                Timber.e(TAG + " - Returned null shop");
                return null;
            } else {
                actualShop = Utils.getGsonParser().fromJson(json, Shop.class);
                Timber.d(TAG + " - Returned shop from memory:" + actualShop.toString());
                return actualShop;
            }
        }
    }

    /**
     * Set actually selected shop.
     *
     * @param actualShop selected shop or null for disable selection.
     */
    public static void setActualShop(Shop actualShop) {
        if (actualShop != null)
            Timber.d(TAG + " - Set selected shop: " + actualShop.toString());
        else
            Timber.d(TAG + " - Disable selected shop");
        SettingsMy.actualShop = actualShop;

        String json = Utils.getGsonParser().toJson(SettingsMy.actualShop);
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(PREF_ACTUAL_SHOP, json);
        editor.apply();
    }

    /**
     * Get actually selected shop.
     * If actually selected shop is null, then return to {@link SplashActivity}
     *
     * @param activity corresponding activity.
     * @return actually selected shop or empty shop (when the app will be closed).
     */
    @NonNull
    public static Shop getActualNonNullShop(Activity activity) {
        Shop shop = getActualShop();
        if (shop == null) {
            if (activity != null) {
                MsgUtils.showToast(activity, MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.LONG);
                Intent intent = new Intent(activity.getApplicationContext(), SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
            } else {
                Timber.e("Null shop detected also with null activity parameter.");
            }
            // Return an empty shop to prevent a null pointer exception before intent is processed.
            return new Shop();
        } else {
            return shop;
        }
    }

    /**
     * Get active user info.
     *
     * @return user or null if nobody logged in.
     */
    public static User getActiveUser() {
        if (activeUser != null) {
            Timber.d(TAG + " - Returned active user");
            return activeUser;
        } else {
            SharedPreferences prefs = getSettings();
            String json = prefs.getString(PREF_ACTIVE_USER, "");
            if (json.isEmpty() || "null".equals(json)) {
                Timber.d(TAG + " - Returned null");
                return null;
            } else {
                activeUser = Utils.getGsonParser().fromJson(json, User.class);
                Timber.d(TAG + " - Returned active user from memory:" + activeUser.toString());
                return activeUser;
            }
        }
    }

    /**
     * Set active user.
     *
     * @param user active user or null for disable user.
     */
    public static void setActiveUser(User user) {
        if (user != null)
            Timber.d(TAG + " - Set active user with name: " + user.toString());
        else
            Timber.d(TAG + " - Deleting active user");
        SettingsMy.activeUser = user;

        String json = Utils.getGsonParser().toJson(SettingsMy.activeUser);
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(PREF_ACTIVE_USER, json);
        editor.apply();
    }

    /**
     * Get user email. Used for login purpose.
     *
     * @return email of last logged user.
     */
    public static String getUserEmailHint() {
        SharedPreferences prefs = getSettings();
        String userEmail = prefs.getString(PREF_USER_EMAIL, "");
        Timber.d(TAG + " - Obtained user email:" + userEmail);
        return userEmail;
    }

    /**
     * Set user email to preferences.
     * Used for login purpose.
     *
     * @param userEmail email of last logged user.
     */
    public static void setUserEmailHint(String userEmail) {
        Timber.d(TAG + " - Set user email: " + userEmail);
        putParam(PREF_USER_EMAIL, userEmail);
    }

    /**
     * Get indicator, that GCM token was sent to third party server.
     *
     * @return true if successfully received by third party server. False otherwise.
     */
    public static Boolean getTokenSentToServer() {
        SharedPreferences prefs = getSettings();
        boolean tokenSent = prefs.getBoolean(SENT_TOKEN_TO_SERVER, false);
        Timber.d(TAG + " - Obtained token sent to server:" + tokenSent);
        return tokenSent;
    }

    /**
     * Set GCM token sent to third party server indicator.
     *
     * @param tokenSent true if successfully received by server.
     */
    public static void setTokenSentToServer(boolean tokenSent) {
        putParam(SENT_TOKEN_TO_SERVER, tokenSent);
    }

    public static boolean isHintRequired() {
        return getSettings().getBoolean(SHOW_HINT, true);
    }

    public static void setHintRequired(boolean hintShowed) {
        putParam(SHOW_HINT, hintShowed);
    }

    /**
     * Obtain preferences instance.
     *
     * @return base instance of app SharedPreferences.
     */
    public static SharedPreferences getSettings() {
        if (sharedPref == null) {
            sharedPref = MyApplication.getInstance().getSharedPreferences(MyApplication.PACKAGE_NAME, Context.MODE_PRIVATE);
        }
        return sharedPref;
    }

    private static boolean putParam(String key, String value) {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(key, value);
        return editor.commit();
    }

    private static boolean putParam(String key, boolean value) {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    private static boolean putParam(String key, long value) {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    private static boolean putParam(String key, int value) {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putInt(key, value);
        return editor.commit();
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////// GcmUtils //////////////////////////////////////////////////////////////////////////


    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    ///////////////////////// END of GcmUtils //////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
}
