package com.choco_tyranno.team_tree.ui.card.view

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.choco_tyranno.team_tree.databinding.ItemCardframeBinding
import com.choco_tyranno.team_tree.domain.card_data.CardDto
import com.choco_tyranno.team_tree.ui.card_rv.CardRecyclerView
import com.choco_tyranno.team_tree.ui.card_rv.ContactCardViewHolder
import com.choco_tyranno.team_tree.ui.detail_page.DetailCardActivity
import com.choco_tyranno.team_tree.ui.detail_page.view.CameraButton
import com.choco_tyranno.team_tree.ui.main.MainCardActivity
import com.google.android.material.imageview.ShapeableImageView

class DetailIntentView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ShapeableImageView(context, attrs) {
    init {
        setOnClickListener(OnClickListenerForDetailIntentView.getInstance())
    }
    private class OnClickListenerForDetailIntentView private constructor() : OnClickListener {
        override fun onClick(v: View) = startDetailActivity(v)
        private fun startDetailActivity(v: View) {
            val cardBackPositionManager = v.parent
            val cardBackContainer = cardBackPositionManager.parent
            val cardFrame = cardBackContainer.parent as ConstraintLayout
            val cardRecyclerView = cardFrame.parent as CardRecyclerView
            val cardViewHolder = cardRecyclerView.getChildViewHolder(cardFrame) as ContactCardViewHolder
            val binding: ItemCardframeBinding = cardViewHolder.binding
            val cardDTO: CardDto = binding.card
            val cardImageView: ImageView = binding.cardBackLayout.imageViewCardBackCardImage
            val mainActivity = v.context as MainCardActivity
            val intent = Intent(mainActivity, DetailCardActivity::class.java)
            intent.putExtra("post_card", cardDTO)
            val pairImg = Pair.create<View, String>(cardImageView, cardImageView.transitionName)
            val activityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(mainActivity, pairImg)
            mainActivity.getActivityResultLauncherForDetailScene()
                .launch(intent, activityOptionsCompat)
        }
        companion object {
            private val instance: OnClickListenerForDetailIntentView =
                OnClickListenerForDetailIntentView()
            @JvmStatic
            fun getInstance() = instance
        }
    }
}