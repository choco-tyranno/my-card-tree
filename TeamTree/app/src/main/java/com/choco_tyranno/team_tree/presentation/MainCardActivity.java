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
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.print.PrintAttributes;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.choco_tyranno.team_tree.presentation.main.NewCardButton;
import com.choco_tyranno.team_tree.presentation.main.TopAppBar;
import com.choco_tyranno.team_tree.presentation.searching_drawer.CardFinder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class MainCardActivity extends AppCompatActivity {
    private CardViewModel viewModel;
    private ActivityMainBinding binding;
    private Handler mMainHandler;
    private CardFinder cardFinder;
    private final int REQ_UPDATE = 1992;
    private final String TAG = "@@choco_tyranno";
    AppUpdateManager appUpdateManager;
    InstallStateUpdatedListener installStateUpdatedListenerForFlexibleUpdate;

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
            return;
        }
        if (requestCode == REQ_UPDATE && resultCode != RESULT_OK) {
            SingleToaster.makeTextShort(this,"Team tree 업데이트 실패. 앱을 종료 후 다시 시도해 주세요.").show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(appUpdateInfo -> {
                    final int installType;
                    if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE))
                        installType = AppUpdateType.FLEXIBLE;
                    else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE))
                        installType = AppUpdateType.IMMEDIATE;
                    else
                        installType = -1;
                    Log.d(TAG,"onResume#installType: "+installType);
                    Log.d(TAG,"onResume#appUpdateInfo.updateAvailability(): "+appUpdateInfo.updateAvailability());
                    Log.d(TAG,"onResume#appUpdateInfo.appUpdateInfo.installStatus(): "+appUpdateInfo.installStatus());
                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        popupSnackBarForCompleteFlexibleUpdate();
                        return;
                    }
                    if(installType == AppUpdateType.IMMEDIATE &&
                            appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){
                        try {
                            appUpdateManager.startUpdateFlowForResult(
                                    appUpdateInfo,
                                    AppUpdateType.IMMEDIATE,
                                    this,
                                    REQ_UPDATE);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void checkUpdates() {
        if (appUpdateManager == null)
            appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            Log.d(TAG,"checkUpdates#appUpdateInfo.updateAvailability() : "+appUpdateInfo.updateAvailability());
            Log.d(TAG,"checkUpdates#appUpdateInfo.installStatus() : "+appUpdateInfo.installStatus());
            if (appUpdateInfo.updateAvailability() != UpdateAvailability.UPDATE_AVAILABLE &&
                    appUpdateInfo.updateAvailability() != UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                return;
            }
            final int installType;
            if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE))
                installType = AppUpdateType.FLEXIBLE;
            else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE))
                installType = AppUpdateType.IMMEDIATE;
            else
                installType = -1;
            Log.d(TAG,"checkUpdates()/installType:"+installType);
            if (installType == -1)
                return;
            if (installType == AppUpdateType.FLEXIBLE) {
                initInstallStateUpdatedListenerForFlexibleUpdate();
                appUpdateManager.registerListener(installStateUpdatedListenerForFlexibleUpdate);
            }
            try {
                appUpdateManager.startUpdateFlowForResult(appUpdateInfo,
                        installType,
                        MainCardActivity.this,
                        REQ_UPDATE);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        });
    }

    private void initInstallStateUpdatedListenerForFlexibleUpdate() {
        if (installStateUpdatedListenerForFlexibleUpdate != null)
            return;
        installStateUpdatedListenerForFlexibleUpdate = installState -> {
            if (installState.installStatus() == InstallStatus.DOWNLOADED)
                popupSnackBarForCompleteFlexibleUpdate();
            if (installState.installStatus() == InstallStatus.INSTALLED)
                appUpdateManager.unregisterListener(installStateUpdatedListenerForFlexibleUpdate);
        };
    }

    private void popupSnackBarForCompleteFlexibleUpdate() {
        Snackbar.make(binding.getRoot(), "\uD83C\uDF20새로운 버전 다운로드 완료!", Snackbar.LENGTH_INDEFINITE)
                .setAction("설치", view -> {
                    if (appUpdateManager != null)
                        appUpdateManager.completeUpdate();
                }).setActionTextColor(getResources().getColor(R.color.colorAccent_b, getTheme()))
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        if (!Optional.ofNullable(mMainHandler).isPresent())
            mMainHandler = new Handler(getMainLooper());
        checkUpdates();
        viewModel = new ViewModelProvider(MainCardActivity.this).get(CardViewModel.class);
        loadDefaultCardImage();
        mainBinding();
        setSupportActionBar(binding.layoutMainbody.toolbarMainBodyDefaultAppBar);
        Objects.requireNonNull(getSupportActionBar()).hide();
        binding.setViewModel(viewModel);
        setContainerRv();
        setSearchingResultRv();
        cardFinder = new CardFinder(this);
        initView();

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

    private void initView() {
        TopAppBar topAppBar = binding.layoutMainbody.viewMainBodyTopAppBar;
        new DependentUIResolver.DependentUIResolverBuilder<View>()
                .baseView(topAppBar)
                .with(topAppBar.getId()
                        , binding.layoutMainbody.removeSwitchMainBodyRemoveSwitch::setScaleByTopAppBar
                        , binding.layoutSearchdrawer.cardSearchView::setConstrainFixedHeightByTopAppBar
                ).build()
                .resolve();

        NewCardButton newCardButton = binding.layoutMainbody.buttonMainBodyNewCard;
        new DependentUIResolver.DependentUIResolverBuilder<View>()
                .baseView(newCardButton)
                .with(newCardButton.getId()
                        , binding.layoutMainbody.bottomBarMainBodyBottomBar::setHeightByNewCardButton
                ).build()
                .resolve();

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
        SingleToastManager.clear();
        appUpdateManager.unregisterListener(installStateUpdatedListenerForFlexibleUpdate);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CardViewShadowProvider.onDestroy();
        SingleToastManager.clear();
    }

    public CardViewModel getCardViewModel() {
        return viewModel;
    }

    public Handler getMainHandler() {
        return mMainHandler;
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