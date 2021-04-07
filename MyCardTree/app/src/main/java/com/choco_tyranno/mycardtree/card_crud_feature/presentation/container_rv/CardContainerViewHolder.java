package com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv;

import androidx.annotation.NonNull;
import androidx.databinding.library.baseAdapters.BR;

import com.choco_tyranno.mycardtree.card_crud_feature.data.container_data.CardContainer;
import com.choco_tyranno.mycardtree.databinding.ItemCardcontainerBinding;


public class CardContainerViewHolder extends ContainerViewHolder {

    private ItemCardcontainerBinding mBinding;

    public CardContainerViewHolder(@NonNull ItemCardcontainerBinding binding) {
        super(binding.getRoot());
        this.mBinding = binding;
    }

    public void bind(CardContainer container){
        mBinding.setVariable(BR.container, container);
        mBinding.executePendingBindings();
    }


}
