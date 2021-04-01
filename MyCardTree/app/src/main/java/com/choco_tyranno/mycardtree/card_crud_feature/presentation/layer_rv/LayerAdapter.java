package com.choco_tyranno.mycardtree.card_crud_feature.presentation.layer_rv;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.card_crud_feature.data.layer_data.CardContainer;

import java.util.List;

public class LayerAdapter extends RecyclerView.Adapter<CardLayerViewHolder> {

    List<CardContainer> cardContainers;

    public LayerAdapter(){

    }

    @NonNull
    @Override
    public CardLayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull CardLayerViewHolder holder, int position) {
        CardContainer cardContainer = getItem(position);
//        holder.binding
    }

    CardContainer getItem(int position){
        return cardContainers.get(position);
    }


    @Override
    public int getItemCount() {
        return 0;
    }




}
