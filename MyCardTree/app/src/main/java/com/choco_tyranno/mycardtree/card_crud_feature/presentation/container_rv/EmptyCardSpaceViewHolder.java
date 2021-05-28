package com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv;

import android.view.View;

import androidx.annotation.NonNull;

import com.choco_tyranno.mycardtree.card_crud_feature.presentation.CardTreeViewModel;
import com.choco_tyranno.mycardtree.databinding.ItemCardEmptyBinding;

public class EmptyCardSpaceViewHolder extends ContainerViewHolder{
    private ItemCardEmptyBinding mBinding;

    public EmptyCardSpaceViewHolder(@NonNull ItemCardEmptyBinding binding) {
        super(binding.getRoot());
        mBinding = binding;
    }

    public void bind(CardTreeViewModel viewModel, int position){
        mBinding.setViewModel(viewModel);
    }
}
