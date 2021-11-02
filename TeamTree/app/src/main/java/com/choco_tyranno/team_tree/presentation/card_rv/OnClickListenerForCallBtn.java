package com.choco_tyranno.team_tree.presentation.card_rv;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

public class OnClickListenerForCallBtn implements View.OnClickListener {
    @Override
    public void onClick(View callBtn) {
        MaterialCardView cardView  = (MaterialCardView) callBtn.getParent();
        ConstraintLayout cardFrame = (ConstraintLayout) cardView.getParent();
        RecyclerView cardRecyclerView = (RecyclerView) cardFrame.getParent();
        String targetContactNumber = ((ContactCardViewHolder)cardRecyclerView.getChildViewHolder(cardFrame)).getBinding().getCard().getContactNumber();
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:"+targetContactNumber));
        callBtn.getContext().startActivity(callIntent);
    }
}
