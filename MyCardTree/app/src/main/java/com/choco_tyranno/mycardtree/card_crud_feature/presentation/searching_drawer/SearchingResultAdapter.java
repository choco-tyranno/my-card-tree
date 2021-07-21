package com.choco_tyranno.mycardtree.card_crud_feature.presentation.searching_drawer;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.CardViewModel;
import com.choco_tyranno.mycardtree.databinding.ItemSearchingResultBinding;

public class SearchingResultAdapter extends RecyclerView.Adapter<SearchingResultAdapter.SearchingResultViewHolder> {
    private CardViewModel viewModel;

    public SearchingResultAdapter(CardViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public SearchingResultAdapter.SearchingResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSearchingResultBinding binding =ItemSearchingResultBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        binding.setViewModel(viewModel);
        return new SearchingResultViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchingResultAdapter.SearchingResultViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return viewModel.getSearchingResultItemCount();
    }

    public void setViewModel(CardViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public static class SearchingResultViewHolder extends RecyclerView.ViewHolder {
        private final ItemSearchingResultBinding mBinding;

        public SearchingResultViewHolder(ItemSearchingResultBinding binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        public void bind(int itemPosition){
            CardViewModel viewModel = mBinding.getViewModel();
            final int focusPageNo = viewModel.getFocusPageNo();
            CardDTO theCard = viewModel.getSearchingResultCard(focusPageNo, itemPosition);
            mBinding.setCard(theCard);
        }

        public ItemSearchingResultBinding getBinding(){
            return mBinding;
        }
    }
}
