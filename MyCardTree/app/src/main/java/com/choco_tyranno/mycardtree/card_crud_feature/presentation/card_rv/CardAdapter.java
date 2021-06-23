package com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.CardViewModel;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.MainCardActivity;
import com.choco_tyranno.mycardtree.databinding.ItemCardFrameBinding;

/**
 *  It is recommended that CardAdapter instance be recycled. Use #clear().
 *
 * */
public class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {
    private final CardViewModel viewModel;
    private int mContainerPosition;

    public CardAdapter(Context context) {
        Logger.message("cardAdapter#constructor");
        this.viewModel = ((MainCardActivity) context).getCardViewModel();
        mContainerPosition = -1;
    }

    public void initialize(int containerPosition){
        this.mContainerPosition = containerPosition;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Logger.message("cardAdapter/onCreateVH");
        ItemCardFrameBinding binding = ItemCardFrameBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ContactCardViewHolder(binding, viewModel);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int cardPosition) {
        Logger.message("cardAdapter/onBindVH");
        holder.bind(viewModel.getCardDTO(mContainerPosition, cardPosition), viewModel.getCardState(mContainerPosition, cardPosition));
    }

    @Override
    public int getItemCount() {
        Logger.message("cardAdapter#getItemCount");
        if (mContainerPosition == -1) {
            Logger.message("container pos -1 detected.");
            return 0;
        }
//        if (viewModel.getPresentContainerCount()>mContainerPosition)
        return viewModel.getPresentCardCount(mContainerPosition);
//        return 0;
    }

    public void clear() {
        this.mContainerPosition = -1;
    }

    public CardAdapter getInstance() {
        return CardAdapter.this;
    }

    public void setContainerPosition(int containerPosition) {
        Logger.message("cardAdapter#setContainerPos :" + containerPosition);
        this.mContainerPosition = containerPosition;
    }

    public int getPosition(){
        return mContainerPosition;
    }
}
