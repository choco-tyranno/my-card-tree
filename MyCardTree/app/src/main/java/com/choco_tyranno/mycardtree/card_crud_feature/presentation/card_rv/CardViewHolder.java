package com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

abstract class CardViewHolder extends RecyclerView.ViewHolder {
    public final static int CONTACT_CARD_TYPE = 100;
    public CardViewHolder(@NonNull View itemView) {
        super(itemView);
    }
    public void bind(Object object){};
}
