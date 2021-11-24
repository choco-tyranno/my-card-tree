package com.choco_tyranno.team_tree.presentation.searching_drawer;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.team_tree.presentation.CardViewModel;

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
        viewModel.notifySearchQueryTextChanged(queryText);
        return false;
    }
}
