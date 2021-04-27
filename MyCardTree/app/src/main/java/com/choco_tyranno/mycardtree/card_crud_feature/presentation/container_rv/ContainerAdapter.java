package com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.MainCardActivity;
import com.choco_tyranno.mycardtree.databinding.ItemCardcontainerBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContainerAdapter extends RecyclerView.Adapter<CardContainerViewHolder> {
    private final LayoutInflater inflater;
    private final List<List<CardDTO>> mData;
    private final List<List<CardDTO>> mPresentData;

    public ContainerAdapter(Context context) {
        this.inflater = ((MainCardActivity) context).getLayoutInflater();
        this.mData = new ArrayList<>();
        this.mPresentData = new ArrayList<>();
    }

    @NonNull
    @Override
    public CardContainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCardcontainerBinding binding = ItemCardcontainerBinding.inflate(inflater.from(parent.getContext()), parent, false);
        return new CardContainerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CardContainerViewHolder holder, int position) {
//        holder.bind(position+1, mPresentData);
    }

    @Override
    public int getItemCount() {
        return mPresentData.size();
    }

    public void submitList(List<CardDTO> listData){
        this.mData.clear();
//        this.mData.addAll(listData);

        this.mPresentData.clear();
//        this.mPresentData.addAll(listData);
        notifyDataSetChanged();
    }

    private List<List<CardDTO>> separateToListByContainerNo(List<CardDTO> unrefinedData){
        List<List<CardDTO>> basket = new ArrayList<>();
        Optional.ofNullable(unrefinedData).ifPresent(data ->{
            int size = data.size();
//            for (:
//                 ) {
//
//            }
//            basket.;
        });
//        basket.add();
        return null;

    }

    private void updatePresentData(){
        mPresentData.clear();

//        mPresentData.addAll();
        notifyDataSetChanged();
    }
}
