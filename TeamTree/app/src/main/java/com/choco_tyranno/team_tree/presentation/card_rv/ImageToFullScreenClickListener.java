package com.choco_tyranno.team_tree.presentation.card_rv;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import com.choco_tyranno.team_tree.databinding.ItemCardFrameBinding;
import com.choco_tyranno.team_tree.domain.card_data.CardDto;
import com.choco_tyranno.team_tree.presentation.MainCardActivity;
import com.choco_tyranno.team_tree.presentation.detail_page.DetailCardActivity;

public class ImageToFullScreenClickListener implements OnClickListener {
    public static final int REQ_MANAGE_DETAIL = 1;

    @Override
    public void onClick(View view) {
        FrameLayout frameLayout = (FrameLayout) view.getParent().getParent().getParent();
        com.choco_tyranno.team_tree.presentation.card_rv.CardRecyclerView cardRecyclerView = (CardRecyclerView) frameLayout.getParent();
        com.choco_tyranno.team_tree.presentation.card_rv.ContactCardViewHolder cardViewHolder = (ContactCardViewHolder) cardRecyclerView.getChildViewHolder(frameLayout);
        ItemCardFrameBinding binding = cardViewHolder.getBinding();
        CardDto cardDTO = binding.getCard();
        ImageView cardImageView = binding.cardBackLayout.backCardImage;
        com.choco_tyranno.team_tree.presentation.MainCardActivity mainCardActivity =((MainCardActivity)view.getContext());
        Intent intent = new Intent(mainCardActivity, DetailCardActivity.class);
        intent.putExtra("post_card", cardDTO);
        Pair<View, String> pairImg = Pair.create(cardImageView, cardImageView.getTransitionName());
        @SuppressWarnings("unchecked") ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(mainCardActivity, pairImg);
        mainCardActivity.startActivityForResult(intent, REQ_MANAGE_DETAIL, activityOptionsCompat.toBundle());
    }
}
