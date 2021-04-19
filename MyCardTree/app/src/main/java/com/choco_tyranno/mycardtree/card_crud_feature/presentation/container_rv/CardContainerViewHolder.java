package com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv;

import androidx.annotation.NonNull;

import com.choco_tyranno.mycardtree.databinding.ItemCardcontainerBinding;


public class CardContainerViewHolder extends ContainerViewHolder {

    private ItemCardcontainerBinding mBinding;

    public CardContainerViewHolder(@NonNull ItemCardcontainerBinding binding) {
        super(binding.getRoot());
        this.mBinding = binding;
    }

    public void bind(Integer containerNum){

    }

}
