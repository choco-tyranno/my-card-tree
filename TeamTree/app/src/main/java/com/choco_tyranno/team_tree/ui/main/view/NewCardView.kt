package com.choco_tyranno.team_tree.ui.main.view

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.util.Pair
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.choco_tyranno.team_tree.R
import com.choco_tyranno.team_tree.ui.card_rv.CardViewShadowProvider
import com.choco_tyranno.team_tree.ui.card_rv.ContactCardViewHolder
import com.choco_tyranno.team_tree.ui.container_rv.CloneCardShadow
import com.choco_tyranno.team_tree.ui.main.DependentView
import com.google.android.material.card.MaterialCardView

class NewCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatButton(context, attrs), DependentView {
    init{
        setOnLongClickListener(OnLongClickListenerForNewCardView.getInstance())
    }
    override fun ready() {
        if (DependentView.ready.get()) return
        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                DependentView.ready.set(true)
                initAttributes()
                if (!DependentView.attributeSettingActions.isEmpty()) {
                    while (!DependentView.attributeSettingActions.isEmpty()) {
                        val action = DependentView.attributeSettingActions.poll()
                        action?.run()
                    }
                }
            }
        })
    }
    private fun initAttributes() {
        setMarginBottom()
    }
    private fun setMarginBottom() {
        val action = Runnable {
            val marginBottom = height / 4
            val constraintSet = ConstraintSet()
            val parent = parent as ConstraintLayout
            constraintSet.clone(parent)
            constraintSet.setMargin(
                id,
                ConstraintLayout.LayoutParams.BOTTOM,
                marginBottom
            )
            constraintSet.applyTo(parent)
        }
        postAttributeSettingAction(action)
    }
    private class OnLongClickListenerForNewCardView private constructor() : OnLongClickListener {
        override fun onLongClick(v: View): Boolean = startDragAndDrop(v)
        private fun startDragAndDrop(v: View) : Boolean {
            v.startDragAndDrop(
                ClipData.newPlainText("", ""),
                CloneCardShadow(CardViewShadowProvider.getInstance(v.context, null)),
                Pair.create("CREATE", ""),
                0
            )
            return false
        }
        companion object {
            private val instance: OnLongClickListenerForNewCardView = OnLongClickListenerForNewCardView()
            @JvmStatic
            fun getInstance() = instance
        }
    }
}