package com.choco_tyranno.team_tree.ui.card.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
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
    class OnClickListenerForCallBtn private constructor() : OnClickListener {
        override fun onClick(v: View) = startCallDialActivity(v)

        private fun startCallDialActivity(callBtn: View) {
            val resources = callBtn.resources
            val context = callBtn.context
            val viewPositionManager: ConstraintLayout =
                callBtn.parent as ConstraintLayout
            val cardView: MaterialCardView =
                viewPositionManager.parent as MaterialCardView
            val cardFrame: ConstraintLayout = cardView.parent as ConstraintLayout
            val cardRecyclerView: RecyclerView = cardFrame.parent as RecyclerView
            val card: CardDto? =
                (cardRecyclerView.getChildViewHolder(cardFrame) as ContactCardViewHolder).binding.card
            val targetContactNumber = card?.contactNumber
            val cardTitle = "\'"+if (card?.title.equals("")) "이름없음" else card?.title+"\'"
            val alertDialog =
                AlertDialog.Builder(context)
                    .setTitle(R.string.main_callDialogAlertDialogTitle)
                    .setMessage(cardTitle+resources.getString(R.string.main_callDialogAlertDialogMessage))
                    .setCancelable(true)
                    .setPositiveButton(R.string.default_positiveText) { dialog, id ->
                        val intentDial = Intent(Intent.ACTION_DIAL)
                        intentDial.setData(Uri.parse("tel:$targetContactNumber"))
                        context.startActivity(intentDial)
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.default_negativeText){ dialog , id ->
                        dialog.dismiss()
                    }.create()
            alertDialog.show()
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(resources.getColor(R.color.colorAccent_c, context.theme))
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(resources.getColor(R.color.defaultTextColor, context.theme))
        }

        companion object {
            private val instance: OnClickListenerForCallBtn = OnClickListenerForCallBtn()
            @JvmStatic
            fun getInstance() = instance
        }
    }

    class AccessDeniedAnimationProvider {
        companion object {
            private lateinit var instance: Animation
            @JvmStatic
            fun getInstance(context: Context): Animation {
                if (::instance.isInitialized)
                    return instance
                instance = AnimationUtils.loadAnimation(context, R.anim.shaking_accessdenied)
                return instance
            }
        }
    }
}