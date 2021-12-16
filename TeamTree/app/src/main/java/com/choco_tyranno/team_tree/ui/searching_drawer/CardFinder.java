package com.choco_tyranno.team_tree.ui.searching_drawer;

import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.choco_tyranno.team_tree.R;
import com.choco_tyranno.team_tree.databinding.ActivityMainBinding;
import com.choco_tyranno.team_tree.ui.main.MainCardActivity;

import java.util.Optional;


public class CardFinder {
    private boolean findCardRequested = false;
    Animation flyingToRightAnimation;
    Runnable finishAction;

    public CardFinder(Context context) {
        flyingToRightAnimation = AnimationUtils.loadAnimation(context, R.anim.search_page_sending_card);
        flyingToRightAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                findCardRequested = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                MainCardActivity mainCardActivity = (MainCardActivity) context;
                ActivityMainBinding binding = mainCardActivity.getBinding();
                DrawerLayout MainDL = binding.drawerLayoutMainSearchDrawer;
                MainDL.closeDrawer(GravityCompat.END);
                SearchView searchView = binding.layoutSearchdrawer.cardSearchView;
                searchView.setQuery("",false);
                searchView.setIconified(true);
                Optional.ofNullable(finishAction).ifPresent(Runnable::run);
                finishAction = null;
                findCardRequested = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    public void animate(View selectingAssistView){
        ViewGroup parentView = (ViewGroup) selectingAssistView.getParent();
        View selectingView = parentView.findViewById(R.id.view_searchResultItem_finderShapeHelper);
        //back to origin visibility / color needed.
        selectingView.getBackground().setColorFilter(new BlendModeColorFilter(selectingView.getResources().getColor(R.color.colorSubPrimary_a
                , selectingView.getContext().getTheme()), BlendMode.SRC_ATOP));
        View planeImageView = parentView.findViewById(R.id.imageButton_searchResultItem_paperAirplane);
        finishAction = ()->{
            selectingView.getBackground().setColorFilter(new BlendModeColorFilter(selectingView.getResources().getColor(R.color.colorAccent_c
                    , selectingView.getContext().getTheme()), BlendMode.SRC_ATOP));
            planeImageView.setVisibility(View.INVISIBLE);
        };
        planeImageView.setVisibility(View.VISIBLE);
        planeImageView.startAnimation(flyingToRightAnimation);
    }

    public boolean isFindCardRequested(){
        return findCardRequested;
    }

    public void setFindCardRequested(boolean flag){
        this.findCardRequested = flag;
    }

}
