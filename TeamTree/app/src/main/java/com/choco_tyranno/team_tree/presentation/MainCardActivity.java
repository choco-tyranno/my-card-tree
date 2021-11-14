package com.choco_tyranno.team_tree.presentation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.choco_tyranno.team_tree.Logger;
import com.choco_tyranno.team_tree.R;
import com.choco_tyranno.team_tree.databinding.ActivityMainBinding;
import com.choco_tyranno.team_tree.domain.card_data.CardDto;
import com.choco_tyranno.team_tree.presentation.card_rv.CardGestureListener;
import com.choco_tyranno.team_tree.presentation.card_rv.CardViewShadowProvider;
import com.choco_tyranno.team_tree.presentation.card_rv.SpreadingOutDetailOnClickListener;
import com.choco_tyranno.team_tree.presentation.container_rv.CardContainerViewHolder;
import com.choco_tyranno.team_tree.presentation.container_rv.ContainerAdapter;
import com.choco_tyranno.team_tree.presentation.container_rv.ContainerRecyclerView;
import com.choco_tyranno.team_tree.presentation.searching_drawer.CardFinder;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;

public class MainCardActivity extends AppCompatActivity {
    private CardViewModel viewModel;
    private ActivityMainBinding binding;
    private Handler mMainHandler;
    private CardFinder cardFinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("@@H", "onCreate");
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        if (!Optional.ofNullable(mMainHandler).isPresent())
            mMainHandler = new Handler(getMainLooper());
        viewModel = new ViewModelProvider(MainCardActivity.this).get(CardViewModel.class);
        loadDefaultCardImage();
        mainBinding();
        setSupportActionBar(binding.layoutMainbody.toolbarMainBodyTopAppBar);
        Objects.requireNonNull(getSupportActionBar()).hide();
        binding.setViewModel(viewModel);
        setContainerRv();
        setSearchingResultRv();
        cardFinder = new CardFinder(this);

        scaleMainRemoveSwitch();

        setSearchViewAttributes();

        ImageView searchBtn = binding.layoutSearchdrawer.cardSearchView.findViewById(androidx.appcompat.R.id.search_button);
        ImageView searchCloseBtn = binding.layoutSearchdrawer.cardSearchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        SearchView.SearchAutoComplete searchAutoComplete = binding.layoutSearchdrawer.cardSearchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchBtn.setColorFilter(R.color.colorPrimary_a);
        searchCloseBtn.setColorFilter(R.color.colorPrimary_a);
        searchAutoComplete.setTextColor(getResources().getColor(R.color.colorPrimary, getTheme()));

        CardGestureListener cardGestureListener = new CardGestureListener();
        GestureDetectorCompat cardGestureDetector = new GestureDetectorCompat(MainCardActivity.this, cardGestureListener);
        viewModel.setCardGestureListener(cardGestureListener);
        viewModel.setCardGestureDetector(cardGestureDetector);
        viewModel.connectGestureUtilsToOnCardTouchListener();

