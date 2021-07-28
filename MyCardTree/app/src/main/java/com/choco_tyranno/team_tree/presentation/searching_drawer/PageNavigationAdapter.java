package com.choco_tyranno.team_tree.presentation.searching_drawer;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.team_tree.R;
import com.choco_tyranno.team_tree.databinding.ItemPageNavigationBinding;
import com.choco_tyranno.team_tree.presentation.CardViewModel;
import com.google.android.material.button.MaterialButton;

public class PageNavigationAdapter extends RecyclerView.Adapter<PageNavigationAdapter.PageNavigationViewHolder> {
    CardViewModel viewModel;

    public PageNavigationAdapter(CardViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public PageNavigationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPageNavigationBinding binding = ItemPageNavigationBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new PageNavigationViewHolder(binding, viewModel);
    }

    @Override
    public void onBindViewHolder(@NonNull PageNavigationViewHolder holder, int position) {
        holder.bind(position, viewModel.getFocusPageNo());
    }

    @Override
    public int getItemCount() {
        return viewModel.getPageNavigationCount();
    }

    public static class PageNavigationViewHolder extends RecyclerView.ViewHolder {
        ItemPageNavigationBinding mBinding;

        public PageNavigationViewHolder(ItemPageNavigationBinding pageBinding, CardViewModel viewModel) {
            super(pageBinding.getRoot());
            mBinding = pageBinding;
            mBinding.setViewModel(viewModel);
        }

        public void bind(int position, int focusPageNo) {
            final int maxPageSetCount = Math.floorDiv(focusPageNo, CardViewModel.VISIBLE_PAGE_ITEM_MAX_COUNT);
            final boolean noRemainderPage = Math.floorMod(focusPageNo, CardViewModel.VISIBLE_PAGE_ITEM_MAX_COUNT) == 0;
            int basePageCount = maxPageSetCount * CardViewModel.VISIBLE_PAGE_ITEM_MAX_COUNT;
            if (noRemainderPage) {
                basePageCount = (maxPageSetCount - 1) * CardViewModel.VISIBLE_PAGE_ITEM_MAX_COUNT;
            }
            final int pageNumber = basePageCount + position + 1;
            mBinding.pageBtn.setText(String.valueOf(pageNumber));
            if (focusPageNo == pageNumber) {
                mBinding.pageBtn.setBackgroundColor(mBinding.pageBtn.getResources().getColor(R.color.colorPrimaryDark
                        , mBinding.pageBtn.getContext().getApplicationContext().getTheme()));
                return;
            }
            mBinding.pageBtn.setBackgroundColor(mBinding.pageBtn.getResources().getColor(R.color.colorPrimary
                    , mBinding.pageBtn.getContext().getApplicationContext().getTheme()));

        }
    }
}
