package com.choco_tyranno.team_tree.presentation.card_rv;

import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.GestureDetectorCompat;


/*
 * Singleton [CardTouchListener, CardGestureListener, GestureDetectorCompat] instance management.
 * */
public class CardTouchListener implements View.OnTouchListener {
    private CardGestureListener cardGestureListener;
    private GestureDetectorCompat cardGestureDetectorCompat;

    public CardTouchListener() {

    }

    public void setCardGestureListener(CardGestureListener listener) {
        this.cardGestureListener = listener;
    }

    public void setCardGestureDetectorCompat(GestureDetectorCompat gestureDetector) {
        this.cardGestureDetectorCompat = gestureDetector;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!cardGestureListener.hasView() || cardGestureListener.getView() != v) {
                    cardGestureListener.setView(v);
                }
                break;
            case MotionEvent.ACTION_UP:
                v.performClick();
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        cardGestureDetectorCompat.onTouchEvent(event);
        return true;
    }

}
