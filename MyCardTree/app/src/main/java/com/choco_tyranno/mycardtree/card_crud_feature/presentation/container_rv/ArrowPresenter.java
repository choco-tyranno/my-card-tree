package com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.R;
import com.choco_tyranno.mycardtree.card_crud_feature.Logger;

import java.util.Queue;

public class ArrowPresenter {
    public static final int CARD_RECYCLERVIEW = 0;
    public static final int CONTAINER_RECYCLERVIEW = 1;

    public static final int NO_ARROW_NEEDED = -1;
    public static final int TWO_WAY_ARROW_NEEDED = 0;
    public static final int ONLY_PREVIOUS_ARROW_NEEDED = 1;
    public static final int ONLY_NEXT_ARROW_NEEDED = 2;

    public static void fadeInArrowsIfNecessary(int type, RecyclerView recyclerView, @Nullable Container container) {
        Logger.message("#presentArrow");
        if (type != CARD_RECYCLERVIEW && type != CONTAINER_RECYCLERVIEW)
            return;
        if (type == CARD_RECYCLERVIEW) {
            handleCardRecyclerViewCase(recyclerView, container);
            return;
        }
        if (type == CONTAINER_RECYCLERVIEW)
            handleContainerRecyclerViewCase(recyclerView);
    }

    public static void fadeOutArrowsIfNecessary(RecyclerView recyclerView, @Nullable Container container){
        ConstraintLayout containerLayout = ((ConstraintLayout) recyclerView.getParent());
        if (container!=null){
            Queue<View> arrowStorage = container.getArrowStorage();
            while (!arrowStorage.isEmpty()){
                View arrow = arrowStorage.poll();
                containerLayout.removeView(arrow);
            }
        }
    }

    private static void handleCardRecyclerViewCase(RecyclerView recyclerView, Container container) {
        Logger.message("#handleCardRecyclerViewCase");
        int direction = testPresentNecessaryDirection(recyclerView);
        Logger.message("#handleCardRecyclerViewCase - direction"+direction);
        ConstraintLayout containerLayout = ((ConstraintLayout) recyclerView.getParent());
        switch (direction) {
            case NO_ARROW_NEEDED:
                break;
            case TWO_WAY_ARROW_NEEDED:
                Logger.message("#handleCardRecyclerViewCase - TWO_WAY_ARROW_NEEDED");
                showLeftArrow(containerLayout, recyclerView, container);
                showRightArrow(containerLayout, recyclerView, container);
                break;
            case ONLY_PREVIOUS_ARROW_NEEDED:
                Logger.message("#handleCardRecyclerViewCase - ONLY_PREVIOUS_ARROW_NEEDED");
                showLeftArrow(containerLayout, recyclerView, container);
                break;
            case ONLY_NEXT_ARROW_NEEDED:
                Logger.message("#handleCardRecyclerViewCase - ONLY_NEXT_ARROW_NEEDED");
                showRightArrow(containerLayout, recyclerView, container);
                break;
        }
    }

    private static void handleContainerRecyclerViewCase(RecyclerView recyclerView) {
        Logger.message("#handleContainerRecyclerViewCase");
        int directionArr = testPresentNecessaryDirection(recyclerView);
        ConstraintLayout containerLayout = ((ConstraintLayout) recyclerView.getParent());
        switch (directionArr) {
            case TWO_WAY_ARROW_NEEDED:
                showTopArrow(containerLayout, recyclerView);
                showBottomArrow(containerLayout, recyclerView);
                break;
            case ONLY_PREVIOUS_ARROW_NEEDED:
                showTopArrow(containerLayout, recyclerView);
                break;
            case ONLY_NEXT_ARROW_NEEDED:
                showBottomArrow(containerLayout, recyclerView);
                break;
        }
    }

