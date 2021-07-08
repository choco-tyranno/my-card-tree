package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.choco_tyranno.mycardtree.R;
import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardGestureListener;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardState;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardViewShadowProvider;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.ContainerAdapter;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.ContainerRecyclerView;
import com.choco_tyranno.mycardtree.databinding.ActivityMainFrameBinding;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MainCardActivity extends AppCompatActivity {
    private CardViewModel viewModel;
    private ActivityMainFrameBinding binding;
    private Handler mMainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Optional.ofNullable(mMainHandler).isPresent())
        mMainHandler = new Handler(getMainLooper());
        viewModel = new ViewModelProvider(MainCardActivity.this).get(CardViewModel.class);
        mainBinding();
        binding.setViewModel(viewModel);
        setContainerRv();
        CardGestureListener cardGestureListener = new CardGestureListener();
        GestureDetectorCompat cardGestureDetector = new GestureDetectorCompat(MainCardActivity.this, cardGestureListener);
        viewModel.setCardGestureListener(cardGestureListener);
        viewModel.setCardGestureDetector(cardGestureDetector);
        viewModel.connectGestureUtilsToOnCardTouchListener();
        viewModel.loadData(()-> runOnUiThread(()->Objects.requireNonNull(binding.mainScreen.mainBody.containerRecyclerview.getAdapter()).notifyDataSetChanged()));
//        observeCardData();
        binding.mainScreen.appNameFab.setOnClickListener((view)->{
//            viewModel.printContainers();
//            viewModel.printAllData();
        });
    }


    private void mainBinding() {
        binding = ActivityMainFrameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.setLifecycleOwner(this);
    }

    private void setContainerRv() {
        ContainerRecyclerView rv = binding.mainScreen.mainBody.containerRecyclerview;
        rv.setAdapter(new ContainerAdapter(this));
        rv.setLayoutManager(new ContainerRecyclerView.ItemScrollingControlLayoutManager(MainCardActivity.this, LinearLayoutManager.VERTICAL, false));
        Objects.requireNonNull(rv.getAdapter()).notifyDataSetChanged();
    }

    public ActivityMainFrameBinding getMainBinding(){
        return binding;
    }

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
        CardViewShadowProvider.onDestroy();
        SingleToastManager.clear();
    }

    public CardViewModel getCardViewModel() {
        return viewModel;
    }

    public Handler getMainHandler(){
        return mMainHandler;
    }

}