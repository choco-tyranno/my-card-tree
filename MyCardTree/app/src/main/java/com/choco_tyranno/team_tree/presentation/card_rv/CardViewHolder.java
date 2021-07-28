package com.choco_tyranno.team_tree.presentation.card_rv;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.team_tree.domain.card_data.CardDto;

public abstract class CardViewHolder extends RecyclerView.ViewHolder {
    public final static int CONTACT_CARD_TYPE = 100;
    public CardViewHolder(@NonNull View itemView) {
        super(itemView);
    }
    public abstract void bind(CardDto data, CardState cardState, ObservableBitmap cardImage);
}
