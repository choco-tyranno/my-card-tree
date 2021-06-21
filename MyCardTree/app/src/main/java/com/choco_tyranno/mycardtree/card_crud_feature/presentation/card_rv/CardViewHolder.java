package com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardState;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.CardViewModel;

public abstract class CardViewHolder extends RecyclerView.ViewHolder {
    public final static int CONTACT_CARD_TYPE = 100;
    public CardViewHolder(@NonNull View itemView) {
        super(itemView);
    }
    public abstract void bind(CardViewModel viewModel, CardDTO data, CardState cardState);
}
