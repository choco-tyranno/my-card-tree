package com.choco_tyranno.team_tree.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.choco_tyranno.team_tree.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.OnSuccessListener
import com.google.android.play.core.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity constructor(
    private val REQ_UPDATE: Int = 1001

) : AppCompatActivity() {
    @Inject
    lateinit var appUpdateManager: AppUpdateManager
    private val installStateUpdatedListenerForFlexibleUpdate: InstallStateUpdatedListener =
        createInstallStateUpdatedListenerForFlexibleUpdate()

    private fun createInstallStateUpdatedListenerForFlexibleUpdate(): InstallStateUpdatedListener {
        return InstallStateUpdatedListener { installState ->
            run {
                if (installState.installStatus() == InstallStatus.DOWNLOADED) popupSnackBarForCompleteFlexibleUpdate()
                if (installState.installStatus() == InstallStatus.INSTALLED) appUpdateManager.unregisterListener(
                    installStateUpdatedListenerForFlexibleUpdate
                )
            }
        }
    }

    private fun popupSnackBarForCompleteFlexibleUpdate() {
        Snackbar.make(
            this@SplashActivity.findViewById(R.id.constraintLayout_splash_rootView),
            "\uD83C\uDF20새로운 버전 다운로드 완료!",
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(
                "설치"
            ) { appUpdateManager.completeUpdate() }
            .setActionTextColor(resources.getColor(R.color.colorAccent_b, theme))
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Log.d(TAG, "$appUpdateManager")
        checkUpdates()
    }

    private fun checkUpdates() {
        val updateInfoTask: Task<AppUpdateInfo> = appUpdateManager.appUpdateInfo
        updateInfoTask.addOnSuccessListener { updateInfo ->
            if (updateInfo.updateAvailability() != UpdateAvailability.UPDATE_AVAILABLE
                && updateInfo.updateAvailability() != UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
            ) return@addOnSuccessListener
            var installType = -1
            if (updateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) installType =
                AppUpdateType.FLEXIBLE
            else if (updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) installType =
                AppUpdateType.IMMEDIATE
            if (installType == -1) return@addOnSuccessListener
            if (installType == AppUpdateType.FLEXIBLE) appUpdateManager.registerListener(
                installStateUpdatedListenerForFlexibleUpdate
            )
            appUpdateManager.startUpdateFlowForResult(
                updateInfo,
                installType,
                this@SplashActivity,
                REQ_UPDATE
            );
        }
    }

    override fun onStop() {
        super.onStop()
        SingleToastManager.clear()
        appUpdateManager.unregisterListener(installStateUpdatedListenerForFlexibleUpdate)
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                var installType = -1
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE))
                    installType = AppUpdateType.FLEXIBLE
                else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE))
                    installType = AppUpdateType.IMMEDIATE
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackBarForCompleteFlexibleUpdate()
                    return@addOnSuccessListener
                }
                if (installType == AppUpdateType.IMMEDIATE
                    && appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this@SplashActivity,
                        REQ_UPDATE
                    )
                }
            }
    }

    companion object {
        val TAG = "@@Splash"
    }

}