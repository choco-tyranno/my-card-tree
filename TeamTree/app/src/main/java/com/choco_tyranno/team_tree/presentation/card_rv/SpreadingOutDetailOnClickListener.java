package com.choco_tyranno.team_tree.presentation.card_rv;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import com.choco_tyranno.team_tree.databinding.ItemCardframeBinding;
import com.choco_tyranno.team_tree.domain.card_data.CardDto;
import com.choco_tyranno.team_tree.presentation.detail_page.DetailCardActivity;
import com.choco_tyranno.team_tree.presentation.main.MainCardActivity;

public class SpreadingOutDetailOnClickListener implements OnClickListener {
    public static final int REQ_MANAGE_DETAIL = 1;

    @Override
    public void onClick(View view) {
        ConstraintLayout cardFrame = (ConstraintLayout) view.getParent().getParent().getParent();
        com.choco_tyranno.team_tree.presentation.card_rv.CardRecyclerView cardRecyclerView = (CardRecyclerView) cardFrame.getParent();
        com.choco_tyranno.team_tree.presentation.card_rv.ContactCardViewHolder cardViewHolder = (ContactCardViewHolder) cardRecyclerView.getChildViewHolder(cardFrame);
        ItemCardframeBinding binding = cardViewHolder.getBinding();
        CardDto cardDTO = binding.getCard();
        ImageView cardImageView = binding.cardBackLayout.imageViewCardBackCardImage;
        com.choco_tyranno.team_tree.presentation.main.MainCardActivity mainCardActivity =((MainCardActivity)view.getContext());
        Intent intent = new Intent(mainCardActivity, DetailCardActivity.class);
        intent.putExtra("post_card", cardDTO);
        Pair<View, String> pairImg = Pair.create(cardImageView, cardImageView.getTransitionName());
        @SuppressWarnings("unchecked") ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(mainCardActivity, pairImg);
        mainCardActivity.startActivityForResult(intent, REQ_MANAGE_DETAIL, activityOptionsCompat.toBundle());
    }
}
