package com.choco_tyranno.mycardtree.card_crud_feature.presentation.container_rv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.MainCardActivity;
import com.choco_tyranno.mycardtree.databinding.ItemCardcontainerBinding;

import java.util.ArrayList;
import java.util.List;

public class ContainerAdapter extends RecyclerView.Adapter<CardContainerViewHolder> {
    private static ContainerAdapter instance;
    private final LayoutInflater inflater;
    List<CardDTO> allData;
    List<CardDTO> presentData;

    public ContainerAdapter(Context context) {
        instance = this;
        this.inflater = ((MainCardActivity) context).getLayoutInflater();
        this.allData = new ArrayList<>();
        this.presentData = new ArrayList<>();
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
        return presentData.size();
    }

    public void presentItem(){

    }

//    @BindingAdapter("container_data")
//    public static void bindRecyclerview(RecyclerView rv, List<CardDTO> data){
//        Optional.ofNullable(rv.getAdapter()).ifPresent((adapter)->{((ContainerAdapter) adapter).submitList(data);});
//    }

    public void submitList(List<CardDTO> listData){
        this.allData =listData;
    }
}
