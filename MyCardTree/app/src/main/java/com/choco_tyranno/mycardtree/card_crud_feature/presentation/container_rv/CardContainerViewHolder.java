package com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardAdapter;
import com.choco_tyranno.mycardtree.databinding.ItemCardcontainerBinding;

import java.util.List;


public class CardContainerViewHolder extends ContainerViewHolder {
    private final ItemCardcontainerBinding mBinding;

    public CardContainerViewHolder(@NonNull ItemCardcontainerBinding binding) {
        super(binding.getRoot());
        this.mBinding = binding;
        RecyclerView rv = mBinding.cardRecyclerview;
        rv.setAdapter(new CardAdapter(mBinding.getRoot().getContext()));
        rv.setLayoutManager(new LinearLayoutManager(mBinding.getRoot().getContext(),LinearLayoutManager.HORIZONTAL,false));
    }

    public void bind(int containerNum, List<CardDTO> data){
        RecyclerView rv = mBinding.cardRecyclerview;
        rv.setLayoutManager(null);
        rv.setLayoutManager(new LinearLayoutManager(mBinding.getRoot().getContext(),LinearLayoutManager.HORIZONTAL,false));
        ((CardAdapter)rv.getAdapter()).clear();

        mBinding.setContainerNo(containerNum);
        ((CardAdapter)mBinding.cardRecyclerview.getAdapter()).submitList(data);
    }

}
