package com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.CardTreeViewModel;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.MainCardActivity;
import com.choco_tyranno.mycardtree.databinding.ItemCardFrameBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {
    private final CardTreeViewModel viewModel;
    private final LayoutInflater inflater;
    private int mContainerPosition;

    public CardAdapter(Context context) {
        this.viewModel = ((MainCardActivity) context).shareViewModel();
        inflater = ((MainCardActivity) context).getLayoutInflater();
        mContainerPosition = -1;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCardFrameBinding binding = ItemCardFrameBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ContactCardViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.bind(viewModel.getCardDTO(mContainerPosition, position));
    }

    @Override
    public int getItemCount() {
        return viewModel.getPresentCardCount(mContainerPosition);
    }

    public void clear() {
        this.mContainerPosition = -1;
    }

    public CardAdapter getInstance(){
        return CardAdapter.this;
    }

    public void setContainerPosition(int containerPosition){
        this.mContainerPosition = containerPosition;
    }
}
