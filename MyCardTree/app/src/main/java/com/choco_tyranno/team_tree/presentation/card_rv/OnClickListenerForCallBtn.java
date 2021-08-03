package com.choco_tyranno.team_tree.presentation.card_rv;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class OnClickListenerForCallBtn implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        View targetViewItem = (View) v.getParent().getParent().getParent();
        RecyclerView targetRecyclerView = (RecyclerView) targetViewItem.getParent();
        String targetContactNumber = ((ContactCardViewHolder)targetRecyclerView.getChildViewHolder(targetViewItem)).getBinding().getCard().getContactNumber();
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:"+targetContactNumber));
        v.getContext().startActivity(callIntent);
    }
}
