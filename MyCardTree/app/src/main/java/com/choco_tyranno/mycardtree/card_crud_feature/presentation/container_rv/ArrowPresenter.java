package com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

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

    public static void presentArrow(int type, RecyclerView recyclerView) {
        Logger.hotfixMessage("#presentArrow");
        if (type != CARD_RECYCLERVIEW && type != CONTAINER_RECYCLERVIEW)
            return;
        if (type == CARD_RECYCLERVIEW) {
            handleCardRecyclerViewCase(recyclerView);
            return;
        }
        if (type == CONTAINER_RECYCLERVIEW)
            handleContainerRecyclerViewCase(recyclerView);
    }

    private static void handleCardRecyclerViewCase(RecyclerView recyclerView) {
        Logger.hotfixMessage("#handleCardRecyclerViewCase");
        int direction = testPresentNecessaryDirection(recyclerView);
        Logger.hotfixMessage("#handleCardRecyclerViewCase - direction"+direction);
        ConstraintLayout containerLayout = ((ConstraintLayout) recyclerView.getParent());
        switch (direction) {
            case NO_ARROW_NEEDED:
                break;
            case TWO_WAY_ARROW_NEEDED:
                Logger.hotfixMessage("#handleCardRecyclerViewCase - TWO_WAY_ARROW_NEEDED");
                showLeftArrow(containerLayout, recyclerView);
                showRightArrow(containerLayout, recyclerView);
                break;
            case ONLY_PREVIOUS_ARROW_NEEDED:
                Logger.hotfixMessage("#handleCardRecyclerViewCase - ONLY_PREVIOUS_ARROW_NEEDED");
                showLeftArrow(containerLayout, recyclerView);
                break;
            case ONLY_NEXT_ARROW_NEEDED:
                Logger.hotfixMessage("#handleCardRecyclerViewCase - ONLY_NEXT_ARROW_NEEDED");
                showRightArrow(containerLayout, recyclerView);
                break;
        }
    }

    private static void handleContainerRecyclerViewCase(RecyclerView recyclerView) {
        Logger.hotfixMessage("#handleContainerRecyclerViewCase");
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
        Logger.hotfixMessage("#testPresentNecessaryDirection");
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
        Logger.hotfixMessage("#testPresentNecessaryDirection  - result : "+result);
        return result;
    }

    private static void showLeftArrow(ConstraintLayout containerLayout, RecyclerView recyclerView) {
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
    }

    private static void showRightArrow(ConstraintLayout containerLayout, RecyclerView recyclerView) {
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
    }

    private static void showTopArrow(ConstraintLayout containerLayout, RecyclerView recyclerView) {
    }

    private static void showBottomArrow(ConstraintLayout containerLayout, RecyclerView recyclerView) {
    }
}
