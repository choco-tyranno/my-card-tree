package com.choco_tyranno.team_tree.presentation.card_rv;

import androidx.annotation.NonNull;

import com.choco_tyranno.team_tree.databinding.ItemCardFrameBinding;
import com.choco_tyranno.team_tree.databinding.ItemCardFrameBindingImpl;
import com.choco_tyranno.team_tree.domain.card_data.CardDto;
import com.choco_tyranno.team_tree.presentation.CardViewModel;


public class ContactCardViewHolder extends CardViewHolder {
    private final ItemCardFrameBinding mBinding;

    public ContactCardViewHolder(@NonNull ItemCardFrameBinding binding, CardViewModel viewModel) {
        super(binding.getRoot());
        this.mBinding = binding;
        mBinding.setViewModel(viewModel);
        mBinding.setCardRootReference((ItemCardFrameBindingImpl) mBinding);
    }

    @Override
    public void bind(CardDto cardDTO, CardState cardState, ObservableBitmap cardImage) {
        mBinding.setCardState(cardState);
        mBinding.setCard(cardDTO);
        mBinding.setCardImage(cardImage);
        mBinding.executePendingBindings();
    }

    public ItemCardFrameBinding getBinding() {
        return mBinding;
    }

}
