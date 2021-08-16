package com.choco_tyranno.team_tree.presentation.container_rv;

import android.text.TextUtils;
import android.util.Pair;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;

import com.choco_tyranno.team_tree.Logger;
import com.choco_tyranno.team_tree.R;
import com.choco_tyranno.team_tree.presentation.CardViewModel;
import com.choco_tyranno.team_tree.presentation.MainCardActivity;

public class OnDragListenerForBottomArrow implements View.OnDragListener {
    @Override
    public boolean onDrag(View view, DragEvent event) {

        final int action = event.getAction();
        if (action == DragEvent.ACTION_DRAG_STARTED) {
            Logger.hotfixMessage("Bottom start");
            final String dragType = ((String) ((Pair) event.getLocalState()).first);
            final boolean moveDragEvent = TextUtils.equals(dragType, "MOVE");
            if (moveDragEvent)
                return true;
        }

        ContainerRecyclerView containerRecyclerView = ((ViewGroup) view.getParent().getParent()).findViewById(R.id.main_body);
        ContainerRecyclerView.ItemScrollingControlLayoutManager containerLayoutManager = containerRecyclerView.getLayoutManager();
        if (containerLayoutManager == null)
            return false;
        ViewGroup viewGroup = (ViewGroup) containerRecyclerView.getParent();
        View prevContainerArrow = viewGroup.findViewById(R.id.prev_container_arrow);
        View nextContainerArrow = viewGroup.findViewById(R.id.next_container_arrow);
        CardViewModel viewModel = ((MainCardActivity) view.getContext()).getCardViewModel();

        if (action == DragEvent.ACTION_DRAG_LOCATION) {
            if (containerLayoutManager.hasScrollAction()) {
                return false;
            }

            final int lastCompletelyVisibleContainerPosition = containerLayoutManager.findLastCompletelyVisibleItemPosition();
            if (lastCompletelyVisibleContainerPosition < 0)
                return false;
            final int containerCount = viewModel.presentContainerCount();
            if (lastCompletelyVisibleContainerPosition != containerCount - 1) {
                containerLayoutManager.setContainerScrollAction(() -> {
                    containerRecyclerView.smoothScrollToPosition(lastCompletelyVisibleContainerPosition + 1);
                    if (lastCompletelyVisibleContainerPosition + 1 + 1 == containerCount) {
                        view.setAlpha(0f);
                    }
                    prevContainerArrow.setAlpha(1f);
                });
                containerLayoutManager.scrollDelayed(100);
                return true;
            } else
                return false;
        }

        if (action == DragEvent.ACTION_DRAG_ENDED) {
            if (containerLayoutManager.hasScrollAction()) {
                containerLayoutManager.setExitAction(() -> {
                    prevContainerArrow.setAlpha(0f);
                    nextContainerArrow.setAlpha(0f);
                });
                return true;
            }
            prevContainerArrow.setAlpha(0f);
            nextContainerArrow.setAlpha(0f);
            return true;
        }
        return false;
    }
}
