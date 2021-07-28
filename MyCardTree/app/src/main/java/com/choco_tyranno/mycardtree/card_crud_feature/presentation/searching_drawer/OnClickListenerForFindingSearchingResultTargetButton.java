package com.choco_tyranno.mycardtree.card_crud_feature.presentation.searching_drawer;

import android.util.Pair;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.CardViewModel;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.MainCardActivity;
import com.choco_tyranno.mycardtree.databinding.ItemSearchingResultBinding;

public class OnClickListenerForFindingSearchingResultTargetButton implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        MainCardActivity mainCardActivity = (MainCardActivity) v.getContext();
        CardFinder cardFinder = mainCardActivity.getFindCardBtn();
        CardViewModel viewModel = mainCardActivity.getCardViewModel();
        if (cardFinder.isSendingFindCardReq()){
            return;
        }
        cardFinder.animate(v);
        View parentView = (View) v.getParent();
        RecyclerView resultRecyclerView = (RecyclerView) parentView.getParent();
        int targetPosition = resultRecyclerView.getChildAdapterPosition(parentView);
        SearchingResultAdapter.SearchingResultViewHolder searchingResultViewHolder =(SearchingResultAdapter.SearchingResultViewHolder) resultRecyclerView.findViewHolderForAdapterPosition(targetPosition);
        if (searchingResultViewHolder==null){
            return;
        }
        ItemSearchingResultBinding binding = searchingResultViewHolder.getBinding();
        CardDTO cardDTO = binding.getCard();
        Pair<Integer, Integer[]> scrollUtilDataForFindingOutCard =  viewModel.findScrollUtilDataForFindingOutCard(cardDTO);
        if (scrollUtilDataForFindingOutCard.second.length==0)
            scrollUtilDataForFindingOutCard = Pair.create(cardDTO.getContainerNo(), scrollUtilDataForFindingOutCard.second);
        mainCardActivity.scrollToFindingTargetCard(scrollUtilDataForFindingOutCard);
    }
}
