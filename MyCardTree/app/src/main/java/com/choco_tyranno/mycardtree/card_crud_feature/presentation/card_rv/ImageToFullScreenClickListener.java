package com.choco_tyranno.mycardtree.card_crud_feature.presentation.card_rv;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import com.choco_tyranno.mycardtree.card_crud_feature.domain.card_data.CardDTO;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.MainCardActivity;
import com.choco_tyranno.mycardtree.card_crud_feature.presentation.detail_page.DetailCardActivity;
import com.choco_tyranno.mycardtree.databinding.ItemCardFrameBinding;

public class ImageToFullScreenClickListener implements OnClickListener {
    public static final int REQ_MANAGE_DETAIL = 1;

    @Override
    public void onClick(View view) {
        FrameLayout frameLayout = (FrameLayout) view.getParent().getParent().getParent();
        CardRecyclerView cardRecyclerView = (CardRecyclerView) frameLayout.getParent();
        ContactCardViewHolder cardViewHolder = (ContactCardViewHolder) cardRecyclerView.getChildViewHolder(frameLayout);
        ItemCardFrameBinding binding = cardViewHolder.getBinding();
        CardDTO cardDTO = binding.getCard();
        ImageView cardImageView = binding.cardBackLayout.backCardImageView;
        MainCardActivity mainCardActivity =((MainCardActivity)view.getContext());
        Intent intent = new Intent(mainCardActivity, DetailCardActivity.class);
        intent.putExtra("post_card", cardDTO);
        Pair<View, String> pairImg = Pair.create(cardImageView, cardImageView.getTransitionName());
        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(mainCardActivity, pairImg);
        mainCardActivity.startActivityForResult(intent, REQ_MANAGE_DETAIL, activityOptionsCompat.toBundle());
    }
}
