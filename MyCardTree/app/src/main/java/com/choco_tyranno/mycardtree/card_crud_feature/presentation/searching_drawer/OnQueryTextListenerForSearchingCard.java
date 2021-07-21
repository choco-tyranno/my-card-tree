package com.choco_tyranno.mycardtree.card_crud_feature.presentation.searching_drawer;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.CardViewModel;

public class OnQueryTextListenerForSearchingCard implements SearchView.OnQueryTextListener {
    private CardViewModel viewModel;

    public OnQueryTextListenerForSearchingCard(CardViewModel viewModel){
        this.viewModel = viewModel;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String queryText) {
        viewModel.searchCards(queryText);
        viewModel.resetFocusPageNo();

        viewModel.getPageNavigationRecyclerViewAdapter().notifyDataSetChanged();
        viewModel.getSearchingResultRecyclerViewAdapter().notifyDataSetChanged();
        return false;
    }
}
