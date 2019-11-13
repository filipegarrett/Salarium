package com.filipewilliam.salarium.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.filipewilliam.salarium.activity.App.CHANNEL_1_ID;

public class MyNotificationsBroadcaster extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context. NOTIFICATION_SERVICE ) ;
        Notification notification = intent.getParcelableExtra(NOTIFICATION) ;
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_1_ID, "Contas à vencer", NotificationManager.IMPORTANCE_HIGH);
            channel1.enableVibration(true);
            channel1.enableLights(true);
            channel1.setDescription("Contas à vencer");

            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel1);
        }
        int id = intent.getIntExtra(NOTIFICATION_ID, 0) ;
        assert notificationManager != null;
        notificationManager.notify(id , notification) ;

    }
}
