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

import java.util.Objects;
import java.util.Optional;

public class MainCardActivity extends AppCompatActivity {
    private static CardViewModel viewModel;
    private ActivityMainFrameBinding binding;
    private Handler mMainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Optional.ofNullable(mMainHandler).isPresent())
        mMainHandler = new Handler(getMainLooper());
//        isStart = true;
        viewModel = new ViewModelProvider(MainCardActivity.this).get(CardViewModel.class);
        mainBinding();
        binding.setViewModel(viewModel);
        setContainerRv();
        viewModel.loadData(()-> runOnUiThread(()->Objects.requireNonNull(binding.mainScreen.mainBody.containerRecyclerview.getAdapter()).notifyDataSetChanged()));
//        observeCardData();
        binding.mainScreen.appNameFab.setOnClickListener(new View.OnClickListener() {
//            int pos = 2;
            @Override
            public void onClick(View v) {

//                ((CardContainerViewHolder) Objects.requireNonNull(binding.mainScreen.mainBody.containerRecyclerview.findViewHolderForAdapterPosition(0)))
//                        .getBinding().cardRecyclerview.smoothScrollToPosition(pos);
//                if (pos == 2)
//                    pos = 0;
//                else
//                    pos = 2;
            }
        });
    }

    public static CardViewModel getViewModel(){
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
        Objects.requireNonNull(rv.getAdapter()).notifyDataSetChanged();
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
    protected void onStop() {
        super.onStop();
        Logger.message("onStop");
        SingleToastManager.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.message("destroyed");
        SingleToastManager.clear();
    }

    public CardViewModel getCardViewModel() {
        return viewModel;
    }

    public Handler getMainHandler(){
        return mMainHandler;
    }

}