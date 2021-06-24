package com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv;

import android.content.ClipData;
import android.view.View;

import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.CloneCardShadow;

import java.util.Optional;


public class CardLongClickListener implements View.OnLongClickListener {
    private CardDTO cardDTO;

    public CardLongClickListener() {
    }

    @Override
    public boolean onLongClick(View view) {
        if (!Optional.ofNullable(cardDTO).isPresent())
            throw new RuntimeException("CardLongClickListener#onLongClick - cardDTO is null");
        return view.startDragAndDrop(ClipData.newPlainText("", "")
                , new CloneCardShadow(CardViewShadowProvider.getInstance(view.getContext(), cardDTO))
                , "MOVE", 0);
    }

    public void setCard(CardDTO card) {
        cardDTO = card;
    }


}
