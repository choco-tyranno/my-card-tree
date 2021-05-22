package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.source.MyCardTreeDataBase;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.ContainerAdapter;
import com.choco_tyranno.mycardtree.databinding.ActivityMainBodyBinding;
import com.choco_tyranno.mycardtree.databinding.ActivityMainFrameBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class MainCardActivity extends AppCompatActivity {
    private static CardTreeViewModel viewModel;
    private ActivityMainFrameBinding binding;
    boolean isStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        isStart = true;
        viewModel = new ViewModelProvider(MainCardActivity.this).get(CardTreeViewModel.class);
        mainBinding();
        binding.mainScreen.setViewModel(viewModel);
        setContainerRv();
        viewModel.loadData(()->binding.mainScreen.mainBody.containerRecyclerview.getAdapter().notifyDataSetChanged());
//        observeCardData();
    }

    public static CardTreeViewModel getViewModel(){
        if (Optional.ofNullable(viewModel).isPresent())
        return viewModel;
        throw new RuntimeException("MainCardActivity#getViewModel is null");
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
        rv.getAdapter().notifyDataSetChanged();
    }

    // TODO : this is for auto notify using DiffUtil
//    private void observeCardData() {
//        viewModel.loadData(() -> Optional.ofNullable(viewModel.getAllLiveData()).ifPresent((liveData -> runOnUiThread(() ->
//                        liveData.observe(this, (cards) -> {
//                            Toast.makeText(this, "AllLiveData/Observer#onChanged", Toast.LENGTH_SHORT).show();
//                            Logger.message("allLiveData - The Data changed");
//                            boolean hasAdapter = Optional.ofNullable((ContainerAdapter) binding.mainScreen.mainBody.containerRecyclerview.getAdapter()).isPresent();
//                            if (hasAdapter) {
////                                binding.mainScreen.mainBody.containerRecyclerview.getAdapter().notifyDataSetChanged();
//                            } else
//                                throw new RuntimeException("MainActivity#observeCardData/binding.mainScreen.mainBody.containerRecyclerview has no adapter.");
//                        })
//                ))
//        ));
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public CardTreeViewModel shareViewModel() {
        return viewModel;
    }

}