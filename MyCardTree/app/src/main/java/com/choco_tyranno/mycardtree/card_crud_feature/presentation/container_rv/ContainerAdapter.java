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
    private final LayoutInflater inflater;
    private final List<List<CardDTO>> mData;
    private final List<Integer> mPresentContainerGroupingFlags;
    private final CardTreeViewModel viewModel;

    public ContainerAdapter(Context context) {
        this.inflater = ((MainCardActivity) context).getLayoutInflater();
        this.mData = new ArrayList<>();
        this.mPresentContainerGroupingFlags = new ArrayList<>();
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
        Logger.message("Container/onBVH pos:"+position);
        holder.bind(position + 1, mPresentContainerGroupingFlags.get(position), mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mPresentContainerGroupingFlags.size();
    }

    public void submitList(List<List<CardDTO>> data) {
        this.mData.clear();
        this.mData.addAll(data);
//        Optional.ofNullable(maybeExistData).ifPresent(data -> {
//            this.mData.addAll(separateToListByContainerNo(data));
//        });
    }

    public void presentInitContainerViews() {
        Logger.message("ContainerAdapter/initPresent");
        setInitPresentFlags();
        notifyDataSetChanged();
    }

    private void setInitPresentFlags() {
        int nextGroupFlag = -1;

        for (int i = 0; i < mData.size(); i++) {
            List<CardDTO> dtoList = mData.get(i);
            boolean hasNext = false;
            int foundFlag = -1;

            if (mData.get(i).isEmpty()) {
                return;
            }

            if (i == 0) {
                foundFlag = dtoList.get(0).getBossNo();
                nextGroupFlag = dtoList.get(0).getCardNo();
                hasNext = true;
            } else {
                for (CardDTO dto : dtoList) {
                    if (dto.getBossNo() == nextGroupFlag) {
                        foundFlag = dto.getBossNo();
                        nextGroupFlag = dto.getCardNo();
                        hasNext = true;
                        break;
                    }
                }
            }

            mPresentContainerGroupingFlags.add(i, foundFlag);

            if (!hasNext)
                break;
        }
    }

//    private List<List<CardDTO>> separateToListByContainerNo(List<CardDTO> unrefinedData) {
//        List<List<CardDTO>> basket = new ArrayList<>();
//        Optional.ofNullable(unrefinedData).ifPresent(dtoList -> {
//            for (CardDTO dto : dtoList) {
//                int position = dto.getContainerNo() - 1;
//                if (position > basket.size() - 1) {
//                    basket.add(new ArrayList<>());
//                }
//                basket.get(position).add(dto);
//            }
//        });
//        return basket;
//    }

}
