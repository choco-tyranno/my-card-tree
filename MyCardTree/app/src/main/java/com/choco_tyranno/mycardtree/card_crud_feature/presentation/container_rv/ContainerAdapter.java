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
import com.choco_tyranno.mycardtree.databinding.ItemCardEmptyBinding;
import com.choco_tyranno.mycardtree.databinding.ItemCardcontainerBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ContainerAdapter extends RecyclerView.Adapter<ContainerViewHolder> {
    private final CardTreeViewModel viewModel;
    private static final int CARD_TYPE = 0;
    private static final int EMPTY_CARD_TYPE = 1;

    public ContainerAdapter(Context context) {
        Logger.message("contAdapter#constructor");
        this.viewModel = ((MainCardActivity) context).shareViewModel();
    }

    @NonNull
    @Override
    public ContainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Logger.message("contAdapter#onCreateVH");
        if (viewType==EMPTY_CARD_TYPE){
            ItemCardEmptyBinding binding = ItemCardEmptyBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new EmptyCardSpaceViewHolder(binding);
        }
        ItemCardcontainerBinding binding = ItemCardcontainerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CardContainerViewHolder(binding);
    }

    @Override
    public int getItemViewType(int position) {
        Logger.message("contAdapter#getItemViewType pos :"+position);
        if (position+1==viewModel.presentContainerCount())
            return EMPTY_CARD_TYPE;
        return CARD_TYPE;
    }

    @Override
    public void onBindViewHolder(@NonNull ContainerViewHolder holder, int position) {
        Logger.message("contAdapter#onBindVH pos :"+position);
        holder.bind(viewModel, position);
    }

    @Override
    public int getItemCount() {
        Logger.message("contAdapter#getItemCount");
        return viewModel.presentContainerCount();
    }

}
