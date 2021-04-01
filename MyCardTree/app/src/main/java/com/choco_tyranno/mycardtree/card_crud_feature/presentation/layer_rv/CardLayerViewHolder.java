package com.choco_tyranno.mycardtree.card_crud_feature.presentation.layer_rv;

import android.view.View;

import androidx.annotation.NonNull;

import com.choco_tyranno.mycardtree.databinding.ItemCardFrameBinding;

import java.util.Arrays;


public class CardLayerViewHolder extends LayerViewHolder {

    ItemCardFrameBinding binding;

    public CardLayerViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = ItemCardFrameBinding.bind(itemView);
    }


}
