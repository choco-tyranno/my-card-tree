package com.choco_tyranno.team_tree.ui.container_rv;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

abstract class ContainerViewHolder extends RecyclerView.ViewHolder {
    public final static int CARD_LAYER_TYPE = 1000;
    public final static int REPLICA_LAYER_TYPE = 2000;
    public ContainerViewHolder(@NonNull View itemView) {
        super(itemView);
    }
    public abstract void bind(int position);
}
