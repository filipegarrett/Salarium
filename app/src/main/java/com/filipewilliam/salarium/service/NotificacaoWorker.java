package com.filipewilliam.salarium.service;

import android.content.Context;
import android.support.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificacaoWorker extends Worker {

    public static String WORKER = "NotificacaoWorker";
    public Context context;

    public NotificacaoWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;

    }

    @NonNull
    @Override
    public Result doWork() {

        NotificacaoService.mostrarNotificacao(getApplicationContext(), NotificacaoService.UPDATE_CHANNEL_ID, NotificacaoService.UPDATE_NOTIFICATION_ID);

        return Result.success();

    }
}
