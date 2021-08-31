package com.choco_tyranno.team_tree.presentation.card_rv;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class OnClickListenerForMessageBtn implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        View targetViewItem = (View) v.getParent().getParent().getParent();
        RecyclerView targetRecyclerView = (RecyclerView) targetViewItem.getParent();
        String targetContactNumber = ((ContactCardViewHolder)targetRecyclerView.getChildViewHolder(targetViewItem)).getBinding().getCard().getContactNumber();
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("smsto:"+targetContactNumber));
        v.getContext().startActivity(smsIntent);
    }
}
