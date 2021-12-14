package com.choco_tyranno.team_tree.ui.card_rv;

import android.view.View;

import androidx.annotation.NonNull;

import com.choco_tyranno.team_tree.databinding.ItemCardframeBinding;
import com.choco_tyranno.team_tree.databinding.ItemCardframeBindingImpl;
import com.choco_tyranno.team_tree.domain.card_data.CardDto;
import com.choco_tyranno.team_tree.ui.CardViewModel;
import com.choco_tyranno.team_tree.ui.DependentUIResolver;


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
