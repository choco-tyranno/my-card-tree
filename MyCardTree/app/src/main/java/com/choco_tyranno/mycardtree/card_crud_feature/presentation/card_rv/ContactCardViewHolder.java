package com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;

import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.Card;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.databinding.ItemCardFrameBinding;

public class ContactCardViewHolder extends CardViewHolder {

    private final ItemCardFrameBinding mBinding;

    public ContactCardViewHolder(@NonNull ItemCardFrameBinding binding) {
        super(binding.getRoot());
        this.mBinding = binding;
    }

    public void bind(CardDTO data){
        mBinding.setData(data);
    }

}
