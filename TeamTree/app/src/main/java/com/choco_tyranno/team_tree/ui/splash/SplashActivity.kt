package com.choco_tyranno.team_tree.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import com.choco_tyranno.team_tree.R
import com.choco_tyranno.team_tree.databinding.ActivitySplashBinding
import com.choco_tyranno.team_tree.ui.SingleToastManager
import com.choco_tyranno.team_tree.ui.SingleToaster
import com.choco_tyranno.team_tree.ui.main.MainCardActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
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
    private lateinit var binding: ActivitySplashBinding
    private lateinit var handler: Handler
    private val splashViewModel: SplashViewModel by viewModels()

    private fun createInstallStateUpdatedListenerForFlexibleUpdate(): InstallStateUpdatedListener {
        return InstallStateUpdatedListener { installState ->
            run {
                if (installState.installStatus() == InstallStatus.DOWNLOADED) showSnackBarForCompleteFlexibleUpdate()
                if (installState.installStatus() == InstallStatus.INSTALLED) {
                    startMainCardActivity()
                }
            }
        }
    }

    /*
    * note : appUpdateManager.completeUpdate() trigger install new version and restart app.
    * */
    private fun showSnackBarForCompleteFlexibleUpdate() {
        Snackbar.make(
            this@SplashActivity.findViewById(R.id.constraintLayout_splash_rootView),
            R.string.splashActivity_flexibleUpdateCompleteSnackBarMessage,
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(
                R.string.splashActivity_flexibleUpdateCompleteActionText
            ) {
                splashViewModel.setProgressContentName("Installing new version...")
                appUpdateManager.completeUpdate()
            }
            .setActionTextColor(resources.getColor(R.color.colorAccent_b, theme))
            .show()
    }

    private fun startMainCardActivity() {
        handler.postDelayed(Runnable {
            splashViewModel.setProgressContentName("Loading main UI...")
            startActivity(Intent(this@SplashActivity, MainCardActivity::class.java))
            finish()
        },300)
    }

    private fun checkUpdates() {
        splashViewModel.setProgressContentName("Checking updates...")
        handler.postDelayed(Runnable {
            val updateInfoTask: Task<AppUpdateInfo> = appUpdateManager.appUpdateInfo
            updateInfoTask.addOnSuccessListener { updateInfo ->
                if (updateInfo.updateAvailability() != UpdateAvailability.UPDATE_AVAILABLE
                    && updateInfo.updateAvailability() != UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {

                    splashViewModel.setProgressContentName("Update is clean...")
                    startMainCardActivity()
                    return@addOnSuccessListener
                }
                if (updateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    splashViewModel.setProgressContentName("New version is downloaded...")
                    showSnackBarForCompleteFlexibleUpdate()
                    return@addOnSuccessListener
                }
                var installType = -1
                if (updateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) installType =
                    AppUpdateType.FLEXIBLE
                else if (updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) installType =
                    AppUpdateType.IMMEDIATE
                if (installType == -1) {
                    splashViewModel.setProgressContentName("Update is clean...")
                    startMainCardActivity()
                    return@addOnSuccessListener
                }
                if (installType == AppUpdateType.FLEXIBLE)
                    appUpdateManager.registerListener(installStateUpdatedListenerForFlexibleUpdate)
                splashViewModel.setProgressContentName("Loading new version updates...")
                appUpdateManager.startUpdateFlowForResult(
                    updateInfo,
                    installType,
                    this@SplashActivity,
                    REQ_UPDATE
                );
            }
        },1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_UPDATE && resultCode != RESULT_OK) {
            splashViewModel.setProgressContentName("Update failed...")
            SingleToaster.makeTextLong(
                this,
                resources.getString(R.string.splashActivity_updateFailText)
            ).show()
            startMainCardActivity()
        }
    }

    /*
    * note : the method 'checkUpdates' chained to startMainCardActivity.
    * So, another checking code should be a priority.
    * */
    override fun onResume() {
        super.onResume()
        checkUpdates()
    }

    override fun onStop() {
        super.onStop()
        SingleToastManager.clear()
        appUpdateManager.unregisterListener(installStateUpdatedListenerForFlexibleUpdate)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this@SplashActivity
        setContentView(binding.root)
        binding.viewModel = splashViewModel
        handler = Handler(mainLooper)
        splashViewModel.setProgressContentName("Initializing app progress data...")
    }

    companion object {
        const val TAG = "@@Splash"
    }

}