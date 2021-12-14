package com.choco_tyranno.team_tree.ui.card.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.choco_tyranno.team_tree.R
import com.choco_tyranno.team_tree.domain.card_data.CardDto
import com.choco_tyranno.team_tree.ui.card_rv.ContactCardViewHolder
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped

class CallDialView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageButton(context, attrs) {
    init {
        setOnClickListener(OnClickListenerForCallBtn.getInstance())
    }

    @ActivityScoped
    class OnClickListenerForCallBtn private constructor(): OnClickListener {
        override fun onClick(v: View) = checkCallPermission(v)
//        override fun onClick(v: View) = test(v)

        private fun test(view : View){
            view.startAnimation(AccessDeniedAnimationProvider.getInstance(view.context))
        }

        private fun checkCallPermission(view : View){
            view.context.checkSelfPermission(Manifest.permission.CALL_PHONE)
        }

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

    class AccessDeniedAnimationProvider{
        companion object{
            private lateinit var instance : Animation
            @JvmStatic fun getInstance(context: Context) : Animation{
                if (::instance.isInitialized)
                    return instance
                instance = AnimationUtils.loadAnimation(context, R.anim.shaking_accessdenied)
                return instance
            }
        }
    }
}