package com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv;

import android.content.ClipData;
import android.os.Handler;
import android.util.Pair;
import android.view.View;

import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.CardViewModel;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.MainCardActivity;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.CloneCardShadow;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.ContainerRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class CardLongClickListener implements View.OnLongClickListener {
    private CardDTO cardDTO;

    public CardLongClickListener() {
    }

    @Override
    public boolean onLongClick(View view) {
        if (!Optional.ofNullable(cardDTO).isPresent())
            throw new RuntimeException("CardLongClickListener#onLongClick - cardDTO is null");
        Pair<List<CardDTO>, List<CardDTO>> savedOriginData = prepareDragStart(view);

        Logger.hotfixMessage("LongClicked / savedOriginData first : "+savedOriginData.first.size()
                +" / second : "+savedOriginData.second.size());

        return view.startDragAndDrop(ClipData.newPlainText("", "")
                , new CloneCardShadow(CardViewShadowProvider.getInstance(view.getContext(), cardDTO), cardDTO)
                , Pair.create("MOVE", Pair.create(cardDTO, savedOriginData)), 0);
    }

    public void setCard(CardDTO card) {
        cardDTO = card;
    }

    /*
     * moving data -> has no change.
     * next data -> seq reduced.
     * return : Pair<> (first : moving-data, second : next data),
     * */
    private Pair<List<CardDTO>, List<CardDTO>> prepareDragStart(View view) {
        CardRecyclerView cardRecyclerView = findTargetCardRecyclerView(view);
        CardViewModel viewModel = findCardViewModel(view);

        List<CardDTO> savedMovingData = new ArrayList<>();
        List<CardDTO> savedNextData = new ArrayList<>();
        Pair<List<CardDTO>, List<CardDTO>> preparedData = Pair.create(savedMovingData, savedNextData);

        List<CardDTO> movingItemList = new ArrayList<>();

        viewModel.findChildrenCards(cardDTO, movingItemList);
        movingItemList.add(cardDTO);
        //clone
        savedMovingData.addAll(CardDTO.cloneList(movingItemList));

        CardDTO.orderByContainerNoDesc(movingItemList);
        viewModel.removeFromAllList(movingItemList.toArray(new CardDTO[0]));

        List<CardDTO> foundNextCards = viewModel.findNextCards(cardDTO.getContainerNo(), cardDTO.getSeqNo());
        //clone
        savedNextData.addAll(CardDTO.cloneList(foundNextCards));

        //reduce next cards seqNo.
        if (!foundNextCards.isEmpty()) {
            viewModel.reduceListSeq(foundNextCards);
        }

        final boolean hasLeftItem = viewModel.removeSinglePresentCardDto(cardDTO);
        cardRecyclerView.getAdapter().notifyItemRemoved(cardDTO.getSeqNo());

        if (!hasLeftItem) {
            viewModel.clearContainerPositionPresentData(cardDTO.getContainerNo());
            return preparedData;
        }
        int newFocusPosition = viewModel.findNearestItemPosition(cardDTO.getContainerNo(), cardDTO.getSeqNo());
        viewModel.getContainer(cardDTO.getContainerNo()).setFocusCardPosition(newFocusPosition);
        findMainHandler(view).postDelayed(() -> {
            cardRecyclerView.smoothScrollToPosition(newFocusPosition);
            viewModel.presentChildren(cardRecyclerView, cardDTO.getContainerNo(), newFocusPosition);
        }, 500);
        return preparedData;
    }

    private Handler findMainHandler(View view) {
        return ((MainCardActivity) view.getContext()).getMainHandler();
    }

    private CardRecyclerView findTargetCardRecyclerView(View view) {
        ContainerRecyclerView containerRecyclerView = ((MainCardActivity) view.getContext()).getMainBinding().mainScreen.mainBody.containerRecyclerview;
        return containerRecyclerView.findViewHolderForAdapterPosition(cardDTO.getContainerNo()).getBinding().cardRecyclerview;
    }

    private CardViewModel findCardViewModel(View view) {
        return ((MainCardActivity) view.getContext()).getCardViewModel();
    }
}
