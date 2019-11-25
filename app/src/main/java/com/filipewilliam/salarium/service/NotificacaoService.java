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

    public static void mostrarNotificacao(Context context, String channelId, int notificationId) {
        criarCanalNotificacoes(context, channelId);
        Intent intent = new Intent(context, ContasVencerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context, channelId)
                .setSmallIcon(R.drawable.ic_codigo_de_barras_boleto_branco)
                .setContentTitle("Não deixe atrasar!")
                .setContentText("Pague em dia para evitar juros!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat
                .from(context);
        notificationManager.notify(notificationId, mBuilder.build());
    }

    public static void criarCanalNotificacoes(Context context, String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "TESTE";
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

    public static void cancelarNotificacoes(Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat
                .from(context);
        notificationManager.cancelAll();
    }

}
