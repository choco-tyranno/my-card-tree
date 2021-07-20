package com.choco_tyranno.mycardtree.card_crud_feature.presentation;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.card_crud_feature.Logger;

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
//        viewModel.setSearchingQueryText(queryText);
        viewModel.searchCards(queryText);
        viewModel.resetFocusPageNo();
        viewModel.searchingResultAdapter.notifyDataSetChanged();
        return false;
    }
}
