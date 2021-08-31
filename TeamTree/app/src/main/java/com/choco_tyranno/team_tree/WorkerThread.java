package com.choco_tyranno.team_tree;

import android.os.Handler;
import android.os.Looper;

public class WorkerThread extends Thread {
    private Handler workerHandler = new Handler(Looper.getMainLooper());

    public Handler getWorkerHandler(){
        return workerHandler;
    }
}
