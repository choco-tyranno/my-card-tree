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
    private int presentContainerLength;

    public ContainerAdapter(Context context) {
        this.inflater = ((MainCardActivity) context).getLayoutInflater();
        this.mData = new ArrayList<>();
        this.presentContainerLength = 0;
    }

    @NonNull
    @Override
    public CardContainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCardcontainerBinding binding = ItemCardcontainerBinding.inflate(inflater.from(parent.getContext()), parent, false);
        return new CardContainerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CardContainerViewHolder holder, int position) {
        holder.bind(position + 1, mData.get(position));
    }

    @Override
    public int getItemCount() {
        return presentContainerLength;
    }

    public void submitList(List<CardDTO> maybeExistData) {
        this.mData.clear();
        Optional.ofNullable(maybeExistData).ifPresent(data->{
            this.mData.addAll(separateToListByContainerNo(data));
        });

        notifyDataSetChanged();
    }

    private List<List<CardDTO>> separateToListByContainerNo(List<CardDTO> unrefinedData) {
        List<List<CardDTO>> basket = new ArrayList<>();
        Optional.ofNullable(unrefinedData).ifPresent(dtoList -> {
            for (CardDTO dto : dtoList) {
                int position = Integer.parseInt(dto.getContactNumber())-1;
                basket.set(position, Optional.ofNullable(basket.get(position)).orElse(new ArrayList<>()));
                basket.get(position).add(dto);
            }
        });
        return basket;
    }

    private void updatePresentData() {
//        mPresentData.clear();
//        mPresentData.addAll();
        notifyDataSetChanged();
    }
}
