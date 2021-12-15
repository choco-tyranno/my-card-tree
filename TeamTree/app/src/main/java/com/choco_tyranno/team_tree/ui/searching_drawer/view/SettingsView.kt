package com.choco_tyranno.team_tree.ui.searching_drawer.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.choco_tyranno.team_tree.R
import com.choco_tyranno.team_tree.ui.main.MainCardActivity

class SettingsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageButton(context, attrs) {
    init{
        setOnClickListener(OnClickListenerForSettingsView.getInstance())
    }
    private class OnClickListenerForSettingsView private constructor() : OnClickListener {
        override fun onClick(v: View) = startSettingsScene(v)
        private fun startSettingsScene(v: View) {
            val activity = v.context as MainCardActivity
            val binding = activity.getBinding()
            val viewModel = activity.cardViewModel
            val mainDL: DrawerLayout = binding.drawerLayoutMainSearchDrawer
            mainDL.closeDrawer(GravityCompat.END)
            viewModel.toggleSettingsOn()
            val appBar = activity.supportActionBar
            appBar?.title = v.context.resources.getString(R.string.settings_appBarTitle)
            appBar?.setDisplayHomeAsUpEnabled(true)
            appBar?.show()
        }
        companion object {
            private val instance: OnClickListenerForSettingsView = OnClickListenerForSettingsView()
            @JvmStatic
            fun getInstance() = instance
        }
    }
}