package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.ContainerAdapter;
import com.choco_tyranno.mycardtree.databinding.ActivityMainFrameBinding;

public class MainCardActivity extends AppCompatActivity {
    ContainerAdapter layerAdapter;
    LinearLayoutManager layerLM;
    CardTreeViewModel viewModel;
    ActivityMainFrameBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(MainCardActivity.this).get(CardTreeViewModel.class);
        viewModel.setMainHandler(new Handler(getMainLooper()));
        viewModel.loadData();
        viewModel.getData().observe(this,(cards)->{
            Logger.nullCheck(cards,"activity#onCreate/observe cards");
        });

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