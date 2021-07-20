package com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv;

import androidx.annotation.NonNull;

import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardState;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.CardViewModel;
import com.choco_tyranno.mycardtree.databinding.ItemCardFrameBinding;
import com.choco_tyranno.mycardtree.databinding.ItemCardFrameBindingImpl;

public class ContactCardViewHolder extends CardViewHolder {
    private final ItemCardFrameBinding mBinding;

    public ContactCardViewHolder(@NonNull ItemCardFrameBinding binding, CardViewModel viewModel) {
        super(binding.getRoot());
        this.mBinding = binding;
        mBinding.setViewModel(viewModel);
        mBinding.setCardRootReference((ItemCardFrameBindingImpl) mBinding);
    }

    @Override
    public void bind(CardDTO cardDTO, CardState cardState, ObservableBitmap cardImage) {
        mBinding.setCardState(cardState);
        mBinding.setCard(cardDTO);
        mBinding.setCardImage(cardImage);
        mBinding.executePendingBindings();
    }

    public ItemCardFrameBinding getBinding(){
        return mBinding;
    }

}
