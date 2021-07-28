package com.choco_tyranno.team_tree.presentation.container_rv;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.team_tree.presentation.CardViewModel;


public class ContainerScrollListener extends RecyclerView.OnScrollListener {
    private final ObservableBoolean scrolled;
    CardViewModel cardViewModel;

    public ContainerScrollListener(CardViewModel viewModel) {
        this.cardViewModel = viewModel;
        scrolled = new ObservableBoolean(false);
    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
//        if (newState == RecyclerView.SCROLL_STATE_IDLE&&scrolled.get()){
//            synchronized (scrolled){
//                ((MainCardActivity)recyclerView.getContext()).getMainHandler().postDelayed(()-> scrolled.set(false),700);
//            }
//        }
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
//        if (scrolled.get())
//            return;
//        scrolled.set(true);
    }

    public ObservableBoolean getScrolledFlag(){
        return this.scrolled;
    }
}
