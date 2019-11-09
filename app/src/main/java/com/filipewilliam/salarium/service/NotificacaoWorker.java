package com.filipewilliam.salarium.service;

import android.content.Context;
import android.support.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificacaoWorker extends Worker {

    public static String WORKER = "Notificacao";

    public NotificacaoWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        //NotificacaoService.
        return null;
    }
}
