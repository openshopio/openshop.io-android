package bf.io.openshop.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import bf.io.openshop.BuildConfig;
import bf.io.openshop.R;
import bf.io.openshop.api.EndPoints;
import bf.io.openshop.ux.SplashActivity;
import timber.log.Timber;

public class MyGcmListenerService extends GcmListenerService {

    private static final int NOTIFICATION_ID = 6342806;

    private static final String TAG = "GcmListener";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Timber.d("From: %s", from);
        Timber.d("Message: %s", message);
        Timber.d("Bundle: %s", data.toString());

        // Topics not implemented.
//        if (from.startsWith("/topics/")) {
//            // message received from some topic.
//        } else {
//            // normal downstream message.
//        }

        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(data);
    }


    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param extras GCM message received.
     */
    private void sendNotification(Bundle extras) {
////    If needed parse shopId, for shop specific notifications. ////
//        String shopIdStr = extras.getString(EndPoints.NOTIFICATION_SHOP_ID, "0");
//        int shopId;
//        try {
//            shopId = Integer.parseInt(shopIdStr);
//        } catch (Exception e) {
//            shopId = 0;
//            if (BuildConfig.DEBUG) Log.e(TAG, "Failed parsing notification shop id");
//        }
//
//        if (BuildConfig.DEBUG) Log.d(TAG, "Shop id: " + shopId);


        // Get title and message text.
        String title = extras.getString(EndPoints.NOTIFICATION_TITLE, getString(R.string.app_name));
        String message = extras.getString(EndPoints.NOTIFICATION_MESSAGE);
        if (BuildConfig.DEBUG) Log.d(TAG, "Title: " + title);
        if (BuildConfig.DEBUG) Log.d(TAG, "Message: " + message);

        // Build notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notification_small)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_notification_big))
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        // Set big image if exist
        String imageUrl = extras.getString(EndPoints.NOTIFICATION_IMAGE_URL);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Bitmap bitmap = getBitmapFromURL(imageUrl);
            if (bitmap != null) {
                if (BuildConfig.DEBUG) Log.d(TAG, "Set big icon");
                mBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .setBigContentTitle(title)
                        .setSummaryText(message));
            } else {
                if (BuildConfig.DEBUG) Log.e(TAG, "Cannot download image");
            }
        }

        // Determine if start activity or web
        String link = extras.getString(EndPoints.NOTIFICATION_LINK);
        if (BuildConfig.DEBUG) Log.d(TAG, "Gcm linkType: " + link);
        Intent notificationIntent;
        if (link != null && link.contains("http")) {
            Uri url;
            try {
                url = Uri.parse(link);
            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.e(TAG, "Parsing notification url failed.");
                return;
            }
            notificationIntent = new Intent(Intent.ACTION_VIEW, url);
        } else {
            notificationIntent = new Intent(this, SplashActivity.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            notificationIntent.putExtra(EndPoints.NOTIFICATION_LINK, link);
            notificationIntent.putExtra(EndPoints.NOTIFICATION_TITLE, title);
        }

        String utmSource = "utm_source=API";
        String utmMedium = "utm_medium=notification";
        String utmCampaign = "utm_campaign=" + title;
        String utm = utmSource + "&" + utmMedium + "&" + utmCampaign;
        notificationIntent.putExtra(EndPoints.NOTIFICATION_UTM, utm);
        // Create notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);
        ((NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, mBuilder.build());
    }

    /**
     * Obtain image from server.
     *
     * @param stringURL server path to image.
     * @return image or null if error occurred.
     */
    private Bitmap getBitmapFromURL(String stringURL) {
        try {
            URL url = new URL(stringURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