        viewModel.loadData(() -> {
            waitDefaultCardImageLoading(getMainHandler());
            loadPictureCardImages(viewModel.getPictureCardArr(), getMainHandler());
        });
    }

    private void setSearchViewAttributes() {
        SearchView searchView = binding.layoutSearchdrawer.cardSearchView;
        searchView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                searchView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int searchViewHeight = searchView.getHeight();
                final ConstraintSet constraintSet = new ConstraintSet();
                ConstraintLayout parent = (ConstraintLayout)searchView.getParent();
                constraintSet.clone(parent);
                constraintSet.constrainMinHeight(searchView.getId(), searchViewHeight);
                constraintSet.applyTo(parent);
            }
        });


    }


    private void scaleMainRemoveSwitch() {
        View topAppBar = binding.layoutMainbody.viewMainBodyTopAppBarBackground;
        SwitchMaterial removeSwitch = binding.layoutMainbody.switchMaterialMainBodyRemoveSwitch;
        removeSwitch.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                removeSwitch.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                float switchRatioToTopAppBar = Float.parseFloat(binding.getRoot().getContext().getResources().getString(R.string.mainBody_removeSwitchRatioToTopAppBar));
                int switchHeightPx = removeSwitch.getHeight();
                int topAppBarHeightPx = topAppBar.getHeight();
                if (topAppBarHeightPx==0){
                    topAppBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            float multiplingValue = switchRatioToTopAppBar * topAppBarHeightPx / switchHeightPx;
                            removeSwitch.setScaleX(multiplingValue);
                            removeSwitch.setScaleY(multiplingValue);
                        }
                    });
                    return;
                }
                float multiplingValue = switchRatioToTopAppBar * topAppBarHeightPx / switchHeightPx;
                removeSwitch.setScaleX(multiplingValue);
                removeSwitch.setScaleY(multiplingValue);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Objects.requireNonNull(getSupportActionBar()).hide();
        viewModel.toggleSettingsOn();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout mainDL = binding.drawerLayoutMainSearchDrawer;
        if (mainDL.isDrawerOpen(GravityCompat.END)) {
            mainDL.closeDrawer(GravityCompat.END);
            SearchView searchView = binding.layoutSearchdrawer.cardSearchView;
            searchView.setQuery("", false);
            searchView.setIconified(true);
            cardFinder.setSendingFindCardReq(false);
            return;
        }
        Optional.of(viewModel.isSettingsOn()).flatMap(settingsOn ->
                Optional.ofNullable(settingsOn.getValue())).ifPresent(value -> {
            if (value) {
                Objects.requireNonNull(getSupportActionBar()).hide();
                viewModel.toggleSettingsOn();
            } else {
                super.onBackPressed();
            }
        });
    }

    public CardFinder getCardFinder() {
        return this.cardFinder;
    }

    private void setSearchingResultRv() {
        binding.layoutSearchdrawer.cardSearchResultRecyclerview
                .addItemDecoration(new DividerItemDecoration(MainCardActivity.this, DividerItemDecoration.VERTICAL));
    }


    public void loadPictureCardImages(CardDto[] allCardArr, Handler handler) {
        for (CardDto theCardDto : allCardArr) {
            if (TextUtils.equals(theCardDto.getImagePath(), ""))
                continue;
            handler.postDelayed(() -> {
                final int cardNo = theCardDto.getCardNo();
                try {
                    int width = Math.round(getResources().getDimension(R.dimen.card_thumbnail_image_width));
                    int height = Math.round(getResources().getDimension(R.dimen.card_thumbnail_image_height));
                    Glide.with(MainCardActivity.this).asBitmap()
                            .load(theCardDto.getImagePath()).addListener(new RequestListener<>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            viewModel.setPictureCardImage(resource, cardNo);
                            return false;
                        }
                    }).submit(width, height);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 1000);
        }
    }

    public void waitDefaultCardImageLoading(Handler handler) {
        handler.postDelayed(() -> {
            if (viewModel.hasDefaultCardImage()) {
                showContainerCardUi();
            } else {
                waitDefaultCardImageLoading(handler);
            }
        }, 1000);
    }

    public void showContainerCardUi() {
        runOnUiThread(() -> Objects.requireNonNull(binding.layoutMainbody.containerRecyclerViewMainBodyContainers.getAdapter())
                .notifyDataSetChanged());
    }

    private void loadDefaultCardImage() {
        new Thread(() -> {
            try {
                int width = Math.round(getResources().getDimension(R.dimen.card_thumbnail_image_width));
                int height = Math.round(getResources().getDimension(R.dimen.card_thumbnail_image_height));
                Glide.with(MainCardActivity.this).asBitmap()
                        .load(R.drawable.default_card_image_01).addListener(new RequestListener<>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        viewModel.setDefaultCardImage(resource);
                        return false;
                    }
                }).submit(width, height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void mainBinding() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.setLifecycleOwner(this);
    }

    private void setContainerRv() {
        ContainerRecyclerView rv = binding.layoutMainbody.containerRecyclerViewMainBodyContainers;
        rv.setAdapter(new ContainerAdapter(this));
        rv.setLayoutManager(new ContainerRecyclerView.ItemScrollingControlLayoutManager(MainCardActivity.this, LinearLayoutManager.VERTICAL, false));
        Objects.requireNonNull(rv.getAdapter()).notifyDataSetChanged();
    }

    public ActivityMainBinding getMainBinding() {
        return binding;
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SpreadingOutDetailOnClickListener.REQ_MANAGE_DETAIL) {
            if (data == null)
                return;
            CardDto updatedCardDto = (CardDto) data.getSerializableExtra("post_card");
            boolean imageChanged = viewModel.applyCardFromDetailActivity(updatedCardDto);
            if (imageChanged) {
                CardDto[] cardDTOArr = {updatedCardDto};
                loadPictureCardImages(cardDTOArr, mMainHandler);
            }
        }
    }

    public void scrollToFindingTargetCard(Pair<Integer, Integer[]> scrollUtilDataForFindingOutCard, Runnable finishAction) {
        final int startContainerPosition = scrollUtilDataForFindingOutCard.first;
        final Integer[] scrollTargetCardSeqArr = scrollUtilDataForFindingOutCard.second;
        RecyclerView containerRecyclerview = binding.layoutMainbody.containerRecyclerViewMainBodyContainers;
        Queue<Runnable> scrollActionQueue = new LinkedList<>();
        int s = 0;
        for (int i = startContainerPosition; i < startContainerPosition + scrollTargetCardSeqArr.length; i++) {
            final int s1 = s;
            final int i1 = i;
            scrollActionQueue.offer(() -> {
                containerRecyclerview.smoothScrollToPosition(i1);
                Runnable delayedAction = () -> {
//<Exception>                    ClassCastException : below line.
                    CardContainerViewHolder containerViewHolder = (CardContainerViewHolder) containerRecyclerview.findViewHolderForAdapterPosition(i1);
                    if (containerViewHolder == null)
                        throw new RuntimeException("MainCardActivity#scrollToFindingTargetCard - containerViewHolder == null");
                    RecyclerView cardRecyclerview = containerViewHolder.getBinding().cardRecyclerViewCardContainerCards;
                    cardRecyclerview.smoothScrollToPosition(scrollTargetCardSeqArr[s1]);
                };
                mMainHandler.postDelayed(delayedAction, 900);
            });
            s++;
        }
        if (scrollActionQueue.isEmpty()) {
            mMainHandler.postDelayed(() ->
                    containerRecyclerview.smoothScrollToPosition(startContainerPosition), 900);
            finishAction.run();
            return;
        }
        scrollActionDelayed(scrollActionQueue, finishAction);
    }

    public void scrollActionDelayed(Queue<Runnable> scrollActionQueue, Runnable finishAction) {
        mMainHandler.postDelayed(() -> {
            if (scrollActionQueue.isEmpty()) {
                if (finishAction != null)
                    finishAction.run();
                return;
            }
            Optional.ofNullable(scrollActionQueue.poll()).ifPresent(Runnable::run);
            scrollActionDelayed(scrollActionQueue, finishAction);
        }, 900);
    }
}