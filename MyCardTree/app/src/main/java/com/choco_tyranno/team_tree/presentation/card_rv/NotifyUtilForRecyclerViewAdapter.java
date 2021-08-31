package com.choco_tyranno.team_tree.presentation.card_rv;

import android.app.Activity;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class NotifyUtilForRecyclerViewAdapter {
    public static final int NOTIFY_ITEM_INSERTED = 1;
    public static final int NOTIFY_ITEM_REMOVED = 2;
    public static final int NOTIFY_ITEM_CHANGED = 3;

    public NotifyUtilForRecyclerViewAdapter() {

    }

    public static void notifySingleItemChanged(int notifyFlag, RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter, int position, View view) {
        switch (notifyFlag){
            case NOTIFY_ITEM_INSERTED:
                runOnUiThread(()->adapter.notifyItemInserted(position), view);
                break;
            case NOTIFY_ITEM_REMOVED:
                runOnUiThread(()->adapter.notifyItemRemoved(position), view);
                break;
            case NOTIFY_ITEM_CHANGED:
                runOnUiThread(()->adapter.notifyItemChanged(position), view);
                break;
        }
    }

    public static void notifyMultiItemChanged(int notifyFlag, RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter, int startPosition, int count, View view){
        switch (notifyFlag){
            case NOTIFY_ITEM_INSERTED:
                runOnUiThread(()->adapter.notifyItemRangeInserted(startPosition, count), view);
                break;
            case NOTIFY_ITEM_REMOVED:
                runOnUiThread(()->adapter.notifyItemRangeRemoved(startPosition, count), view);
                break;
            case NOTIFY_ITEM_CHANGED:
                runOnUiThread(()->adapter.notifyItemRangeChanged(startPosition, count), view);
                break;
        }
    }

    public static void runOnUiThread(Runnable action, View view){
        ((Activity)view.getContext()).runOnUiThread(action);
    }
}
