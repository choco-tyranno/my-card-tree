package com.choco_tyranno.team_tree.presentation.card_rv;

import android.util.Log;

import androidx.annotation.NonNull;

import com.choco_tyranno.team_tree.R;
import com.choco_tyranno.team_tree.databinding.ItemCardFrameBinding;
import com.choco_tyranno.team_tree.databinding.ItemCardFrameBindingImpl;
import com.choco_tyranno.team_tree.domain.card_data.CardDto;
import com.choco_tyranno.team_tree.presentation.CardViewModel;
import com.choco_tyranno.team_tree.presentation.DisplayUtil;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.lang.reflect.Field;


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
        float switchRatio = Float.parseFloat(mBinding.getRoot().getContext().getResources().getString(R.string.card_switch_ratio));
        int cardFramePx = mBinding.cardFrame.getWidth();
        int switchPx = mBinding.cardFrontLayout.modeSwitch.getWidth();
        if (cardFramePx != 0) {
            float multipleValue = switchRatio*cardFramePx/switchPx;
            mBinding.cardFrontLayout.modeSwitch.setScaleX(multipleValue);
            mBinding.cardFrontLayout.modeSwitch.setScaleY(multipleValue);
        }
    }

    public ItemCardFrameBinding getBinding() {
        return mBinding;
    }

}
