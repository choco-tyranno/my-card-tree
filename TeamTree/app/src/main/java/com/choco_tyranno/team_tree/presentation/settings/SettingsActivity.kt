package com.choco_tyranno.team_tree.presentation.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.choco_tyranno.team_tree.R
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        OssLicensesMenuActivity.setActivityTitle("")
    }
}