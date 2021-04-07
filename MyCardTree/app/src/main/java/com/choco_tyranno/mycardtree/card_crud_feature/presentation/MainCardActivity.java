package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.ContainerAdapter;
import com.choco_tyranno.mycardtree.card_crud_feature.utils.WorkerThreads;
import com.choco_tyranno.mycardtree.databinding.ActivityMainFrameBinding;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MainCardActivity extends AppCompatActivity {
    final String DEBUG_TAG = "!!!!:";
    ContainerAdapter layerAdapter;
    LinearLayoutManager layerLM;
    CardTreeViewModel viewModel;
    ActivityMainFrameBinding binding;

    public void assignWork(Runnable work) {
        WorkerThreads.instance.execute(work);
    }

    private void observeData() {
        viewModel.getData().observe(MainCardActivity.this, containerWithCards -> {
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(DEBUG_TAG, "Activity onCreate start");
        WorkerThreads workers = new WorkerThreads(4, 4, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        viewModel = new ViewModelProvider(MainCardActivity.this).get(CardTreeViewModel.class);
//        viewModel.createRepo();
        viewModel.prepareData(this::observeData);

        binding = ActivityMainFrameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.setLifecycleOwner(this);
        binding.mainScreen.createCardFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        loadContainerRecyclerView();

    }

    private void loadContainerRecyclerView() {
        RecyclerView rv = binding.mainScreen.mainBody.parentRecyclerview;
        rv.setAdapter(new ContainerAdapter(this));
        rv.setLayoutManager(new LinearLayoutManager(MainCardActivity.this, LinearLayoutManager.VERTICAL, false));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        android.os.Process.killProcess(android.os.Process.myPid());
    }

}