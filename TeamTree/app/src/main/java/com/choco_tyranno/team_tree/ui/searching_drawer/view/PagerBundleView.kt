package com.choco_tyranno.team_tree.ui.searching_drawer.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import com.choco_tyranno.team_tree.ui.main.MainCardActivity

class PagerBundleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageButton(context, attrs) {
    init{
        setOnClickListener(OnClickListenerForPagerBundleView.getInstance())
    }
    private class OnClickListenerForPagerBundleView private constructor() : OnClickListener {
        override fun onClick(v: View) = showPagerBundle(v)
        private fun showPagerBundle(v: View) {
            (v.context as MainCardActivity).cardViewModel.showPagerBundle(v.id)
        }
        companion object {
            private val instance: OnClickListenerForPagerBundleView = OnClickListenerForPagerBundleView()
            @JvmStatic
            fun getInstance() = instance
        }
    }
}