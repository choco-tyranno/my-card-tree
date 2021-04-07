package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.choco_tyranno.mycardtree.card_crud_feature.utils.WorkerThreads;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class MyAppCompatActivity extends AppCompatActivity {
    MyAppCompatActivity() {
        Log.d("!!!:","MyAppCompatActivity constructor in");
//        defaultSettings();
    }

    public void defaultSettings() {
        new WorkerThreads(4, 4, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        Log.d("!!!:","workerThreads are prepared.");
    }

}
