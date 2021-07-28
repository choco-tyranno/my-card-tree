package com.choco_tyranno.team_tree.presentation.searching_drawer;

import android.text.TextUtils;
import android.view.View;

import com.choco_tyranno.team_tree.presentation.CardViewModel;
import com.choco_tyranno.team_tree.presentation.MainCardActivity;

public class OnClickListenerForMovingPageBundleBtn implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        CardViewModel viewModel = ((MainCardActivity) v.getContext()).getCardViewModel();
        String viewName = v.getResources().getResourceEntryName(v.getId());
        if (TextUtils.equals(viewName, "prev_page_btn"))
            onPrevBtnClicked(viewModel);
        if (TextUtils.equals(viewName, "next_page_btn"))
            onNextBtnClicked(viewModel);
    }

    private void onPrevBtnClicked(CardViewModel viewModel) {
        if (!viewModel.hasPrevPageBundle())
            return;
        int prevPageNo = viewModel.countBaseMaxPageBundle() * CardViewModel.VISIBLE_PAGE_ITEM_MAX_COUNT;
        viewModel.setFocusPageNo(prevPageNo);
        viewModel.getSearchingResultRecyclerViewAdapter().notifyDataSetChanged();
        viewModel.getPageNavigationRecyclerViewAdapter().notifyDataSetChanged();
    }

    private void onNextBtnClicked(CardViewModel viewModel) {
        if (!viewModel.hasNextPageBundle())
            return;
        int nextPageNo = viewModel.countBaseMaxPageBundle() * CardViewModel.VISIBLE_PAGE_ITEM_MAX_COUNT + CardViewModel.VISIBLE_PAGE_ITEM_MAX_COUNT+1;
        viewModel.setFocusPageNo(nextPageNo);
        viewModel.getSearchingResultRecyclerViewAdapter().notifyDataSetChanged();
        viewModel.getPageNavigationRecyclerViewAdapter().notifyDataSetChanged();
    }
}
