package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import android.app.Application;
import android.content.ClipData;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Pair;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.R;
import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardEntity;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardState;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.source.CardRepository;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.source.OnDataLoadListener;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardAdapter;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.ContactCardViewHolder;
import com.choco_tyranno.mycardtree.databinding.ItemCardFrameBinding;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CardTreeViewModel extends AndroidViewModel {
    private final CardRepository mCardRepository;
    private final MutableLiveData<List<List<CardDTO>>> mListLiveDataByContainer;
    private final List<List<CardDTO>> allData;

    private final List<Integer> mPresentFlags;
    private final List<List<Pair<CardDTO, CardState>>> mPresentData;

    //    private final View.OnTouchListener onTouchListenerForAddCardUtilFab;
    private final View.OnLongClickListener onLongListenerForCreateCardUtilFab;
    private View.OnDragListener onDragListenerForCard;

    public CardTreeViewModel(Application application) {
        super(application);
        mCardRepository = new CardRepository(application);
        mListLiveDataByContainer = new MutableLiveData<>();
        mPresentFlags = new ArrayList<>();
        allData = new ArrayList<>();
        mPresentData = new ArrayList<>();
        onLongListenerForCreateCardUtilFab = (view) -> view.startDragAndDrop(ClipData.newPlainText("", ""), new CardShadow(view), null, 0);
        onDragListenerForCard = (v, event) -> {
            if (!(v instanceof MaterialCardView)) {
                return false;
            }

            FrameLayout parentView = (FrameLayout) v.getParent();
            RecyclerView cardRecyclerView = (RecyclerView) parentView.getParent();
            int position = cardRecyclerView.getChildAdapterPosition(parentView);
            CardAdapter cardAdapter = (CardAdapter) cardRecyclerView.getAdapter();
            ItemCardFrameBinding cardFrameBinding = ((ContactCardViewHolder) cardRecyclerView.getChildViewHolder(parentView)).getBinding();
            CardDTO cardDTO = cardFrameBinding.getCard();
            int targetSeqNo = cardDTO.getSeqNo();
            int targetContainerNo = cardDTO.getContainerNo();
            int targetBossNo = cardDTO.getBossNo();
            List<Pair<CardDTO, CardState>> targetContainerPresentCardItems = mPresentData.get(targetContainerNo - 1);

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:
//                    ((MaterialCardView) v).setCardBackgroundColor(Color.YELLOW);
                    //Create CardDTO
                    CardDTO newCardDTO = new CardDTO.Builder().bossNo(targetBossNo).seqNo(targetSeqNo + 1).containerNo(targetContainerNo).build();
                    targetContainerPresentCardItems.add(targetSeqNo + 1, Pair.create(newCardDTO, new CardState(CardState.FRONT_DISPLAYING)));
                    if (targetContainerPresentCardItems.size() > targetSeqNo + 2) {
                        toPushBackSeqListItems(targetContainerPresentCardItems, targetSeqNo + 2);
                    }
                    Optional.ofNullable(cardAdapter).ifPresent((cardAdapter1 -> cardAdapter.notifyItemInserted(targetSeqNo + 1)));
                    cardRecyclerView.smoothScrollToPosition(targetSeqNo + 1);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    targetContainerPresentCardItems.remove(targetSeqNo);
                    if (targetContainerPresentCardItems.size() > targetSeqNo) {
                        takeListItemsSeqBack(targetContainerPresentCardItems, targetSeqNo);
                    }
                    Optional.ofNullable(cardAdapter).ifPresent((cardAdapter1 -> cardAdapter.notifyItemRemoved(targetSeqNo)));
                case DragEvent.ACTION_DRAG_ENDED:
//                    ((MaterialCardView) v).setCardBackgroundColor(v.getResources().getColor(R.color.colorPrimary));
                    break;
                case DragEvent.ACTION_DROP:
//                    FrameLayout parentView = (FrameLayout) v.getParent();
//                    RecyclerView cardRecyclerView = (RecyclerView) parentView.getParent();
//                    int position = cardRecyclerView.getChildAdapterPosition(parentView);
//                    CardAdapter cardAdapter= (CardAdapter)cardRecyclerView.getAdapter();
//                    ItemCardFrameBinding cardFrameBinding = ((ContactCardViewHolder)cardRecyclerView.getChildViewHolder(parentView)).getBinding();
//                    CardDTO cardDTO = cardFrameBinding.getCard();
//                    int targetCardNo = cardDTO.getCardNo();
//                    int targetSeqNo = cardDTO.getSeqNo();
//                    int targetContainerNo = cardDTO.getContainerNo();
//                    int targetBossNo = cardDTO.getBossNo();
//                    //Create CardDTO
//                    CardDTO newCardDTO = new CardDTO.Builder().bossNo(targetBossNo).seqNo(targetSeqNo).containerNo(targetContainerNo).build();
//                    //align seqNo
//
//                    //add CardDTO to present List
//
//                    //sort??
//
//                    //insert CardDTO data
//                    mCardRepository.insertCard(newCardDTO.toEntity());
                    break;
            }
            return true;
        };
    }

    private void takeListItemsSeqBack(List<Pair<CardDTO, CardState>> list, int start) {
        for (int i = start; i < list.size(); i++) {
            list.get(i).first.setSeqNo(i);
        }
    }

    private void toPushBackSeqListItems(List<Pair<CardDTO, CardState>> list, int start) {
        for (int i = start; i < list.size(); i++) {
            list.get(i).first.setSeqNo(i + 1);
        }
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

//    public void addCard(CardDTO newData) {
//        mCardRepository.insertCard(newData.toEntity());
//    }

//    public void updateCard(CardDTO modData) {
////      _dtoCards.setValue() -> trigger onChanged
//        mCardRepository.updateCard(modData.toEntity());
//    }

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

    //    public View.OnTouchListener getOnTouchListener(){
//        return onTouchListenerForAddCardUtilFab;
//    }
    public View.OnLongClickListener getOnLongListenerForCreateCardUtilFab() {
        return onLongListenerForCreateCardUtilFab;
    }

    public View.OnDragListener getOnDragListenerForCard() {
        return onDragListenerForCard;
    }


}