package com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
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
import java.util.concurrent.atomic.AtomicInteger;

public class CardAdapter extends RecyclerView.Adapter<CardViewHolder> {
    private final CardTreeViewModel viewModel;
    private int mContainerPosition;

    public CardAdapter(Context context) {
        Logger.message("cardAdapter#constructor");
        this.viewModel = ((MainCardActivity) context).shareViewModel();
        mContainerPosition = -1;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Logger.message("cardAdapter/onCreateVH");
        ItemCardFrameBinding binding = ItemCardFrameBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ContactCardViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int cardPosition) {
        Logger.message("cardAdapter/onBindVH");
        holder.bind(viewModel, viewModel.getCardDTO(mContainerPosition, cardPosition), viewModel.getCardState(mContainerPosition, cardPosition));
    }

    @Override
    public int getItemCount() {
        Logger.message("cardAdapter#getItemCount");
        if (mContainerPosition==-1){
            Logger.message("container pos -1 detected.");
            return 0;
        }
        return viewModel.getPresentCardCount(mContainerPosition);
    }

    public void clear() {
        Logger.message("cardAdapter#clear : mContainerPos -> -1");
        this.mContainerPosition = -1;
    }

    public CardAdapter getInstance() {
        return CardAdapter.this;
    }

    public void setContainerPosition(int containerPosition) {
        Logger.message("cardAdapter#setContainerPos :"+containerPosition);
        this.mContainerPosition = containerPosition;
    }
}
