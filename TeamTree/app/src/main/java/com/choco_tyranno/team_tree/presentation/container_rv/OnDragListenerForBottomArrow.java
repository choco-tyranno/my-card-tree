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
import com.choco_tyranno.team_tree.presentation.card_rv.DragMoveDataContainer;

public class OnDragListenerForBottomArrow implements View.OnDragListener {
    @Override
    public boolean onDrag(View view, DragEvent event) {
        final int action = event.getAction();
        if (action == DragEvent.ACTION_DRAG_STARTED) {
            if (!(event.getLocalState() instanceof DragMoveDataContainer))
                return false;
            final String dragType = ((DragMoveDataContainer) event.getLocalState()).getDragType();
            final boolean moveDragEvent = TextUtils.equals(dragType, DragMoveDataContainer.DRAG_TYPE);
            if (moveDragEvent)
                return true;
        }
        ContainerRecyclerView containerRecyclerView = ((ViewGroup) view.getParent()).findViewById(R.id.containerRecyclerView_main_containers);
        ContainerRecyclerView.ItemScrollingControlLayoutManager containerLayoutManager = containerRecyclerView.getLayoutManager();
        if (containerLayoutManager == null)
            return false;
        ViewGroup viewGroup = (ViewGroup) containerRecyclerView.getParent();
        View prevContainerArrow = viewGroup.findViewById(R.id.imageView_main_topArrow);
        View nextContainerArrow = viewGroup.findViewById(R.id.imageView_main_bottomArrow);
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
            ((MainCardActivity) view.getContext()).getMainHandler().postDelayed(() ->view.setAlpha(0f), 400);
            return true;
        }
        return false;
    }
}
