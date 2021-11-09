package com.choco_tyranno.team_tree.presentation.container_rv;

import androidx.annotation.NonNull;

import com.choco_tyranno.team_tree.databinding.ItemEmptycontainerBinding;
import com.choco_tyranno.team_tree.presentation.CardViewModel;


public class EmptyCardSpaceViewHolder extends ContainerViewHolder {
    private ItemEmptycontainerBinding mBinding;
    private CardViewModel mViewModel;

    public EmptyCardSpaceViewHolder(@NonNull ItemEmptycontainerBinding binding, CardViewModel viewModel) {
        super(binding.getRoot());
        this.mBinding = binding;
        this.mViewModel = viewModel;
        mBinding.setViewModel(viewModel);
    }

    public void bind(int position) {
    }
}
