package com.filipewilliam.salarium.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.activity.ContasVencerActivity;

public class NotificacaoService {

    public static final String UPDATE_CHANNEL_ID = "Contas à vencer";

    public static final int UPDATE_NOTIFICATION_ID = 1;

    public static final int NOTIFICATION_REQUEST_CODE = 50;

    public static final String CHANNEL_1_ID = "Contas à vencer";

    public static void showNotification(Context context, String channelId, int notificationId) {
        createNotificationChannel(context, channelId);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context, channelId)
                .setSmallIcon(R.drawable.ic_codigo_de_barras_boleto_branco)
                .setContentTitle("NÃO AGUENTO MAIS")
                .setContentText("Tomara que funcione")
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManagerCompat notificationManager = NotificationManagerCompat
                .from(context);
        notificationManager.notify(notificationId, mBuilder.build());
    }

    public static void createNotificationChannel(Context context, String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "tESTE";
            String description = "Muita String";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_1_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context
                    .getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void createPushNotification(Context context, String message) {
        NotificacaoService.createNotificationChannel(context, NotificacaoService.UPDATE_CHANNEL_ID);
        Intent notificationIntent = new Intent(context, ContasVencerActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        //notificationIntent.putExtra(FcmPushListenerService.EXTRAS_NOTIFICATION_DATA, message);
        PendingIntent contentIntent = PendingIntent
                .getActivity(context, NOTIFICATION_REQUEST_CODE, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context, NotificacaoService.UPDATE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_codigo_de_barras_boleto_branco)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat
                .from(context);
        notificationManager.notify(NotificacaoService.UPDATE_NOTIFICATION_ID, mBuilder.build());
    }

    public static void cancelAllNotification(Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat
                .from(context);
        notificationManager.cancelAll();
    }


}
