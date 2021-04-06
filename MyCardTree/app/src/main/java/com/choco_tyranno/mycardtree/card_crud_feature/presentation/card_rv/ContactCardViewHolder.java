package com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv;

import android.view.View;

import androidx.annotation.NonNull;

import com.choco_tyranno.mycardtree.card_crud_feature.data.card_data.Card;
import com.choco_tyranno.mycardtree.databinding.ItemCardFrameBinding;

public class ContactCardViewHolder extends CardViewHolder {

    private final ItemCardFrameBinding mBinding;

    public ContactCardViewHolder(@NonNull ItemCardFrameBinding binding) {
        super(binding.getRoot());
        this.mBinding = binding;
    }

    public void bind(Card card){
    }


}
