package com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv;

import androidx.annotation.NonNull;

import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardState;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.CardViewModel;
import com.choco_tyranno.mycardtree.databinding.ItemCardFrameBinding;
import com.choco_tyranno.mycardtree.databinding.ItemCardFrameBindingImpl;

public class ContactCardViewHolder extends CardViewHolder {

    private final ItemCardFrameBinding mBinding;

    public ContactCardViewHolder(@NonNull ItemCardFrameBinding binding) {
        super(binding.getRoot());
        this.mBinding = binding;
    }

    @Override
    public void bind(CardViewModel viewModel, CardDTO cardDTO, CardState cardState) {
        mBinding.setViewModel(viewModel);
        mBinding.setCardState(cardState);
        mBinding.setCard(cardDTO);
        mBinding.setCardRootReference((ItemCardFrameBindingImpl) mBinding);
        mBinding.executePendingBindings();
    }

    public ItemCardFrameBinding getBinding(){
        return mBinding;
    }

}
