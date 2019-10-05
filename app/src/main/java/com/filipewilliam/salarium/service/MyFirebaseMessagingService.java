package com.filipewilliam.salarium.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.activity.ContasVencerActivity;
import com.filipewilliam.salarium.model.ContasVencer;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage notificacao) {
        super.onMessageReceived(notificacao);


        if( notificacao.getNotification() != null ){

            String titulo = notificacao.getNotification().getTitle();
            String corpo = notificacao.getNotification().getBody();

            enviarNotificacao(titulo, corpo);

            //Log.i("Notificacao", "recebida titulo: " + titulo + " corpo: " + corpo );

        }

    }

    private void enviarNotificacao(String titulo, String corpo){

        //Configuraçõe para notificação
        String canal = getString(R.string.default_notification_channel_id);
        Uri uriSom = RingtoneManager.getDefaultUri( RingtoneManager.TYPE_NOTIFICATION );
        Intent intent = new Intent(this, ContasVencerActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        //Criar notificação
        NotificationCompat.Builder notificacao = new NotificationCompat.Builder(this, canal)
                .setContentTitle( titulo )
                .setContentText( corpo )
                .setSmallIcon( R.drawable.ic_codigo_de_barras_boleto_branco )
                .setSound( uriSom )
                .setAutoCancel( true )
                .setContentIntent( pendingIntent );

        //Recupera notificationManager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //Verifica versão do Android a partir do Oreo para configurar canal de notificação
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
            NotificationChannel channel = new NotificationChannel(canal, "canal", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel( channel );
        }

        //Envia notificação
        notificationManager.notify(0, notificacao.build() );

    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fb", s).apply();

        //Salvar token


        //Nexus
        //dc5KcrCc74A:APA91bHN8IbdHzevNu3sOu5kc3deJkmpAWX2hhs3silz9N4JnrlcoF0IH3rbBDokyBTTkVgis4Y-_vOj_BnbuGN3BN-B8GBgJQiI2X5g9kb0bWMJmdRalKsVo4SvDgp9aESg-tUonj6o

        //Pixel
        //cJ2e1VfOW68:APA91bE9qnKNIjdwZ9PENiOdbQeERQDYZzu3EZ6NC9Ctt2WE2HDeHIrBaPi5OWZkA9OgkJslVmEDm7D3Pny-cK26A0iZBREH24CEcis31Jo4YszPJVqdjFfzb23j-Fhy_R6o4rL1-RbW

        Log.i("onNewToken", "onNewToken: " + s );
    }

    public static String retornaToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty");
    }


}
