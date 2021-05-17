package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import android.app.Activity;
import android.app.Application;
import android.content.ClipData;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardEntity;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardState;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.source.CardRepository;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.source.OnDataLoadListener;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardAdapter;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.ContactCardViewHolder;
import com.choco_tyranno.mycardtree.databinding.ItemCardFrameBinding;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CardTreeViewModel extends AndroidViewModel {
    private final CardRepository mCardRepository;
    private final MutableLiveData<List<List<CardDTO>>> mListLiveDataByContainer;
    private final List<List<CardDTO>> allData;

    private final List<Integer> mPresentFlags;
    private final List<List<Pair<CardDTO, CardState>>> mPresentData;

    private final View.OnLongClickListener onLongListenerForCreateCardUtilFab;
    private View.OnDragListener onDragListenerForCardRecyclerView;

    private final int CARD_LOCATION_LEFT = 0;
    private final int CARD_LOCATION_RIGHT = 1;

    public CardTreeViewModel(Application application) {
        super(application);
        mCardRepository = new CardRepository(application);
        mListLiveDataByContainer = new MutableLiveData<>();
        mPresentFlags = new ArrayList<>();
        allData = new ArrayList<>();
        mPresentData = new ArrayList<>();
        onLongListenerForCreateCardUtilFab = (view) -> view.startDragAndDrop(ClipData.newPlainText("", ""), new CardShadow(view), null, 0);
        initCardRecyclerViewDragListener();
    }

    public void onModeChanged(View view, boolean isOn){
        int newVisibility = View.INVISIBLE;
        if (isOn)
            newVisibility = View.VISIBLE;
        for (List<Pair<CardDTO, CardState>> containerItems : mPresentData){
            for (Pair<CardDTO, CardState> item : containerItems){
                item.second.setRemoveBtnVisibility(newVisibility);
            }
        }
        Toast.makeText(view.getContext(), ""+isOn, Toast.LENGTH_SHORT).show();
    }

    // view : card recyclerView
    private void initCardRecyclerViewDragListener() {
        onDragListenerForCardRecyclerView = (view, event) -> {
            if (view instanceof RecyclerView) {
                if (event.getAction() == DragEvent.ACTION_DRAG_STARTED) {
                    return true;
                }
                RecyclerView targetView = (RecyclerView) view;
                LinearLayoutManager layoutManager = (LinearLayoutManager) targetView.getLayoutManager();
                NullPassUtil.checkLinearLayoutManager(layoutManager);
                if (!(layoutManager.getItemCount()>0)){
                    return false;
                }
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                boolean result;
                if (firstVisibleItemPosition == lastVisibleItemPosition) {
                    result = handleDragEventSingleItemVisibleCase(targetView, firstVisibleItemPosition, event);
                    return result;
                }
                result = handleDragEventMultiItemVisibleCase(targetView, firstVisibleItemPosition, lastVisibleItemPosition, event);
                return result;
            } else {
                throw new RuntimeException("#ondrag() : recyclerview not found");
            }
        };
    }

    private boolean handleDragEventSingleItemVisibleCase(RecyclerView rv,int targetCardPosition, DragEvent event) {
        FrameLayout targetView = Objects.requireNonNull((ContactCardViewHolder) rv.findViewHolderForAdapterPosition(targetCardPosition))
                .getBinding().cardContainerFrameLayout;
        NullPassUtil.checkFrameLayout(targetView);
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_ENTERED:
                slowOut(targetView,false,CARD_LOCATION_LEFT);
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                slowOut(targetView, true, CARD_LOCATION_LEFT);
                return true;
            case DragEvent.ACTION_DROP:
                dropAndCreateService(rv,targetView, null);
                return true;
        }
        return false;
    }

    //TODO: lastVisibleView not found
    private boolean handleDragEventMultiItemVisibleCase(RecyclerView rv,int firstVisibleCardPosition, int lastVisibleCardPosition, DragEvent event) {
        ContactCardViewHolder firstVisibleViewVH = (ContactCardViewHolder) rv.findViewHolderForAdapterPosition(firstVisibleCardPosition);
        FrameLayout firstVisibleView = Objects.requireNonNull(firstVisibleViewVH).getBinding().cardContainerFrameLayout;
        ContactCardViewHolder lastVisibleViewVH = (ContactCardViewHolder) rv.findViewHolderForAdapterPosition(lastVisibleCardPosition);
        FrameLayout lastVisibleView = Objects.requireNonNull(lastVisibleViewVH).getBinding().cardContainerFrameLayout;
        NullPassUtil.checkFrameLayout(firstVisibleView);
        NullPassUtil.checkFrameLayout(lastVisibleView);
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_ENTERED:
                slowOut(firstVisibleView,false,CARD_LOCATION_LEFT);
                slowOut(lastVisibleView,false, CARD_LOCATION_RIGHT);
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                slowOut(firstVisibleView,true,CARD_LOCATION_LEFT);
                slowOut(lastVisibleView,true, CARD_LOCATION_RIGHT);
                return true;
            case DragEvent.ACTION_DROP:
                dropAndCreateService(rv, firstVisibleView, lastVisibleView);
                return true;
        }
        return false;
    }

    private void dropAndCreateService(RecyclerView rv, FrameLayout targetView, @Nullable FrameLayout followingView){
        CardDTO targetCardDTO = ((ContactCardViewHolder) rv.getChildViewHolder(targetView)).getBinding().getCard();
        int targetSeqNo = targetCardDTO.getSeqNo();
        int targetBossNo = targetCardDTO.getBossNo();
        int targetContainerNo = targetCardDTO.getContainerNo();
        List<Pair<CardDTO, CardState>> targetContainerCardList = mPresentData.get(targetContainerNo);
        CardDTO newCardDTO = new CardDTO.Builder().seqNo(targetSeqNo + 1).bossNo(targetBossNo).containerNo(targetContainerNo).build();

        if (targetContainerCardList.size() > targetSeqNo + 1) {
            mCardRepository.insertAndUpdates(newCardDTO.toEntity()
                    , dtoListToEntityList(increaseListCardsSeq(targetContainerCardList, targetSeqNo + 1))
                    , orderDropDataInsertListener(targetCardDTO, targetContainerCardList, rv, targetView, followingView)
            );
        } else {
            mCardRepository.insert(newCardDTO.toEntity()
                    ,orderDropDataInsertListener(targetCardDTO, targetContainerCardList, rv, targetView, followingView)
            );
        }
    }

    //{@param direction} :
    // viewLocation [left 0, right 1]
    // X coordinate [0, -screenWidth, screenWidth]
    private void slowOut(View view, boolean isReverse, int viewLocation) {
        int screenWidth = ((Activity) view.getContext()).getWindowManager().getCurrentWindowMetrics().getBounds().right;
        int fromXCoordinate = -1;
        int toXCoordinate = -1;

        if (viewLocation== CARD_LOCATION_LEFT){
            if (!isReverse){
                fromXCoordinate = 0;
                toXCoordinate = -screenWidth;
            }else {
                fromXCoordinate = -screenWidth;
                toXCoordinate = 0;
            }
        }

        if (viewLocation== CARD_LOCATION_RIGHT){
            if (!isReverse){
                fromXCoordinate = 0;
                toXCoordinate = screenWidth;
            }else {
                fromXCoordinate = screenWidth;
                toXCoordinate = 0;
            }
        }

        if (fromXCoordinate == -1 || toXCoordinate == -1)
            throw new RuntimeException("slowOut/fromXCoordinate or toXCoordinate has no validated value");

        view.animate()
                .setInterpolator(AnimationUtils.loadInterpolator(view.getContext(), android.R.anim.accelerate_decelerate_interpolator))
                .setDuration(200)
                .translationXBy(fromXCoordinate)
                .translationX(toXCoordinate).start();
    }

    /* Data operation */

    private List<CardDTO> increaseListCardsSeq(List<Pair<CardDTO, CardState>> uiList, int increaseStart) {
        List<CardDTO> result = new ArrayList<>();
        for (int i = increaseStart; i < uiList.size(); i++) {
            Pair<CardDTO, CardState> pair = uiList.get(i);
            pair.first.setSeqNo(pair.first.getSeqNo() + 1);
            result.add(pair.first);
        }
        return result;
    }

    private List<CardEntity> dtoListToEntityList(List<CardDTO> uiList) {
        List<CardEntity> result = new ArrayList<>();
        for (CardDTO dto : uiList) {
            result.add(dto.toEntity());
        }
        return result;
    }

    public void loadData(OnDataLoadListener callback) {
        mCardRepository.readData(() -> {
            setData();
            callback.onLoadData();
        });
    }

    private void setData() {
        List<CardDTO> allDTOs = mCardRepository.getData().stream().map(CardEntity::toDTO).collect(Collectors.toList());
        List<List<CardDTO>> groupedData = groupDataByContainerNo(allDTOs);
        initContainerPresentFlags(groupedData);
        List<List<Pair<CardDTO, CardState>>> presentData = collectPresentData(groupedData);
        mPresentData.clear();
        mPresentData.addAll(presentData);
        // for Search func
        mListLiveDataByContainer.postValue(groupedData);
        // TODO : check [following 2 line] is it redundant?
        allData.clear();
        allData.addAll(groupedData);
    }

    private void initContainerPresentFlags(List<List<CardDTO>> data) {
        int nextGroupFlag = -1;

        for (int i = 0; i < data.size(); i++) {
            List<CardDTO> dtoList = data.get(i);
            boolean hasNext = false;
            int foundFlag = -1;

            if (data.get(i).isEmpty()) {
                return;
            }

            if (i == 0) {
                foundFlag = dtoList.get(0).getBossNo();
                nextGroupFlag = dtoList.get(0).getCardNo();
                mPresentFlags.add(i, foundFlag);
                continue;
            }

            for (CardDTO dto : dtoList) {
                if (dto.getBossNo() == nextGroupFlag) {
                    foundFlag = dto.getBossNo();
                    nextGroupFlag = dto.getCardNo();
                    hasNext = true;
                    break;
                }
            }

            mPresentFlags.add(i, foundFlag);

            if (!hasNext)
                break;
        }
    }

    private List<List<CardDTO>> groupDataByContainerNo(List<CardDTO> data) {
        List<List<CardDTO>> basket = new ArrayList<>();
        Optional.ofNullable(data).ifPresent(dtoList -> {
            for (CardDTO dto : dtoList) {
                int position = dto.getContainerNo();
                if (position > basket.size() - 1) {
                    basket.add(new ArrayList<>());
                }
                basket.get(position).add(dto);
            }
        });
        return basket;
    }

    private List<List<Pair<CardDTO, CardState>>> collectPresentData(List<List<CardDTO>> disorderedData) {
        List<List<Pair<CardDTO, CardState>>> collectBasket = new ArrayList<>();
        boolean start = true;
        boolean hasNext = false;
        for (List<CardDTO> data : disorderedData) {
            if (hasNext || start) {
                hasNext = findPresentData(collectBasket, data, mPresentFlags.get(disorderedData.indexOf(data)));
                start = false;
            } else
                break;
        }
        return collectBasket;
    }

    public LiveData<List<List<CardDTO>>> getAllLiveData() {
        return mListLiveDataByContainer;
    }

    private boolean findPresentData(List<List<Pair<CardDTO, CardState>>> basket, List<CardDTO> disorderedData, int orderFlag) {
        List<Pair<CardDTO, CardState>> smallBasket = new ArrayList<>();
        for (CardDTO dto : disorderedData) {
            if (dto.getBossNo() == orderFlag) {
                Pair<CardDTO, CardState> cardDataPair = Pair.create(dto, new CardState(CardState.FRONT_DISPLAYING));
                smallBasket.add(cardDataPair);
            }
        }
        if (!smallBasket.isEmpty()) {
            smallBasket.sort(Comparator.comparing(p -> p.first));
            basket.add(smallBasket);
        }
        return !smallBasket.isEmpty();
    }

    /* Drop Utils*/
    public interface DropDataInsertListener extends Consumer<CardEntity>{
        void accept(CardEntity cardEntity);
        void startDropFinishAnimation();
    }

    public DropDataInsertListener orderDropDataInsertListener(CardDTO targetDTO, List<Pair<CardDTO, CardState>> targetItemList, RecyclerView targetRecyclerView, View targetView, @Nullable View followingView){
        return new DropDataInsertListener() {
            @Override
            public void accept(CardEntity foundEntity) {
                int targetSeqNo = targetDTO.getSeqNo();
                targetItemList.add(targetSeqNo + 1, Pair.create(foundEntity.toDTO(), new CardState()));
                ((Activity)targetView.getContext()).runOnUiThread(()->{
                        Objects.requireNonNull(targetRecyclerView.getAdapter()).notifyItemInserted(targetSeqNo + 1);
                        targetRecyclerView.scrollToPosition(targetSeqNo + 1);
                        startDropFinishAnimation();
                });
            }

            @Override
            public void startDropFinishAnimation() {
                slowOut(targetView,true, CARD_LOCATION_LEFT);
                if (Optional.ofNullable(followingView).isPresent()){
                    slowOut(followingView,true, CARD_LOCATION_RIGHT);
                }
            }
        };
    }

    /* Container Level */
    public int presentContainerCount() {
        return mPresentData.size();
    }

    /* Card Level */
    public int getPresentCardCount(int containerPosition) {
        if (containerPosition != -1)
            return mPresentData.get(containerPosition).size();
        return 0;
    }

    public CardDTO getCardDTO(int containerPosition, int cardPosition) {
        return mPresentData.get(containerPosition).get(cardPosition).first;
    }

    public CardState getCardState(int containerPosition, int cardPosition) {
        return mPresentData.get(containerPosition).get(cardPosition).second;
    }

    public void updateCard(CardDTO cardDTO) {
        mCardRepository.update(cardDTO.toEntity());
    }

    @BindingAdapter("onTouchListener")
    public static void setOnTouchListener(View view, View.OnTouchListener listener) {
        view.setOnTouchListener(listener);
    }

    @BindingAdapter("onDragListener")
    public static void setOnDragListener(View view, View.OnDragListener listener) {
        view.setOnDragListener(listener);
    }

    @BindingAdapter("onLongClickListener")
    public static void setOnLongClickListener(View view, View.OnLongClickListener listener) {
        view.setOnLongClickListener(listener);
    }

    public View.OnLongClickListener getOnLongListenerForCreateCardUtilFab() {
        return onLongListenerForCreateCardUtilFab;
    }

    public View.OnDragListener getOnDragListenerForCardRecyclerView() {
        return onDragListenerForCardRecyclerView;
    }
}