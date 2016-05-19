package bf.io.openshop.utils;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import bf.io.openshop.MyApplication;
import bf.io.openshop.R;
import bf.io.openshop.SettingsMy;
import bf.io.openshop.api.EndPoints;
import bf.io.openshop.api.JsonRequest;
import bf.io.openshop.entities.Shop;
import bf.io.openshop.entities.User;
import timber.log.Timber;

public class MyRegistrationIntentService extends IntentService {

    private static final String TAG = "GcmRegistrationService";
    private static final String[] TOPICS = {"global"};

    /**
     * Creates an IntentService.
     */
    public MyRegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Timber.d("GCM token obtained: %s", token);

            // Send token to third party only if not already registered.
            if (!SettingsMy.getTokenSentToServer()) {
                boolean success = sendRegistrationToServer(token);

                // Subscribe to topic channels. Not implemented now.
                subscribeTopics(token);

                // You should store a boolean that indicates whether the generated token has been
                // sent to your server. If the boolean is false, send the token to your server,
                // otherwise your server should have already received the token.
                SettingsMy.setTokenSentToServer(success);
            }
            // [END register_for_gcm]
        } catch (Exception e) {
            Timber.e(e, "Failed to complete token refresh");
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            SettingsMy.setTokenSentToServer(false);
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(SettingsMy.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     * <p/>
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private boolean sendRegistrationToServer(String token) {
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    JSONObject errorData = new JSONObject(new String(error.networkResponse.data));
                    JSONArray body = errorData.getJSONArray("body");
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < body.length(); i++) {
                        sb.append(body.get(i));
                        sb.append("\n");
                    }
                    String result = sb.toString();
                    Timber.e("Error message: %s", result);
                } catch (Exception e) {
                    Timber.e(e, "GCM error response parsing failed");
                }
            }
        };

        JSONObject requestPost = new JSONObject();
        try {
            requestPost.put("platform", "android");
            requestPost.put("device_token", token);
            Shop shop = SettingsMy.getActualShop();
            if (shop != null) {
                String url = String.format(EndPoints.REGISTER_NOTIFICATION, shop.getId());
                JsonRequest req;
                User activeUser = SettingsMy.getActiveUser();
                if (activeUser != null) {
                    Timber.d("GCM registration send: authorized");
                    req = new JsonRequest(Request.Method.POST, url, requestPost, future, errorListener, null, activeUser.getAccessToken());
                } else {
                    Timber.d("GCM registration send: non-authorized");
                    req = new JsonRequest(Request.Method.POST, url, requestPost, future, errorListener);
                }
                req.setRetryPolicy(MyApplication.getDefaultRetryPolice());
                req.setShouldCache(false);
                MyApplication.getInstance().addToRequestQueue(req, "no_cancel_tag");
            } else {
                Timber.e("Register notification failed - null actual shop.");
                return false;
            }
        } catch (Exception e) {
            Timber.d(e, "Register notification failed.");
            return false;
        }

        try {
            // Called only on success. If request contains error, timeout will be called.
            JSONObject response = future.get(30, TimeUnit.SECONDS);
            Timber.d("GCM registration success: %s", response.toString());
            return true;
        } catch (InterruptedException e) {
            Timber.e(e, "Register device api call interrupted.");
            errorListener.onErrorResponse(new VolleyError(e));
        } catch (ExecutionException e) {
            Timber.e(e, "Register device api call failed.");
            errorListener.onErrorResponse(new VolleyError(e));
        } catch (TimeoutException e) {
            Timber.e(e, "Register device api call timed out.");
            errorListener.onErrorResponse(new VolleyError(e));
        }
        return false;
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        // Topics not used now.
//        GcmPubSub pubSub = GcmPubSub.getInstance(this);
//        for (String topic : TOPICS) {
//            pubSub.subscribe(token, "/topics/" + topic, null);
//        }
    }
    // [END subscribe_topics]
}
