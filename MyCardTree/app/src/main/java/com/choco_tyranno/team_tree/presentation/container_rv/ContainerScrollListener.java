package com.choco_tyranno.team_tree.presentation.container_rv;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.choco_tyranno.team_tree.Logger;
import com.choco_tyranno.team_tree.presentation.CardViewModel;


public class ContainerScrollListener extends RecyclerView.OnScrollListener {
//    private final ObservableBoolean scrolled;

    public ContainerScrollListener() {
//        scrolled = new ObservableBoolean(false);
    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
//        if (scrolled.get())
//            return;
//        scrolled.set(true);
    }

//    public ObservableBoolean getScrolledFlag(){
//        return this.scrolled;
//    }
}
