package com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv;

import android.content.ClipData;
import android.os.Handler;
import android.util.Pair;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.CardViewModel;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.MainCardActivity;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.CardContainerViewHolder;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.CloneCardShadow;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.Container;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.ContainerAdapter;
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
        Pair<List<CardDTO>, List<CardDTO>> savedOriginData = prepareDragStart(view);
        Logger.hotfixMessage("1st");
        view.startDragAndDrop(ClipData.newPlainText("", "")
                , new CloneCardShadow(CardViewShadowProvider.getInstance(view.getContext(), cardDTO), cardDTO)
                , Pair.create("MOVE", Pair.create(cardDTO, savedOriginData)), 0);
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
    private Pair<List<CardDTO>, List<CardDTO>> prepareDragStart(View view) {
        CardRecyclerView cardRecyclerView = findTargetCardRecyclerView(view);
        if (cardRecyclerView == null)
            return null;
        CardViewModel viewModel = findCardViewModel(view);

        List<CardDTO> savedMovingData = new ArrayList<>();
        List<CardDTO> savedNextData = new ArrayList<>();
        Pair<List<CardDTO>, List<CardDTO>> preparedData = Pair.create(savedMovingData, savedNextData);
        viewModel.findChildrenCards(cardDTO, savedMovingData);
        savedMovingData.add(cardDTO);
        viewModel.findNextCards(cardDTO.getContainerNo(), cardDTO.getSeqNo(), savedNextData);
        viewModel.removeFromAllList(savedMovingData.toArray(new CardDTO[0]));
        final boolean hasLeftItemInTargetContainer = viewModel.removeSinglePresentCardDto(cardDTO);
        if (!savedNextData.isEmpty()) {
            viewModel.reduceListSeq(savedNextData);
        }
        if (hasLeftItemInTargetContainer) {
            if (cardRecyclerView.getAdapter() == null)
                throw new RuntimeException("#prepareDragStart - cardRecyclerView is null");
            cardRecyclerView.getAdapter().notifyItemRemoved(cardDTO.getSeqNo());
            ((MainCardActivity) cardRecyclerView.getContext()).getMainHandler().postDelayed(() -> {
                final int newFocusPosition = viewModel.findNearestItemPosition(cardDTO.getContainerNo(), cardDTO.getSeqNo());
                Container container = viewModel.getContainer(cardDTO.getContainerNo());
                if (container != null) {
                    container.setFocusCardPosition(newFocusPosition);
                    cardRecyclerView.smoothScrollToPosition(newFocusPosition);
                    viewModel.presentChildren(cardRecyclerView, cardDTO.getContainerNo(), newFocusPosition);
                }
            }, 150);
        } else {
            ContainerRecyclerView containerRecyclerView = (ContainerRecyclerView) cardRecyclerView.getParent().getParent();
            ContainerAdapter containerAdapter = (ContainerAdapter) containerRecyclerView.getAdapter();
            if (containerAdapter == null)
                return preparedData;
            LinearLayoutManager containerLayoutManager = containerRecyclerView.getLayoutManager();
            if (containerLayoutManager == null)
                return preparedData;
            int cardContainerCount = containerLayoutManager.getItemCount() - 1;
            int removeCount = cardContainerCount - (cardDTO.getContainerNo() + 1) + 1;
            viewModel.clearContainerPositionPresentData(cardDTO.getContainerNo());
            viewModel.clearContainerAtPosition(cardDTO.getContainerNo());
            containerAdapter.notifyItemRangeRemoved(cardDTO.getContainerNo(), removeCount);
        }
        return preparedData;
    }

    private CardRecyclerView findTargetCardRecyclerView(View view) {
        ContainerRecyclerView containerRecyclerView = ((MainCardActivity) view.getContext()).getMainBinding().mainScreen.mainBody.containerRecyclerview;
        RecyclerView.ViewHolder viewHolder = containerRecyclerView.findViewHolderForAdapterPosition(cardDTO.getContainerNo());
        if (viewHolder instanceof CardContainerViewHolder)
            return ((CardContainerViewHolder) viewHolder).getBinding().cardRecyclerview;
        return null;
    }

    private CardViewModel findCardViewModel(View view) {
        return ((MainCardActivity) view.getContext()).getCardViewModel();
    }
}
