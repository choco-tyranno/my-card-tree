package com.choco_tyranno.team_tree.presentation.searching_drawer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.team_tree.R;
import com.choco_tyranno.team_tree.databinding.ActivityMainBinding;
import com.choco_tyranno.team_tree.presentation.DependentUIResolver;
import com.choco_tyranno.team_tree.presentation.MainCardActivity;
import com.choco_tyranno.team_tree.presentation.main.DependentView;
import com.choco_tyranno.team_tree.presentation.main.TopAppBar;
import com.google.android.material.button.MaterialButton;

import java.util.Optional;

public class SearchResultPager extends MaterialButton implements DependentView {

    private int defaultModeInitializedCount = 0;

    public SearchResultPager(@NonNull Context context) {
        super(context);
        ready();
    }

    public SearchResultPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ready();
    }

    public SearchResultPager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ready();
    }

    @Override
    public void ready() {
        if (ready.get())
            return;
        SearchResultPager view = this;
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                view.ready.set(true);
                initAttributes();
                if (!attributeSettingActions.isEmpty()) {
                    while (!attributeSettingActions.isEmpty()) {
                        Runnable action = attributeSettingActions.poll();
                        Optional.ofNullable(action).ifPresent(Runnable::run);
                    }
                }
            }
        });
    }

    public void setWidthByPager1(@NonNull View pager1) {
        Runnable action = () -> {
            final ConstraintSet constraintSet = new ConstraintSet();
            ConstraintLayout parent = (ConstraintLayout) this.getParent();
            constraintSet.clone(parent);
            constraintSet.constrainWidth(this.getId(), 0);
            constraintSet.constrainWidth(this.getId(), pager1.getWidth());
            constraintSet.applyTo(parent);
        };
        postAttributeSettingAction(action);
    }

    @BindingAdapter("longPagerMode")
    public static void setLongPagerMode(SearchResultPager view, boolean longPagerOn) {
        if (view.getId() != R.id.searchResultPager_searchDrawer_pager1)
            return;
        if (longPagerOn)
            view.applyLongPagerMode(view);
        if (!longPagerOn)
            view.applyDefaultPagerMode(view);
    }

    private void applyDefaultPagerMode(View view) {
        if (defaultModeInitializedCount == 0) {
            defaultModeInitializedCount++;
            return;
        }
        ActivityMainBinding binding = ((MainCardActivity) view.getContext()).getMainBinding();
        MaterialButton pager3 = binding.layoutSearchdrawer.searchResultPagerSearchDrawerPager3;
        SearchResultPager pager4 = binding.layoutSearchdrawer.searchResultPagerSearchDrawerPager4;
        SearchResultPager pager5 = binding.layoutSearchdrawer.searchResultPagerSearchDrawerPager5;

        ConstraintLayout parent = (ConstraintLayout) view.getParent();
        final ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(parent);

        constraintSet.constrainWidth(pager4.getId(),0);
        constraintSet.constrainWidth(pager5.getId(),0);
        constraintSet.clear(pager3.getId(), ConstraintSet.END);
        constraintSet.clear(pager4.getId(), ConstraintSet.START);
        constraintSet.clear(pager4.getId(), ConstraintSet.TOP);
        constraintSet.clear(pager5.getId(), ConstraintSet.TOP);
        constraintSet.connect(pager4.getId(), ConstraintSet.TOP, binding.layoutSearchdrawer.cardSearchResultRecyclerview.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(pager5.getId(), ConstraintSet.TOP, binding.layoutSearchdrawer.cardSearchResultRecyclerview.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(pager3.getId(), ConstraintSet.END, pager4.getId(), ConstraintSet.START);
        constraintSet.connect(pager4.getId(), ConstraintSet.START, pager3.getId(), ConstraintSet.END);

        constraintSet.applyTo(parent);
    }

    private void applyLongPagerMode(View view) {
        ActivityMainBinding binding = ((MainCardActivity) view.getContext()).getMainBinding();
        SearchResultPager pager1 = binding.layoutSearchdrawer.searchResultPagerSearchDrawerPager1;
        MaterialButton pager3 = binding.layoutSearchdrawer.searchResultPagerSearchDrawerPager3;
        SearchResultPager pager4 = binding.layoutSearchdrawer.searchResultPagerSearchDrawerPager4;
        SearchResultPager pager5 = binding.layoutSearchdrawer.searchResultPagerSearchDrawerPager5;

        ConstraintLayout parent = (ConstraintLayout) view.getParent();

        final ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(parent);
        constraintSet.clear(pager3.getId(), ConstraintSet.END);
        constraintSet.clear(pager4.getId(), ConstraintSet.START);
        constraintSet.clear(pager4.getId(), ConstraintSet.TOP);
        constraintSet.clear(pager5.getId(), ConstraintSet.TOP);
        constraintSet.connect(pager3.getId(), ConstraintSet.END, binding.layoutSearchdrawer.nextPageBtn.getId(), ConstraintSet.START);
        constraintSet.connect(pager4.getId(), ConstraintSet.START, binding.layoutSearchdrawer.prevPageBtn.getId(), ConstraintSet.END);
        constraintSet.connect(pager4.getId(), ConstraintSet.TOP, pager1.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(pager5.getId(), ConstraintSet.TOP, pager1.getId(), ConstraintSet.BOTTOM);

        new DependentUIResolver.DependentUIResolverBuilder<View>().baseView(pager1)
                .with(pager1.getId()
                        , pager4::setWidthByPager1
                        , pager5::setWidthByPager1
                )
                .build().resolve();

        constraintSet.applyTo(parent);
    }

    private void initAttributes() {
        setDefaultOnClickListener();
    }

    private void setDefaultOnClickListener() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
