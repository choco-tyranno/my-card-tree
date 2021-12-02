package com.choco_tyranno.team_tree.presentation.container_rv;

import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;

import com.choco_tyranno.team_tree.Logger;
import com.choco_tyranno.team_tree.R;
import com.choco_tyranno.team_tree.presentation.CardViewModel;
import com.choco_tyranno.team_tree.presentation.MainCardActivity;
import com.choco_tyranno.team_tree.presentation.card_rv.DragMoveDataContainer;

public class OnDragListenerForContainerRecyclerView implements View.OnDragListener {
    @Override
    public boolean onDrag(View v, DragEvent event) {
        if (v.getId()!=R.id.containerRecyclerView_mainBody_containers)
            return false;
        ContainerRecyclerView containerRecyclerView = (ContainerRecyclerView) v;
        ContainerRecyclerView.ItemScrollingControlLayoutManager containerLayoutManager = containerRecyclerView.getLayoutManager();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                String dragType = null;
                if (event.getLocalState() instanceof Pair)
                    dragType = (String) ((Pair) event.getLocalState()).first;
                if (event.getLocalState() instanceof DragMoveDataContainer)
                    dragType = ((DragMoveDataContainer) event.getLocalState()).getDragType();
                if (dragType==null)
                    return false;
                final boolean moveDragEvent = TextUtils.equals(dragType, DragMoveDataContainer.DRAG_TYPE);
                if (!moveDragEvent)
                    return false;
                containerLayoutManager.onDragStart();
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                if (!event.getResult()) {
                    CardViewModel viewModel = ((MainCardActivity) v.getContext()).getCardViewModel();
                    containerLayoutManager.onDragEndWithDropFail(
                            viewModel.createCardMovingRollbackAction((DragMoveDataContainer) event.getLocalState()
                                    , (ContainerRecyclerView) v)
                    );
                }
                return true;
        }
        return false;
    }
}
