package com.choco_tyranno.mycardtree.card_crud_feature.presentation.searching_drawer;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.card_crud_feature.presentation.CardViewModel;

public class PageNavigationAdapter extends RecyclerView.Adapter<PageNavigationAdapter.PageNavigationViewHolder> {
    CardViewModel viewModel;

    public PageNavigationAdapter(CardViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public PageNavigationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PageNavigationViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class PageNavigationViewHolder extends RecyclerView.ViewHolder {
        public PageNavigationViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
