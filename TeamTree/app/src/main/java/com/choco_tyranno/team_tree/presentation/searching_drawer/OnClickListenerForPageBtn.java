package com.choco_tyranno.team_tree.presentation.searching_drawer;

import android.view.View;

import com.choco_tyranno.team_tree.presentation.CardViewModel;
import com.choco_tyranno.team_tree.presentation.MainCardActivity;
import com.google.android.material.button.MaterialButton;

public class OnClickListenerForPageBtn implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        CardViewModel viewModel = ((MainCardActivity)v.getContext()).getCardViewModel();
        MaterialButton view = (MaterialButton) v;
        final int pageNo = Integer.parseInt(view.getText().toString());
        viewModel.setFocusPageNo(pageNo);
        viewModel.getSearchingResultRecyclerViewAdapter().notifyDataSetChanged();
    }
}
