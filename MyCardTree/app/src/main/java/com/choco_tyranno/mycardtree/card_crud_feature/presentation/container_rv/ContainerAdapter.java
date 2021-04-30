package com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.card_crud_feature.Logger;
import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.CardTreeViewModel;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.MainCardActivity;
import com.choco_tyranno.mycardtree.databinding.ItemCardcontainerBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ContainerAdapter extends RecyclerView.Adapter<CardContainerViewHolder> {
    private final CardTreeViewModel viewModel;
    private final LayoutInflater inflater;

    public ContainerAdapter(Context context) {
        this.inflater = ((MainCardActivity) context).getLayoutInflater();
        this.viewModel = ((MainCardActivity) context).shareViewModel();
    }

    @NonNull
    @Override
    public CardContainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCardcontainerBinding binding = ItemCardcontainerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CardContainerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CardContainerViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return viewModel.presentContainerCount();
    }

}
