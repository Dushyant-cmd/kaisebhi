package com.kaisebhi.kaisebhi.Extras;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kaisebhi.kaisebhi.HomeActivity;
import com.kaisebhi.kaisebhi.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MyFirebaseMesgServices extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData() != null) {

            Map<String, String> params = remoteMessage.getData();
            JSONObject object = new JSONObject(params);

            try {
                String title = object.getString("title");
                String text = object.getString("message");
                Log.d("stitle",title);

                showNotification(title, text);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    void showNotification(String title, String message) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("fladoApp",
                    "FLADOAPP",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Flado DETAILS APP");
            mNotificationManager.createNotificationChannel(channel);
        }
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "fladoApp")
                .setContentTitle(title) // title for notification
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setContentText(message)
                .setContentText(message)// message for notification
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(largeIcon)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());
    }
}


