package com.choco_tyranno.team_tree.presentation.container_rv;

import android.util.Pair;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;

import com.choco_tyranno.team_tree.Logger;
import com.choco_tyranno.team_tree.R;
import com.choco_tyranno.team_tree.presentation.MainCardActivity;

public class OnDragListenerForContainerRecyclerView implements View.OnDragListener {
    @Override
    public boolean onDrag(View v, DragEvent event) {
        ContainerRecyclerView containerRecyclerView = (ContainerRecyclerView) v;
        ContainerRecyclerView.ItemScrollingControlLayoutManager containerLayoutManager = containerRecyclerView.getLayoutManager();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                Logger.hotfixMessage("Container/ACTION_DRAG_STARTED");
                containerLayoutManager.onDragStart();
                ViewGroup viewGroup = (ViewGroup) containerRecyclerView.getParent();
                View prevContainerArrow = viewGroup.findViewById(R.id.prev_container_arrow);
                View nextContainerArrow = viewGroup.findViewById(R.id.next_container_arrow);
                final int containerCount = ((MainCardActivity) v.getContext()).getCardViewModel().presentContainerCount();
                final int firstVisibleContainerPosition = containerLayoutManager.findFirstCompletelyVisibleItemPosition();
                final int lastVisibleContainerPosition = containerLayoutManager.findLastCompletelyVisibleItemPosition();
                if (firstVisibleContainerPosition != 0)
                    prevContainerArrow.setAlpha(1f);
                if (lastVisibleContainerPosition + 1 != containerCount)
                    nextContainerArrow.setAlpha(1f);
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                Logger.hotfixMessage("Container/ACTION_DRAG_ENDED / result :" + event.getResult());
                if (!event.getResult()) {
                    if (!containerLayoutManager.isContainerRollbacked()) {
                        containerLayoutManager.setContainerRollbacked(true);
                        containerLayoutManager.onDragEndWithDropFail(
                                ((MainCardActivity) v.getContext()).getCardViewModel()
                                        .createRollbackAction((Pair) ((Pair) event.getLocalState()).second, (ContainerRecyclerView) v)
                        );
                    }
                }
                return true;
        }
        return false;
    }
}
