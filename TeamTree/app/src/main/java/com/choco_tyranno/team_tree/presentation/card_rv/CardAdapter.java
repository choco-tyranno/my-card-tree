package com.choco_tyranno.team_tree.presentation.card_rv;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.team_tree.databinding.ItemCardframeBinding;
import com.choco_tyranno.team_tree.presentation.CardViewModel;
import com.choco_tyranno.team_tree.presentation.DisplayUtil;
import com.choco_tyranno.team_tree.presentation.MainCardActivity;

/**
 *  It is recommended that CardAdapter instance be recycled. Use #clear().
 *
 * */
public class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {
    private final CardViewModel viewModel;
    private int mContainerPosition;

    public CardAdapter(Context context) {
        this.viewModel = ((MainCardActivity) context).getCardViewModel();
        mContainerPosition = -1;
    }

    public void initialize(int containerPosition){
        this.mContainerPosition = containerPosition;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCardframeBinding binding = ItemCardframeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ContactCardViewHolder(binding, viewModel);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int cardPosition) {
        holder.bind(viewModel.getCardDto(mContainerPosition, cardPosition)
                , viewModel.getCardState(mContainerPosition, cardPosition)
                , viewModel.getCardImage(mContainerPosition, cardPosition));
    }

    @Override
    public int getItemCount() {
        if (mContainerPosition == -1) {
            return 0;
        }
        return viewModel.getPresentCardCount(mContainerPosition);
    }

    public void clear() {
        this.mContainerPosition = -1;
    }

    public CardAdapter getInstance() {
        return CardAdapter.this;
    }

    public void setContainerPosition(int containerPosition) {
        this.mContainerPosition = containerPosition;
    }

    public int getContainerPosition(){
        return mContainerPosition;
    }
}
