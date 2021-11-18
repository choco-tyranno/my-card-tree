package com.choco_tyranno.team_tree.presentation.main;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.choco_tyranno.team_tree.R;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class BottomBar extends View {
    private boolean ready = false;
    final private Queue<Runnable> attributeSettingActions = new LinkedList<>();

    private String TAG = "@@HOTFIX";
    public BottomBar(Context context) {
        super(context);
        ready();
    }

    public BottomBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ready();
    }

    public BottomBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ready();
    }

    public BottomBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        ready();
    }


    public void ready() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d(TAG, "ready().onGlobalLayout()");
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                BottomBar.this.ready = true;
                Log.d(TAG, "ready = true");
                if (!attributeSettingActions.isEmpty()) {
                    Log.d(TAG, "ready().onGlobalLayout() !attributeSettingActions.isEmpty() -> Runnable::run");
                    while (!attributeSettingActions.isEmpty()) {
                        Runnable action = attributeSettingActions.poll();
                        Optional.ofNullable(action).ifPresent(Runnable::run);
                    }
                }
            }
        });
    }


    //Promise the param(Button newCardButton) view layout is ready.
    public void setHeightByNewCardButton(@NonNull Button newCardButton) {
        Runnable action = () -> {
            Log.d(TAG, "/ RUN / setHeightByNewCardButton() action.run");
            ConstraintSet constraintSet = new ConstraintSet();
            ConstraintLayout parent = (ConstraintLayout) this.getParent();
            constraintSet.clone(parent);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) newCardButton.getLayoutParams();
            final int newCardBottomMargin = params.bottomMargin;
            final int bottomBarHeight = newCardButton.getHeight() / 2 + newCardBottomMargin;
            constraintSet.constrainHeight(this.getId(), bottomBarHeight);
            constraintSet.applyTo(parent);
        };
        if (!ready) {
            Log.d(TAG, "setHeightByNewCardButton() !ready");
            attributeSettingActions.offer(action);
            return;
        }
        Log.d(TAG, "setHeightByNewCardButton() ready");
        action.run();
    }

}
