package com.choco_tyranno.team_tree.presentation.card_rv;

import android.graphics.Point;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;

import androidx.annotation.NonNull;

import com.choco_tyranno.team_tree.R;
import com.choco_tyranno.team_tree.databinding.ItemCardframeBinding;
import com.choco_tyranno.team_tree.databinding.ItemCardframeBindingImpl;
import com.choco_tyranno.team_tree.domain.card_data.CardDto;
import com.choco_tyranno.team_tree.presentation.CardViewModel;
import com.choco_tyranno.team_tree.presentation.DependentUIResolver;
import com.choco_tyranno.team_tree.presentation.DisplayUtil;
import com.choco_tyranno.team_tree.presentation.main.TopAppBar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.lang.reflect.Field;


public class ContactCardViewHolder extends CardViewHolder {
    private final ItemCardframeBinding mBinding;

    public ContactCardViewHolder(@NonNull ItemCardframeBinding binding, CardViewModel viewModel) {
        super(binding.getRoot());
        this.mBinding = binding;
        mBinding.setViewModel(viewModel);
        mBinding.setCardRootReference((ItemCardframeBindingImpl) mBinding);
        initContentsView();
    }

    private void initContentsView() {
        View cardFrame = mBinding.constraintLayoutMainCardFramePositioningManager;
        new DependentUIResolver.DependentUIResolverBuilder<View>()
                .baseView(cardFrame)
                .with(cardFrame.getId(),
                        mBinding.cardFrontLayout.cardModeSwitchCardFrontCardModeSwitch::setScaleByCardFrame
                ).build()
                .resolve();

        View title = mBinding.cardFrontLayout.title;
        new DependentUIResolver.DependentUIResolverBuilder<View>()
                .baseView(title)
                .with(title.getId(),
                    mBinding.cardFrontLayout.cardTitleEditorCardFrontTitleEditor::setTextSizeByTitle,
                    mBinding.cardFrontLayout.cardContactNumberEditorCardFrontContactNumberEditor::setTextSizeByTitle
                ).build()
                .resolve();
    }

    @Override
    public void bind(CardDto cardDTO, CardState cardState, ObservableBitmap cardImage) {
        mBinding.setCardState(cardState);
        mBinding.setCard(cardDTO);
        mBinding.setCardImage(cardImage);
        mBinding.executePendingBindings();
    }

    public ItemCardframeBinding getBinding() {
        return mBinding;
    }

}
