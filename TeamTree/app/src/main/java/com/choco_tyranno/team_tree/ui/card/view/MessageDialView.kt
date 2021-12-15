package com.choco_tyranno.team_tree.ui.card.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.choco_tyranno.team_tree.R
import com.choco_tyranno.team_tree.ui.card_rv.ContactCardViewHolder
import com.google.android.material.card.MaterialCardView

class MessageDialView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageButton(context, attrs) {
    init {
        setOnClickListener(OnClickListenerForMessageBtn.getInstance())
    }
    private class OnClickListenerForMessageBtn private constructor() : OnClickListener {
        override fun onClick(v: View) = startMessageDialActivity(v)
        private fun startMessageDialActivity(v: View) {
            val resources = v.resources
            val context = v.context
            val viewPositionManager: ConstraintLayout = v.parent as ConstraintLayout
            val cardView: MaterialCardView = viewPositionManager.parent as MaterialCardView
            val cardFrame: ConstraintLayout = cardView.parent as ConstraintLayout
            val cardRecyclerView: RecyclerView = cardFrame.parent as RecyclerView
            val card = (cardRecyclerView.getChildViewHolder(cardFrame) as ContactCardViewHolder).binding.card
            val cardTitle = "\'" + if (card?.title.equals("")) "이름없음" else card?.title + "\'"
            val alertDialog =
                AlertDialog.Builder(context)
                    .setTitle(R.string.main_messageDialogAlertDialogTitle)
                    .setMessage(cardTitle + resources.getString(R.string.main_messageDialogAlertDialogMessage))
                    .setCancelable(true)
                    .setPositiveButton(R.string.default_positiveText) { dialog, id ->
                        val smsIntent = Intent(Intent.ACTION_SENDTO)
                        smsIntent.setData(Uri.parse("smsto:" + card?.contactNumber))
                        context.startActivity(smsIntent)
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.default_negativeText) { dialog, id ->
                        dialog.dismiss()
                    }.create()
            alertDialog.show()
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(resources.getColor(R.color.colorAccent_c, context.theme))
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(resources.getColor(R.color.defaultTextColor, context.theme))
        }
        companion object {
            private val instance: OnClickListenerForMessageBtn = OnClickListenerForMessageBtn()
            @JvmStatic
            fun getInstance() = instance
        }
    }
}