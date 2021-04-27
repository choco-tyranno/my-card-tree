package com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.Card;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.MainCardActivity;
import com.choco_tyranno.mycardtree.databinding.ItemCardFrameBinding;

import java.util.ArrayList;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardViewHolder>{
    private final LayoutInflater inflater;
    private final List<CardDTO> mData;
    private final List<CardDTO> mPresentData;

    public CardAdapter(Context context){
        mData = new ArrayList<>();
        mPresentData = new ArrayList<>();
        inflater = ((MainCardActivity)context).getLayoutInflater();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCardFrameBinding binding = ItemCardFrameBinding.inflate(inflater.from(parent.getContext()), parent, false);
        return new ContactCardViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public int getItemCount() {
        return mPresentData.size();
    }

    private CardDTO getItem(int position){
        return mPresentData.get(position);
    }

    public void clear(){
        this.mData.clear();
        this.mPresentData.clear();
    }

    public void submitList(List<CardDTO> data){
        this.mData.addAll(data);
        this.mPresentData.addAll(data);
        notifyDataSetChanged();
    }
}
