package com.choco_tyranno.team_tree.presentation.settings

import android.content.Intent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import com.choco_tyranno.team_tree.R
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity

class SettingsViewModel : ViewModel() {

    fun openPageOssLicense(view : View){
        view.context.startActivity(
            Intent(view.context, OssLicensesMenuActivity::class.java)
        )

    }

}