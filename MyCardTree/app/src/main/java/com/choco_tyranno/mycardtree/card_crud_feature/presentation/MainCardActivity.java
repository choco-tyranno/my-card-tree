package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.ContainerAdapter;
import com.choco_tyranno.mycardtree.databinding.ActivityMainFrameBinding;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MainCardActivity extends AppCompatActivity {
    final String DEBUG_TAG = "!!!!:";
    ContainerAdapter layerAdapter;
    LinearLayoutManager layerLM;
    CardTreeViewModel viewModel;
    ActivityMainFrameBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(MainCardActivity.this).get(CardTreeViewModel.class);
        viewModel.prepareData(this::observeData);
        Log.d(DEBUG_TAG,"vm prepared");
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

    private void observeData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewModel.getData().observe(MainCardActivity.this, cardDTOS -> {

                });
            }
        });
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