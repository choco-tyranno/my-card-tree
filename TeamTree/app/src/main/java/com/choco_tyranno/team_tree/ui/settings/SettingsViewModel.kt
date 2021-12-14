package com.choco_tyranno.team_tree.ui.settings

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity

class SettingsViewModel : ViewModel() {

    fun openPageOssLicense(view : View){
        view.context.startActivity(
            Intent(view.context, OssLicensesMenuActivity::class.java)
        )
        OssLicensesMenuActivity.setActivityTitle("오픈소스 라이선스")
    }

}