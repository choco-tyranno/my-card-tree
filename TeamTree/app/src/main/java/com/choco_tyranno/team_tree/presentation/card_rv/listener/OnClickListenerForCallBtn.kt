package com.choco_tyranno.team_tree.presentation.card_rv.listener

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.choco_tyranno.team_tree.domain.card_data.CardDto
import com.choco_tyranno.team_tree.presentation.SingleToaster
import com.choco_tyranno.team_tree.presentation.card_rv.ContactCardViewHolder
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ActivityScoped

@ActivityScoped
class OnClickListenerForCallBtn private constructor(): View.OnClickListener {
    override fun onClick(v: View) = startCallDialActivity(v)

    private fun startCallDialActivity(callBtn : View){
        val viewPositionManager : ConstraintLayout = callBtn.parent as ConstraintLayout
        val cardView : MaterialCardView = viewPositionManager.parent as MaterialCardView
        val cardFrame : ConstraintLayout = cardView.parent as ConstraintLayout
        val cardRecyclerView : RecyclerView = cardFrame.parent as RecyclerView
        val card : CardDto? = ((cardRecyclerView.getChildViewHolder(cardFrame) as ContactCardViewHolder).binding.card)
        val targetContactNumber = card?.contactNumber
        val intentCall = Intent(Intent.ACTION_DIAL)
        intentCall.setData(Uri.parse("tel:$targetContactNumber"))
        callBtn.context.startActivity(intentCall);
    }

    companion object{
        private val instance : OnClickListenerForCallBtn = OnClickListenerForCallBtn()
        @JvmStatic fun getInstance() = instance
    }
}