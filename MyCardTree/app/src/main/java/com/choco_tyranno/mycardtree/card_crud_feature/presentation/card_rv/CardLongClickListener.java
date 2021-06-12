package com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv;

import android.content.ClipData;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.R;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.ArrowPresenter;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv.CloneCardShadow;

import java.util.HashMap;
import java.util.Optional;


public class CardLongClickListener implements View.OnLongClickListener {
    private CardDTO cardDTO;
    //    private HashMap<Integer, Runnable> actions = new HashMap<>();
    public static final int ACTION_DRAG_PREPARE = 1;

    public CardLongClickListener() {
    }

    @Override
    public boolean onLongClick(View view) {
        if (!Optional.ofNullable(cardDTO).isPresent())
            throw new RuntimeException("CardLongClickListener#onLongClick - cardDTO is null");
        RecyclerView recyclerView = (RecyclerView) view.getParent().getParent().getParent();
//        consumeAction(ACTION_DRAG_PREPARE);
        ArrowPresenter.presentArrow(ArrowPresenter.CARD_RECYCLERVIEW, recyclerView);
        return view.startDragAndDrop(ClipData.newPlainText("", "")
                , new CloneCardShadow(CardViewShadowProvider.getInstance(view.getContext(), cardDTO))
                , "MOVE", 0);
    }

    public void setCard(CardDTO card) {
        cardDTO = card;
    }

//    public void setAction(int type, Runnable action){
//        actions.put(type, action);
//    }

//    private void consumeAction(int type){
//        if (actions.containsKey(type)){
//            actions.get(type).run();
//        }
//    }

}
