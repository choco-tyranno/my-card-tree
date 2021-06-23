package com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv;

import androidx.annotation.NonNull;

import com.choco_tyranno.mycardtree.card_crud_feature.presentation.CardViewModel;
import com.choco_tyranno.mycardtree.databinding.ItemCardEmptyBinding;

public class EmptyCardSpaceViewHolder extends ContainerViewHolder {
    private ItemCardEmptyBinding mBinding;
    private CardViewModel mViewModel;

    public EmptyCardSpaceViewHolder(@NonNull ItemCardEmptyBinding binding, CardViewModel viewModel) {
        super(binding.getRoot());
        this.mBinding = binding;
        this.mViewModel = viewModel;
        mBinding.setViewModel(viewModel);
    }

    public void bind(int position) {
    }
}
