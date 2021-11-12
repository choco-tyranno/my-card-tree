package com.choco_tyranno.team_tree.presentation.card_rv;

import android.graphics.Point;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;

import androidx.annotation.NonNull;

import com.choco_tyranno.team_tree.R;
import com.choco_tyranno.team_tree.databinding.ItemCardframeBinding;
import com.choco_tyranno.team_tree.databinding.ItemCardframeBindingImpl;
import com.choco_tyranno.team_tree.domain.card_data.CardDto;
import com.choco_tyranno.team_tree.presentation.CardViewModel;
import com.choco_tyranno.team_tree.presentation.DisplayUtil;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.lang.reflect.Field;


public class ContactCardViewHolder extends CardViewHolder {
    private final ItemCardframeBinding mBinding;

    public ContactCardViewHolder(@NonNull ItemCardframeBinding binding, CardViewModel viewModel) {
        super(binding.getRoot());
        this.mBinding = binding;
        mBinding.setViewModel(viewModel);
        mBinding.setCardRootReference((ItemCardframeBindingImpl) mBinding);
    }

    @Override
    public void bind(CardDto cardDTO, CardState cardState, ObservableBitmap cardImage) {
        mBinding.setCardState(cardState);
        mBinding.setCard(cardDTO);
        mBinding.setCardImage(cardImage);
        mBinding.executePendingBindings();
        setContentsViewsScale();
        setContentsViewsTextSize();
    }

    private void setContentsViewsScale(){
        float switchRatioToCardFrame = Float.parseFloat(mBinding.getRoot().getContext().getResources().getString(R.string.cardFront_cardSwitchRatioToCardFrame));
        int cardFramePx = mBinding.constraintLayoutMainCardFramePositioningManager.getWidth();
        int switchPx = mBinding.cardFrontLayout.modeSwitch.getWidth();
        if (cardFramePx != 0) {
            float multipleValue = switchRatioToCardFrame * cardFramePx / switchPx;
            mBinding.cardFrontLayout.modeSwitch.setScaleX(multipleValue);
            mBinding.cardFrontLayout.modeSwitch.setScaleY(multipleValue);
        }
    }

    private void setContentsViewsTextSize(){
        int autoSizedTextPx = (int) mBinding.cardFrontLayout.title.getTextSize();
        Log.d("@@HOTFIX","card - autoSizedTextPx:"+autoSizedTextPx);
        mBinding.cardFrontLayout.appCompatEditTextCardFrontTitleEditor.setTextSize(TypedValue.COMPLEX_UNIT_PX, autoSizedTextPx);
        mBinding.cardFrontLayout.appCompatEditTextCardFrontContactNumberEditor.setTextSize(TypedValue.COMPLEX_UNIT_PX, autoSizedTextPx);
    }

    public ItemCardframeBinding getBinding() {
        return mBinding;
    }

}
