package com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.card_crud_feature.data.card_data.Card;
import com.choco_tyranno.mycardtree.card_crud_feature.data.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.MainCardActivity;
import com.choco_tyranno.mycardtree.databinding.ItemCardcontainerBinding;

import java.util.ArrayList;
import java.util.List;

public class ContainerAdapter extends RecyclerView.Adapter<CardContainerViewHolder> {
    private final LayoutInflater inflater;
    List<CardDTO> cards;
    List<CardDTO> presentCards;

    public ContainerAdapter(Context context) {
        this.inflater = ((MainCardActivity) context).getLayoutInflater();
        this.cards = new ArrayList<>();
        this.presentCards = new ArrayList<>();
    }

    @NonNull
    @Override
    public CardContainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCardcontainerBinding binding = ItemCardcontainerBinding.inflate(inflater.from(parent.getContext()), parent, false);
        return new CardContainerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CardContainerViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return presentCards.size();
    }

    public void presentItem(){

    }

}