    //needOperationFlag[0] : left || top.
    //needOperationFlag[1] : right || bottom.
    private static int testPresentNecessaryDirection(RecyclerView recyclerView) {
        Logger.message("#testPresentNecessaryDirection");
        int firstVisibleItemPos = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        int lastVisibleItemPos = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
        int containerItemCount = recyclerView.getAdapter().getItemCount();
        if (containerItemCount == 0)
            throw new RuntimeException("AdapterItemCount is zero. But ArrowPresenter is connected.");
        boolean[] needOperationFlag = {false, false};
        if (firstVisibleItemPos > 0)
            needOperationFlag[0] = true;
        if (containerItemCount - 1 > lastVisibleItemPos)
            needOperationFlag[1] = true;
        if (firstVisibleItemPos!=lastVisibleItemPos){
            needOperationFlag[0] = true;
            needOperationFlag[1] = true;
        }
        int result = NO_ARROW_NEEDED;
        if (needOperationFlag[0] && needOperationFlag[1])
            result = TWO_WAY_ARROW_NEEDED;
        if (needOperationFlag[0] && !needOperationFlag[1])
            result = ONLY_PREVIOUS_ARROW_NEEDED;
        if (!needOperationFlag[0] && needOperationFlag[1])
            result = ONLY_NEXT_ARROW_NEEDED;
        Logger.message("#testPresentNecessaryDirection  - result : "+result);
        return result;
    }

    private static void showLeftArrow(ConstraintLayout containerLayout, RecyclerView recyclerView, Container container) {
        ImageView imageView = new ImageView(recyclerView.getContext());
        imageView.setImageResource(R.drawable.ic_baseline_arrow_back_ios_new_24);
        imageView.setContentDescription(recyclerView.getContext().getResources().getString(R.string.prev_arrow_desc));
        imageView.setAdjustViewBounds(true);
        imageView.setId(View.generateViewId());
        ((Activity)recyclerView.getContext()).runOnUiThread(()->{
            containerLayout.addView(imageView);
            ConstraintSet set = new ConstraintSet();
            set.clone(containerLayout);
            set.connect(imageView.getId()
                    , ConstraintSet.LEFT
                    , R.id.card_recyclerview
                    , ConstraintSet.LEFT);
            set.connect(imageView.getId()
                    , ConstraintSet.TOP
                    , R.id.card_recyclerview
                    , ConstraintSet.TOP);
            set.connect(imageView.getId()
                    , ConstraintSet.BOTTOM
                    , R.id.card_recyclerview
                    , ConstraintSet.BOTTOM);
            set.applyTo(containerLayout);
        });
        container.enqueueArrowView(imageView);
    }

    private static void showRightArrow(ConstraintLayout containerLayout, RecyclerView recyclerView, Container container) {
        ImageView imageView = new ImageView(recyclerView.getContext());
        imageView.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24);
        imageView.setContentDescription(recyclerView.getContext().getResources().getString(R.string.next_arrow_desc));
        imageView.setAdjustViewBounds(true);
        imageView.setId(View.generateViewId());
        ((Activity)recyclerView.getContext()).runOnUiThread(()->{
            containerLayout.addView(imageView);
            ConstraintSet set = new ConstraintSet();
            set.clone(containerLayout);
            set.connect(imageView.getId()
                    , ConstraintSet.RIGHT
                    , R.id.card_recyclerview
                    , ConstraintSet.RIGHT);
            set.connect(imageView.getId()
                    , ConstraintSet.TOP
                    , R.id.card_recyclerview
                    , ConstraintSet.TOP);
            set.connect(imageView.getId()
                    , ConstraintSet.BOTTOM
                    , R.id.card_recyclerview
                    , ConstraintSet.BOTTOM);
            set.applyTo(containerLayout);
        });
        container.enqueueArrowView(imageView);
    }

    private static void showTopArrow(ConstraintLayout containerLayout, RecyclerView recyclerView) {
    }
    private static void showBottomArrow(ConstraintLayout containerLayout, RecyclerView recyclerView) {
    }
}
