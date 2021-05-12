package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import android.app.Activity;
import android.app.Application;
import android.content.ClipData;
import android.util.Pair;
import android.view.DragEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.Toast;

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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CardTreeViewModel extends AndroidViewModel {
    private final CardRepository mCardRepository;
    private final MutableLiveData<List<List<CardDTO>>> mListLiveDataByContainer;
    private final List<List<CardDTO>> allData;

    private final List<Integer> mPresentFlags;
    private final List<List<Pair<CardDTO, CardState>>> mPresentData;

    //    private final View.OnTouchListener onTouchListenerForAddCardUtilFab;
    private final View.OnLongClickListener onLongListenerForCreateCardUtilFab;
    //    private final View.OnDragListener onDragListenerForCard;
    private View.OnDragListener onDragListenerForCardRecyclerView;

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

    // view : card recyclerView
    private void initCardRecyclerViewDragListener() {
        onDragListenerForCardRecyclerView = (view, event) -> {
            if (view instanceof RecyclerView) {
                if (event.getAction() == DragEvent.ACTION_DRAG_STARTED) {
                    return true;
                }
                RecyclerView targetView = (RecyclerView) view;
                LinearLayoutManager layoutManager = (LinearLayoutManager) targetView.getLayoutManager();
                if (!Optional.ofNullable(layoutManager).isPresent()){
                    throw new RuntimeException("#initCardRecyclerViewDragListener/ layoutManager is null");
                }
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                boolean result;
                if (firstVisibleItemPosition == lastVisibleItemPosition) {
                    result = handleDragEventSingleItemVisibleCase(targetView, event);
                    return result;
                }
                result = handleDragEventMultiItemVisibleCase(targetView, event);
                return result;

            } else {
                throw new RuntimeException("#ondrag() : none recyclerview detected");
            }
        };
    }

    private boolean handleDragEventSingleItemVisibleCase(RecyclerView rv, DragEvent event) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) rv.getLayoutManager();
        boolean isLmNull = Optional.ofNullable(layoutManager).isPresent();
        if (!isLmNull) {
            throw new RuntimeException("#handleDragEventSingleItemVisibleCase/LM is null");
        }
        int visibleCardItemPos = layoutManager.findFirstVisibleItemPosition();

