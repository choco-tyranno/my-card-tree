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
    private final LayoutInflater inflater;
    private final List<CardDTO> mData;
    private final List<CardDTO> mPresentData;
    private final CardTreeViewModel viewModel;

    public CardAdapter(Context context) {
        mData = new ArrayList<>();
        mPresentData = new ArrayList<>();
        inflater = ((MainCardActivity) context).getLayoutInflater();
        this.viewModel = ((MainCardActivity) context).shareViewModel();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCardFrameBinding binding = ItemCardFrameBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ContactCardViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Logger.message("CardAdapter#onBVH");
        holder.bind(mPresentData.get(position));
    }

    @Override
    public int getItemCount() {
        return mPresentData.size();
    }

    public void clear() {
        this.mData.clear();
        this.mPresentData.clear();
    }

    public void submitList(List<CardDTO> data) {
        this.mData.addAll(data);
    }

    public void presentCardViews(int groupingFlag) {
        setPresentDataList(groupingFlag);
        notifyDataSetChanged();
    }

    private void setPresentDataList(int groupingFlag){
        mPresentData.clear();
        for (CardDTO dto : mData) {
            if (dto.getBossNo() == groupingFlag)
                mPresentData.add(dto);
        }
        Collections.sort(mPresentData);
    }

    public CardAdapter getInstance(){
        return CardAdapter.this;
    }
}
