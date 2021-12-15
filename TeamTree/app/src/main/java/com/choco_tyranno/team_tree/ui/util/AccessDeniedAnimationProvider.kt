package com.choco_tyranno.team_tree.ui.util

import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.choco_tyranno.team_tree.R

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