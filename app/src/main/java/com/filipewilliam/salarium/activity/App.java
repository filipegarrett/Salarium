package com.filipewilliam.salarium.activity;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.support.annotation.NonNull;

import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class App extends Application {

    /*
    * */

    public static final String CHANNEL_1_ID = "contas a vencer";

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        criarCanaisNotificacoes();
    }

    private void criarCanaisNotificacoes(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_1_ID, "Contas à vencer", NotificationManager.IMPORTANCE_HIGH);
            channel1.enableVibration(true);
            channel1.enableLights(true);
            channel1.setDescription("Contas à vencer");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);

        }
    }

}
