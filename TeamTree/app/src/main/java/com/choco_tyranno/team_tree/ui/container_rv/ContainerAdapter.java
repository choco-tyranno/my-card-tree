package com.choco_tyranno.team_tree.ui.container_rv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.team_tree.databinding.ItemEmptycontainerBinding;
import com.choco_tyranno.team_tree.databinding.ItemCardcontainerBinding;
import com.choco_tyranno.team_tree.ui.CardViewModel;
import com.choco_tyranno.team_tree.ui.main.MainCardActivity;

public class ContainerAdapter extends RecyclerView.Adapter<ContainerViewHolder> {
    private final CardViewModel viewModel;
    private static final int CARD_TYPE = 0;
    private static final int EMPTY_CARD_TYPE = 1;

    public ContainerAdapter(Context context) {
        this.viewModel = ((MainCardActivity) context).getCardViewModel();
    }

    @NonNull
    @Override
    public ContainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==EMPTY_CARD_TYPE){
            ItemEmptycontainerBinding binding = ItemEmptycontainerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new EmptyCardSpaceViewHolder(binding, viewModel);
        }
        ItemCardcontainerBinding binding = ItemCardcontainerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CardContainerViewHolder(binding, viewModel);
    }

    @Override
    public int getItemViewType(int position) {
        if (position+1==viewModel.presentContainerCount())
            return EMPTY_CARD_TYPE;
        return CARD_TYPE;
    }

    @Override
    public void onBindViewHolder(@NonNull ContainerViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return viewModel.presentContainerCount();
    }

}
