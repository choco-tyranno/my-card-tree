package com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import androidx.databinding.BaseObservable;

import com.choco_tyranno.mycardtree.R;
import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.CardViewModel;
import com.choco_tyranno.mycardtree.databinding.ItemCardFrameBinding;
import com.google.android.material.card.MaterialCardView;

public class CardGestureListener extends GestureDetector.SimpleOnGestureListener {
    private View view;

    @Override
    public void onLongPress(MotionEvent e) {
        Logger.hotfixMessage("onLongPress");
        super.onLongPress(e);
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Logger.hotfixMessage("CardGestureListener#onDoubleTaped");
        if (view == null) {
            return false;
        }
        FrameLayout frameLayout = (FrameLayout) view.getParent().getParent();
        CardRecyclerView cardRecyclerView = (CardRecyclerView) frameLayout.getParent();
        ContactCardViewHolder cardViewHolder = (ContactCardViewHolder) cardRecyclerView.getChildViewHolder(frameLayout);
        ItemCardFrameBinding binding = cardViewHolder.getBinding();
        MaterialCardView frontCardView = binding.cardFrontLayout.frontCardCardView;
        MaterialCardView backCardView = binding.cardBackLayout.backCardCardView;
        CardState cardState = binding.getCardState();
        flippingCard(frontCardView, backCardView, cardState);
        clearView();
        return true;
    }

    public boolean hasView() {
        return view != null;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void clearView() {
        this.view = null;
    }

    private void flippingCard(View frontCardView, View backCardView, CardState cardState) {
        Context context = frontCardView.getContext();
        final boolean startWithFlipped = cardState.isFlipped();
        View startCardFace;
        View finishCardFace;
        if (startWithFlipped) {
            startCardFace = backCardView;
            finishCardFace = frontCardView;
        } else {
            startCardFace = frontCardView;
            finishCardFace = backCardView;
        }
        startCardFace.animate().rotationX(90f).alpha(0f).
                setInterpolator(AnimationUtils.loadInterpolator(context, android.R.anim.decelerate_interpolator)).
                setDuration(200).withEndAction(() -> {
            startCardFace.setAlpha(0f);
            startCardFace.setRotationX(0f);
            startCardFace.setVisibility(View.INVISIBLE);
            finishCardFace.setAlpha(0f);
            finishCardFace.setRotationX(-90f);
            finishCardFace.setVisibility(View.VISIBLE);
            if (startWithFlipped) {
                cardState.displayFront();
            } else {
                cardState.displayBack();
            }
            finishCardFace.animate().rotationX(0f).
                    setInterpolator(AnimationUtils.loadInterpolator(context, android.R.anim.decelerate_interpolator)).
                    alpha(1f).setDuration(200).start();
        }).start();
    }
}
