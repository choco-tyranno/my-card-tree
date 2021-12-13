package com.choco_tyranno.team_tree.presentation.searching_drawer;

import android.util.Pair;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.team_tree.databinding.ItemSearchingResultBinding;
import com.choco_tyranno.team_tree.domain.card_data.CardDto;
import com.choco_tyranno.team_tree.presentation.CardViewModel;
import com.choco_tyranno.team_tree.presentation.main.MainCardActivity;

import java.lang.reflect.Array;

public class OnClickListenerForFindingSearchingResultTargetButton implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        MainCardActivity mainCardActivity = (MainCardActivity) v.getContext();
        CardFinder cardFinder = mainCardActivity.getCardFinder();
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
        CardDto cardDTO = binding.getCard();
        Pair<Integer, Integer[]> scrollUtilDataForFindingOutCard =  viewModel.findScrollUtilDataForFindingOutCard(cardDTO);
        if (scrollUtilDataForFindingOutCard.second.length==0)
            scrollUtilDataForFindingOutCard = Pair.create(cardDTO.getContainerNo(), scrollUtilDataForFindingOutCard.second);
        mainCardActivity.scrollToFindingTargetCard(scrollUtilDataForFindingOutCard, ()->cardFinder.setSendingFindCardReq(false));
    }
}
