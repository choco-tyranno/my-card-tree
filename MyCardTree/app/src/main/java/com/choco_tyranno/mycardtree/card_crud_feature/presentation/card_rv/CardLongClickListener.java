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
import java.util.Objects;
import java.util.Optional;


public class CardLongClickListener implements View.OnLongClickListener {
    private CardDTO cardDTO;

    public CardLongClickListener() {
    }


    @Override
    public boolean onLongClick(View view) {
        if (!Optional.ofNullable(cardDTO).isPresent())
            throw new RuntimeException("CardLongClickListener#onLongClick - cardDTO is null");
        //check init AllData
//        Pair<List<CardDTO>, List<CardDTO>> savedOriginData = prepareDragStart(view);
        Pair<List<CardDTO>, List<CardDTO>> savedOriginData = prepareDragStart(view);
        view.startDragAndDrop(ClipData.newPlainText("", "")
                , new CloneCardShadow(CardViewShadowProvider.getInstance(view.getContext(), cardDTO), cardDTO)
                , Pair.create("MOVE", Pair.create(cardDTO, savedOriginData)), 0);
//        CardRecyclerView cardRecyclerView = findTargetCardRecyclerView(view);
//        cardRecyclerView.setAdapter(null);
        return true;
    }

    public void setCard(CardDTO card) {
        cardDTO = card;
    }

    /*
     * Prepare moving cards data & detach moving cards.
     * moving data - has no change. for drop.
     * next data - seq reduced. for rollback.
     * return : Pair<> (first : moving-data, second : next data),
     * */
    private Pair<List<CardDTO>, List<CardDTO>>  prepareDragStart(View view) {
        CardRecyclerView cardRecyclerView = findTargetCardRecyclerView(view);
        CardViewModel viewModel = findCardViewModel(view);

        List<CardDTO> savedMovingData = new ArrayList<>();
        List<CardDTO> savedNextData = new ArrayList<>();
        Pair<List<CardDTO>, List<CardDTO>> preparedData = Pair.create(savedMovingData, savedNextData);


        viewModel.findChildrenCards(cardDTO, savedMovingData);
        savedMovingData.add(cardDTO);
        viewModel.findNextCards(cardDTO.getContainerNo(),cardDTO.getSeqNo(),savedNextData);
        viewModel.removeFromAllList(savedMovingData.toArray(new CardDTO[0]));

        //Start detaching moving card views.
        final boolean hasLeftItemInTargetContainer = viewModel.removeSinglePresentCardDto(cardDTO);
        //reduce next cards seqNo.
        if (!savedNextData.isEmpty()) {
            viewModel.reduceListSeq(savedNextData);
        }


        //remove from PresentData
        if(hasLeftItemInTargetContainer){
            if (cardRecyclerView.getAdapter()==null)
                throw new RuntimeException("#prepareDragStart - cardRecyclerView is null");
            cardRecyclerView.getAdapter().notifyItemRemoved(cardDTO.getSeqNo());
            final int newFocusPosition = viewModel.findNearestItemPosition(cardDTO.getContainerNo(), cardDTO.getSeqNo());
            viewModel.getContainer(cardDTO.getContainerNo()).setFocusCardPosition(newFocusPosition);
            cardRecyclerView.smoothScrollToPosition(newFocusPosition);
            viewModel.presentChildren(cardRecyclerView, cardDTO.getContainerNo(), newFocusPosition);

        }else {
            viewModel.clearContainerPositionPresentData(cardDTO.getContainerNo());
            viewModel.clearContainerAtPosition(cardDTO.getContainerNo());
            ContainerRecyclerView containerRecyclerView = (ContainerRecyclerView) cardRecyclerView.getParent().getParent();
            if (cardDTO.getContainerNo()==0){
                Objects.requireNonNull(containerRecyclerView.getAdapter()).notifyDataSetChanged();
                return preparedData;
            }
            final int aboveContainerPosition =cardDTO.getContainerNo()-1;
            //to clear method.
            containerRecyclerView.smoothScrollToPosition(aboveContainerPosition);
            //delay needed?
            findMainHandler(view).postDelayed(()->{
                CardRecyclerView aboveCardRecyclerView = Objects.requireNonNull(containerRecyclerView.findViewHolderForAdapterPosition(aboveContainerPosition)).getBinding().cardRecyclerview;
                final int aboveContainerFocusCardPosition = viewModel.getContainer(aboveContainerPosition).getFocusCardPosition();
                viewModel.presentChildren(aboveCardRecyclerView, aboveContainerPosition, aboveContainerFocusCardPosition);
            },260);
//            cardRecyclerView.setAdapter(null);
        }
        return preparedData;
    }

    private Handler findMainHandler(View view) {
        return ((MainCardActivity) view.getContext()).getMainHandler();
    }

    private CardRecyclerView findTargetCardRecyclerView(View view) {
        ContainerRecyclerView containerRecyclerView = ((MainCardActivity) view.getContext()).getMainBinding().mainScreen.mainBody.containerRecyclerview;
        return Objects.requireNonNull(containerRecyclerView.findViewHolderForAdapterPosition(cardDTO.getContainerNo())).getBinding().cardRecyclerview;
    }

    private CardViewModel findCardViewModel(View view) {
        return ((MainCardActivity) view.getContext()).getCardViewModel();
    }
}