//        FrameLayout targetView = (FrameLayout) layoutManager.getChildAt(visibleCardItemPos);
        ContactCardViewHolder targetViewVH = (ContactCardViewHolder) rv.findViewHolderForAdapterPosition(visibleCardItemPos);
        FrameLayout targetView = (FrameLayout)targetViewVH.getBinding().cardContainerFrameLayout;

        if (!Optional.ofNullable(targetView).isPresent()) {
            throw new RuntimeException("#handleDragEventSingleItemVisibleCase()/layoutManager#getChildAt(visibleCardItemPos) is null / ");
        }
        int screenWidth = ((Activity) targetView.getContext()).getWindowManager().getCurrentWindowMetrics().getBounds().right;

        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_ENTERED:
                targetView.animate()
                        .setInterpolator(AnimationUtils.loadInterpolator(targetView.getContext(), android.R.anim.accelerate_decelerate_interpolator))
                        .setDuration(200)
                        .translationXBy(0)
                        .translationX(-screenWidth).start();
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                targetView.animate()
                        .setInterpolator(AnimationUtils.loadInterpolator(targetView.getContext(), android.R.anim.accelerate_decelerate_interpolator))
                        .setDuration(200)
                        .translationXBy(-screenWidth)
                        .translationX(0).start();
                return true;
            case DragEvent.ACTION_DROP:
                CardDTO cardDTO = ((ContactCardViewHolder)rv.getChildViewHolder(targetView)).getBinding().getCard();
                int targetSeqNo = cardDTO.getSeqNo();
                int targetBossNo = cardDTO.getBossNo();
                int targetContainerNo = cardDTO.getContainerNo();

                List<Pair<CardDTO, CardState>> targetContainerCardList = mPresentData.get(targetContainerNo);

                CardDTO newCardDTO = new CardDTO.Builder().seqNo(targetSeqNo+1).bossNo(targetBossNo).containerNo(targetContainerNo).build();
                if (targetContainerCardList.size() > targetSeqNo+1){
                    mCardRepository.insertCardAndUpdateCardsSeq(newCardDTO.toEntity(), dtoListToEntityList(increaseListCardsSeq(targetContainerCardList, targetSeqNo+1)));
                }else {
                    mCardRepository.insertCard(newCardDTO.toEntity());
                }

                //check is LiveData inserted.

                targetContainerCardList.add(targetSeqNo+1, Pair.create(newCardDTO, new CardState()));
                rv.getAdapter().notifyItemInserted(targetSeqNo+1);
                rv.scrollToPosition(targetSeqNo+1);

                targetView.animate()
                        .setInterpolator(AnimationUtils.loadInterpolator(targetView.getContext(), android.R.anim.accelerate_decelerate_interpolator))
                        .setDuration(200)
                        .translationXBy(-screenWidth)
                        .translationX(0).start();
                return true;
        }
        return false;
    }

    private boolean handleDragEventMultiItemVisibleCase(RecyclerView rv, DragEvent event) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) rv.getLayoutManager();
        boolean isLmNull = Optional.ofNullable(layoutManager).isPresent();
        if (!isLmNull) {
            throw new RuntimeException("#handleDragEventSingleItemVisibleCase/LM is null");
        }
        int firstVisibleCardItemPos = layoutManager.findFirstVisibleItemPosition();
        int lastVisibleCardItemPos = layoutManager.findLastVisibleItemPosition();

        ContactCardViewHolder firstVisibleViewVH = (ContactCardViewHolder) rv.findViewHolderForAdapterPosition(firstVisibleCardItemPos);
        FrameLayout firstVisibleView = (FrameLayout)firstVisibleViewVH.getBinding().cardContainerFrameLayout;
        ContactCardViewHolder lastVisibleViewVH = (ContactCardViewHolder) rv.findViewHolderForAdapterPosition(lastVisibleCardItemPos);
        FrameLayout lastVisibleView = (FrameLayout)lastVisibleViewVH.getBinding().cardContainerFrameLayout;

        if (!Optional.ofNullable(firstVisibleView).isPresent() || !Optional.ofNullable(lastVisibleView).isPresent()) {
            throw new RuntimeException("#handleDragEventSingleItemVisibleCase()/layoutManager#getChildAt(visibleCardItemPos) is null");
        }
        int screenWidth = ((Activity) rv.getContext()).getWindowManager().getCurrentWindowMetrics().getBounds().right;

        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_ENTERED:
                firstVisibleView.animate()
                        .setInterpolator(AnimationUtils.loadInterpolator(rv.getContext(), android.R.anim.accelerate_decelerate_interpolator))
                        .setDuration(200)
                        .translationXBy(0)
                        .translationX(-screenWidth).start();
                lastVisibleView.animate()
                        .setInterpolator(AnimationUtils.loadInterpolator(rv.getContext(), android.R.anim.accelerate_decelerate_interpolator))
                        .setDuration(200)
                        .translationXBy(0)
                        .translationX(screenWidth).start();
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                firstVisibleView.animate()
                    .setInterpolator(AnimationUtils.loadInterpolator(rv.getContext(), android.R.anim.accelerate_decelerate_interpolator))
                    .setDuration(200)
                    .translationXBy(-screenWidth)
                    .translationX(0).start();
                lastVisibleView.animate()
                        .setInterpolator(AnimationUtils.loadInterpolator(rv.getContext(), android.R.anim.accelerate_decelerate_interpolator))
                        .setDuration(200)
                        .translationXBy(screenWidth)
                        .translationX(0).start();
                return true;
            case DragEvent.ACTION_DROP:
                CardDTO cardDTO = ((ContactCardViewHolder)rv.getChildViewHolder(firstVisibleView)).getBinding().getCard();
                int targetSeqNo = cardDTO.getSeqNo();
                int targetBossNo = cardDTO.getBossNo();
                int targetContainerNo = cardDTO.getContainerNo();

                List<Pair<CardDTO, CardState>> targetContainerCardList = mPresentData.get(targetContainerNo);
                CardDTO newCardDTO = new CardDTO.Builder().seqNo(targetSeqNo+1).bossNo(targetBossNo).containerNo(targetContainerNo).build();

                if (targetContainerCardList.size() > targetSeqNo+1){
                    mCardRepository.insertCardAndUpdateCardsSeq(newCardDTO.toEntity(), dtoListToEntityList(increaseListCardsSeq(targetContainerCardList, targetSeqNo+1)));
                }else {
                    mCardRepository.insertCard(newCardDTO.toEntity());
                }

                //check is LiveData inserted.

                targetContainerCardList.add(targetSeqNo+1, Pair.create(newCardDTO, new CardState()));
                rv.getAdapter().notifyItemInserted(targetSeqNo+1);
                rv.scrollToPosition(targetSeqNo+1);
                firstVisibleView.animate()
                        .setInterpolator(AnimationUtils.loadInterpolator(rv.getContext(), android.R.anim.accelerate_decelerate_interpolator))
                        .setDuration(200)
                        .translationXBy(-screenWidth)
                        .translationX(0).start();
                lastVisibleView.animate()
                        .setInterpolator(AnimationUtils.loadInterpolator(rv.getContext(), android.R.anim.accelerate_decelerate_interpolator))
                        .setDuration(200)
                        .translationXBy(screenWidth)
                        .translationX(0).start();
                return true;
        }
        return false;
    }

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

    //sort here
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

    public LiveData<List<List<CardDTO>>> getAllLiveData() {
        return mListLiveDataByContainer;
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
        mCardRepository.updateCard(cardDTO.toEntity());
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