package com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv.CardAdapter;
import com.choco_tyranno.mycardtree.databinding.ItemCardcontainerBinding;

import java.util.List;
import java.util.Optional;


public class CardContainerViewHolder extends ContainerViewHolder {
    private final ItemCardcontainerBinding mBinding;

    public CardContainerViewHolder(@NonNull ItemCardcontainerBinding binding) {
        super(binding.getRoot());
        this.mBinding = binding;
        RecyclerView rv = mBinding.cardRecyclerview;
        CardAdapter cardAdapter = new CardAdapter(mBinding.getRoot().getContext());
        rv.setAdapter(cardAdapter);
        rv.setLayoutManager(new LinearLayoutManager(mBinding.getRoot().getContext(),LinearLayoutManager.HORIZONTAL,false));
    }

    public void bind(int containerPosition){
        RecyclerView rv = mBinding.cardRecyclerview;
        rv.setLayoutManager(null);
        rv.setLayoutManager(new LinearLayoutManager(mBinding.getRoot().getContext(),LinearLayoutManager.HORIZONTAL,false));
        boolean hasAdapter = Optional.ofNullable(((CardAdapter)rv.getAdapter())).isPresent();
        if (hasAdapter){
            CardAdapter cardAdapter = (CardAdapter)rv.getAdapter();
            cardAdapter.clear();
            cardAdapter.setContainerPosition(containerPosition);
            cardAdapter.notifyDataSetChanged();
        } else
            throw new RuntimeException("CardContainerViewHolder#bind/recyclerview has no adapter.");
        mBinding.setContainerNo(containerPosition+1);
    }
}
