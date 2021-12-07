package com.choco_tyranno.team_tree.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.choco_tyranno.team_tree.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }
}