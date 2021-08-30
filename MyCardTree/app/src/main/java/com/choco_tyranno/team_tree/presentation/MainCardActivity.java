package com.choco_tyranno.team_tree.presentation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.choco_tyranno.team_tree.Logger;
import com.choco_tyranno.team_tree.R;
import com.choco_tyranno.team_tree.databinding.ActivityMainFrameBinding;
import com.choco_tyranno.team_tree.domain.card_data.CardDto;
import com.choco_tyranno.team_tree.presentation.card_rv.CardGestureListener;
import com.choco_tyranno.team_tree.presentation.card_rv.CardViewShadowProvider;
import com.choco_tyranno.team_tree.presentation.card_rv.ImageToFullScreenClickListener;
import com.choco_tyranno.team_tree.presentation.container_rv.CardContainerViewHolder;
import com.choco_tyranno.team_tree.presentation.container_rv.ContainerAdapter;
import com.choco_tyranno.team_tree.presentation.container_rv.ContainerRecyclerView;
import com.choco_tyranno.team_tree.presentation.container_rv.ContainerScrollListener;
import com.choco_tyranno.team_tree.presentation.searching_drawer.CardFinder;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;

public class MainCardActivity extends AppCompatActivity {
    private CardViewModel viewModel;
    private ActivityMainFrameBinding binding;
    private Handler mMainHandler;
    private CardFinder cardFinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        if (!Optional.ofNullable(mMainHandler).isPresent())
            mMainHandler = new Handler(getMainLooper());
        viewModel = new ViewModelProvider(MainCardActivity.this).get(CardViewModel.class);
        loadDefaultCardImage();
        mainBinding();
        binding.setViewModel(viewModel);
        setContainerRv();
        setSearchingResultRv();

        cardFinder = new CardFinder(this);

        ImageView searchBtn = binding.rightDrawer.cardSearchView.findViewById(androidx.appcompat.R.id.search_button);
        ImageView searchCloseBtn = binding.rightDrawer.cardSearchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        SearchView.SearchAutoComplete searchAutoComplete = binding.rightDrawer.cardSearchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchBtn.setColorFilter(R.color.colorPrimary_a);
        searchCloseBtn.setColorFilter(R.color.colorPrimary_a);
        searchAutoComplete.setTextColor(getResources().getColor(R.color.colorPrimary, getTheme()));

        // worker thread's job available.
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

    @Override
    public void onBackPressed() {
        DrawerLayout mainDL = binding.mainDrawerLayout;
        if (mainDL.isDrawerOpen(GravityCompat.END)) {
            mainDL.closeDrawer(GravityCompat.END);
            SearchView searchView = binding.rightDrawer.cardSearchView;
            searchView.setQuery("", false);
            searchView.setIconified(true);
            cardFinder.setSendingFindCardReq(false);
            return;
        }
        super.onBackPressed();
    }

    public CardFinder getCardFinder() {
        return this.cardFinder;
    }

    private void setSearchingResultRv() {
        binding.rightDrawer.cardSearchResultRecyclerview
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
                            .load(theCardDto.getImagePath()).addListener(new RequestListener<Bitmap>() {
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
        runOnUiThread(() -> Objects.requireNonNull(binding.mainScreen.mainBody.containerRecyclerview.getAdapter())
                .notifyDataSetChanged());
    }

    private void loadDefaultCardImage() {
        new Thread(() -> {
            try {
                int width = Math.round(getResources().getDimension(R.dimen.card_thumbnail_image_width));
                int height = Math.round(getResources().getDimension(R.dimen.card_thumbnail_image_height));
                Glide.with(MainCardActivity.this).asBitmap()
                        .load(R.drawable.default_card_image_01).addListener(new RequestListener<Bitmap>() {
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
        if (requestCode == ImageToFullScreenClickListener.REQ_MANAGE_DETAIL) {
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

//    TODO : this
    public void scrollToFindingTargetCard(Pair<Integer, Integer[]> scrollUtilDataForFindingOutCard, Runnable finishAction) {
        final int startContainerPosition = scrollUtilDataForFindingOutCard.first;
        final Integer[] scrollTargetCardSeqArr = scrollUtilDataForFindingOutCard.second;
        RecyclerView containerRecyclerview = binding.mainScreen.mainBody.containerRecyclerview;
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
                    RecyclerView cardRecyclerview = containerViewHolder.getBinding().cardRecyclerview;
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