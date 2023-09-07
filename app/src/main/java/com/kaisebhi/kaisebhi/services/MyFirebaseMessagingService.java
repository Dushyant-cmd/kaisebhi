package com.kaisebhi.kaisebhi.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kaisebhi.kaisebhi.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String TAG = "FirebaseService.java";

    /**Below method will be called by android when it receives notification.
     * RemoteMessage class instance contains in data payload*/
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        showNotification();
    }

    private void showNotification() {
        try {
            String channelId = getPackageName();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, "notification",
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("description notification");

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, channelId);
            notification.setContentTitle("title");
            notification.setContentText("body text");
            notification.setPriority(Notification.PRIORITY_DEFAULT);
            notification.setSmallIcon(R.drawable.icon);

            NotificationManagerCompat compat = NotificationManagerCompat.from(this);
            compat.notify(59, notification.build());
        } catch (Exception e) {
            Log.d(TAG, "showNotification: " + e);
        }
    }

    /**Below method will be called by Firebase to android then android call this method
     * with new Firebase cloud messaging token. */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "onNewToken: " + token);
    }
}