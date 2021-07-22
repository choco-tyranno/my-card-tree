package com.choco_tyranno.mycardtree.card_crud_feature.presentation.searching_drawer;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.SingleToastManager;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.SingleToaster;
import com.choco_tyranno.mycardtree.databinding.ItemSearchingResultBinding;

public class OnClickListenerForFindingSearchingResultTargetButton implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        View parentView = (View) v.getParent();
        RecyclerView resultRecyclerView = (RecyclerView) parentView.getParent();
        int targetPosition = resultRecyclerView.getChildAdapterPosition(parentView);
        SearchingResultAdapter.SearchingResultViewHolder searchingResultViewHolder =(SearchingResultAdapter.SearchingResultViewHolder) resultRecyclerView.findViewHolderForAdapterPosition(targetPosition);
        if (searchingResultViewHolder==null){
            return;
        }
        ItemSearchingResultBinding binding = searchingResultViewHolder.getBinding();
        SingleToastManager.show(SingleToaster.makeTextShort(v.getContext(),"bubbling success / "+binding.getCard().getTitle()));
    }


}
