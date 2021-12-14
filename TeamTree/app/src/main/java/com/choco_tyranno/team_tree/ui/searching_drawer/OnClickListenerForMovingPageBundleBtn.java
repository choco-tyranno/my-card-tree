package com.choco_tyranno.team_tree.ui.searching_drawer;

import android.view.View;

import com.choco_tyranno.team_tree.R;
import com.choco_tyranno.team_tree.ui.CardViewModel;
import com.choco_tyranno.team_tree.ui.main.MainCardActivity;

public class OnClickListenerForMovingPageBundleBtn implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        CardViewModel viewModel = ((MainCardActivity) v.getContext()).getCardViewModel();
        if (v.getId()== R.id.prev_page_btn)
            onPrevBtnClicked(viewModel);
        if (v.getId()== R.id.next_page_btn)
            onNextBtnClicked(viewModel);
    }

    private void onPrevBtnClicked(CardViewModel viewModel) {
        viewModel.preparePrevPagers();
    }

    private void onNextBtnClicked(CardViewModel viewModel) {
        viewModel.prepareNextPagers();
    }
}
