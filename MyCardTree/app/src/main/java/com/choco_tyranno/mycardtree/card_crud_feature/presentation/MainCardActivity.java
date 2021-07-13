package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.choco_tyranno.mycardtree.R;
import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardGestureListener;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardViewShadowProvider;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.ContactCardViewHolder;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.CardContainerViewHolder;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.ContainerAdapter;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.ContainerRecyclerView;
import com.choco_tyranno.mycardtree.databinding.ActivityMainFrameBinding;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

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
        Bitmap defaultCardImage = loadDefaultCardImage();
        CardGestureListener cardGestureListener = new CardGestureListener();
        GestureDetectorCompat cardGestureDetector = new GestureDetectorCompat(MainCardActivity.this, cardGestureListener);
        viewModel.setCardGestureListener(cardGestureListener);
        viewModel.setCardGestureDetector(cardGestureDetector);
        viewModel.connectGestureUtilsToOnCardTouchListener();
        viewModel.setDefaultCardImage(defaultCardImage);
        Logger.hotfixMessage("is defaultCardImage null? : "+defaultCardImage==null?"null":"nonNull");
        viewModel.loadData(() -> runOnUiThread(() -> Objects.requireNonNull(binding.mainScreen.mainBody.containerRecyclerview.getAdapter()).notifyDataSetChanged()));
//        observeCardData();
        binding.mainScreen.appNameFab.setOnClickListener((view) -> {
//            ContainerRecyclerView containerRecyclerView = binding.mainScreen.mainBody.containerRecyclerview;
//            CardContainerViewHolder containerViewHolder = (CardContainerViewHolder)containerRecyclerView.findViewHolderForAdapterPosition(1);
//            ContactCardViewHolder cardViewHolder = (ContactCardViewHolder)containerViewHolder.getBinding().cardRecyclerview.findViewHolderForAdapterPosition(0);
//            ConstraintLayout backLayout = cardViewHolder.getBinding().cardBackLayout.backCardConstraintLayout;
//            ConstraintLayout frontLayout = cardViewHolder.getBinding().cardFrontLayout.frontCardConstraintLayout;
//            Logger.hotfixMessage("backLayout: visibility"+backLayout.getVisibility());
//            Logger.hotfixMessage("frontLayout: visibility"+frontLayout.getVisibility());
//            Logger.hotfixMessage("frontCardCardView: visibility"+cardViewHolder.getBinding().cardFrontLayout.frontCardCardView.getVisibility());
//            viewModel.printTargetCard(1, 0);
//            viewModel.printContainers();
//            viewModel.printAllData();
        });
    }

    private Bitmap loadDefaultCardImage() {
        try {
            int width = Math.round(getResources().getDimension(R.dimen.card_thumbnail_image_width));
            int height = Math.round(getResources().getDimension(R.dimen.card_thumbnail_image_height));
            return Glide.with(MainCardActivity.this).asBitmap()
                    .load(R.drawable.default_card_image_01).submit(width, height).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    public ActivityMainFrameBinding getMainBinding() {
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

    public Handler getMainHandler() {
        return mMainHandler;
    }

}