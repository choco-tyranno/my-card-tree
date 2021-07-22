package com.choco_tyranno.mycardtree.card_crud_feature.presentation.searching_drawer;

import android.view.View;

import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.CardViewModel;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.MainCardActivity;
import com.google.android.material.button.MaterialButton;

public class OnClickListenerForPageBtn implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        CardViewModel viewModel = ((MainCardActivity)v.getContext()).getCardViewModel();
        MaterialButton view = (MaterialButton) v;
        final int pageNo = Integer.parseInt(view.getText().toString());
        viewModel.setFocusPageNo(pageNo);
        viewModel.getSearchingResultRecyclerViewAdapter().notifyDataSetChanged();
        viewModel.getPageNavigationRecyclerViewAdapter().notifyDataSetChanged();
    }
}
