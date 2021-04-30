package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.ContainerAdapter;
import com.choco_tyranno.mycardtree.databinding.ActivityMainBodyBinding;
import com.choco_tyranno.mycardtree.databinding.ActivityMainFrameBinding;

import java.util.Optional;

public class MainCardActivity extends AppCompatActivity {
    private CardTreeViewModel viewModel;
    private LinearLayoutManager layerLM;
    private ActivityMainFrameBinding binding;
    boolean isStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isStart = true;
        mainBinding();
        setContainerRv();
        viewModel = new ViewModelProvider(MainCardActivity.this).get(CardTreeViewModel.class);
        observeCardData();
    }

    private void presentInitCardContainerViews() {
        Optional.ofNullable((ContainerAdapter) binding.mainScreen.mainBody.containerRecyclerview.getAdapter())
                .ifPresent(ContainerAdapter::presentInitContainerViews);
    }

    private void mainBinding() {
        binding = ActivityMainFrameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.setLifecycleOwner(this);
    }

    private void setContainerRv() {
        RecyclerView rv = binding.mainScreen.mainBody.containerRecyclerview;
        rv.setAdapter(new ContainerAdapter(this));
        rv.setLayoutManager(new LinearLayoutManager(MainCardActivity.this, LinearLayoutManager.VERTICAL, false));
    }

    private void observeCardData() {
        viewModel.loadData(() -> Optional.ofNullable(viewModel.getData()).ifPresent((liveData -> runOnUiThread(() -> {
            liveData.observe(this, (cards) -> {
                boolean hasAdapter = Optional.ofNullable((ContainerAdapter) binding.mainScreen.mainBody.containerRecyclerview.getAdapter()).isPresent();
                if (hasAdapter){
                    ContainerAdapter containerAdapter = (ContainerAdapter)binding.mainScreen.mainBody.containerRecyclerview.getAdapter();
                    containerAdapter.submitList(cards);
                }
            });
            if (isStart) {
                presentInitCardContainerViews();
                isStart = false;
            }
        }))
        ));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public CardTreeViewModel shareViewModel(){
        return viewModel;
    }

}