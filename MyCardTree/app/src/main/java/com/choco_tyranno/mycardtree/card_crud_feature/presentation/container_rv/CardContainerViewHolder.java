package com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardRecyclerView;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.CardViewModel;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardAdapter;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardScrollListener;
import com.choco_tyranno.mycardtree.databinding.ItemCardcontainerBinding;

import java.util.Objects;
import java.util.Optional;


public class CardContainerViewHolder extends ContainerViewHolder {
    private final ItemCardcontainerBinding mBinding;
    private final CardViewModel mViewModel;

    public CardContainerViewHolder(@NonNull ItemCardcontainerBinding binding, CardViewModel viewModel) {
        super(binding.getRoot());
        Logger.message("contVH#constructor");
        this.mBinding = binding;
        this.mViewModel = viewModel;
        this.mBinding.setViewModel(viewModel);
        CardRecyclerView rv = mBinding.cardRecyclerview;
        rv.setAdapter(new CardAdapter(mBinding.getRoot().getContext()));
    }

    public CardRecyclerView.ScrollControllableLayoutManager createLayoutManager() {
        return new CardRecyclerView.ScrollControllableLayoutManager(mBinding.getRoot().getContext(), LinearLayoutManager.HORIZONTAL, false);
    }

    public void bind(int containerPosition) {
        Logger.message("contVH#bind");
        CardRecyclerView rv = mBinding.cardRecyclerview;
        CardAdapter cardAdapter = (CardAdapter) rv.getAdapter();
        if (cardAdapter==null)
            return;
        rv.suppressLayout();
        rv.clearOnScrollListeners();
        cardAdapter.clear();
        rv.clearLayoutManager();
        rv.setLayoutManager(createLayoutManager());
        cardAdapter.initialize(containerPosition);

        Container targetContainer = mViewModel.getContainer(containerPosition);

        CardScrollListener scrollListener = targetContainer.getCardScrollListener();
        scrollListener.initialize(
                rv.getLayoutManager()
                , mViewModel.getOnFocusChangedListener()
                , mViewModel.getOnScrollStateChangeListener()
                , containerPosition);
        rv.addOnScrollListener(scrollListener);

        mBinding.prevCardArrow.setVisibility(View.INVISIBLE);
        mBinding.nextCardArrow.setVisibility(View.INVISIBLE);

        mBinding.setContainerNo(containerPosition + 1);
        mBinding.setContainer(targetContainer);

        rv.unsuppressLayout();
        if (targetContainer.hasSavedState()) {
            Objects.requireNonNull(rv.getLayoutManager()).onRestoreInstanceState(targetContainer.getSavedScrollState());
        }
        cardAdapter.notifyDataSetChanged();
    }

    public ItemCardcontainerBinding getBinding() {
        Logger.message("contVH#getBinding");
        return mBinding;
    }
}
