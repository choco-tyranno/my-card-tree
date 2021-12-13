package com.choco_tyranno.team_tree.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.choco_tyranno.team_tree.R
import com.choco_tyranno.team_tree.presentation.main.MainCardActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity constructor(
    private val REQ_UPDATE: Int = 1001,
    private val updateClean: AtomicBoolean = AtomicBoolean(false),
    private val essentialPermissionClean: AtomicBoolean = AtomicBoolean(false),
    private val manualPermissionIntented: AtomicBoolean = AtomicBoolean(false)
) : AppCompatActivity() {
    @Inject
    lateinit var appUpdateManager: AppUpdateManager
    private val installStateUpdatedListenerForFlexibleUpdate: InstallStateUpdatedListener =
        createInstallStateUpdatedListenerForFlexibleUpdate()
    private val activityResultLauncherForPermission: ActivityResultLauncher<String> =
        createActivityResultLauncherForPermission()

    private fun createActivityResultLauncherForPermission(): ActivityResultLauncher<String> {
        return registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                setEssentialPermissionsCleanAndStartMainActivity()
            }
        }
    }

    private fun checkEssentialPermissions() {
        when {
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED -> {
                setEssentialPermissionsCleanAndStartMainActivity()
                return
            }
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                showManualPermissionRequestDialog()
            }
            else -> {
                activityResultLauncherForPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun showManualPermissionRequestDialog() {
        val manualPermissionRequestDialogBuilder =
            AlertDialog.Builder(this@SplashActivity)
                .setTitle(R.string.splashActivity_storagePermissionRequestDialogTitle)
                .setMessage(R.string.splashActivity_storagePermissionRequestDialogMessage)
                .setCancelable(false)
                .setPositiveButton(R.string.splashActivity_storagePermissionRequestDialogPositiveButtonText) { dialog, id ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.setData(uri)
                    startActivity(intent)
                    manualPermissionIntented.set(true)
                    dialog.dismiss()
                }
        val permissionAlertDialog = manualPermissionRequestDialogBuilder.create()
        permissionAlertDialog.show()
        permissionAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(resources.getColor(R.color.colorAccent_c, theme))
    }

    private fun showEssentialPermissionDeniedDialog() {
        val permissionDeniedDialogBuilder =
            AlertDialog.Builder(this@SplashActivity)
                .setTitle(R.string.splashActivity_storagePermissionRequestDialogDeniedTitle)
                .setMessage(R.string.splashActivity_storagePermissionRequestDialogDeniedMessage)
                .setCancelable(false)
                .setPositiveButton(R.string.splashActivity_storagePermissionRequestDialogDeniedPositiveButtonText) { dialog, id ->
                    finish()
                }
        val permissionDeniedAlertDialog = permissionDeniedDialogBuilder.create()
        permissionDeniedAlertDialog.show()
        permissionDeniedAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(resources.getColor(R.color.defaultTextColor, theme))
    }

    private fun createInstallStateUpdatedListenerForFlexibleUpdate(): InstallStateUpdatedListener {
        return InstallStateUpdatedListener { installState ->
            run {
                if (installState.installStatus() == InstallStatus.DOWNLOADED) showSnackBarForCompleteFlexibleUpdate()
                if (installState.installStatus() == InstallStatus.INSTALLED) {
                    setUpdateCleanAndCallCheckingEssentialPermissions()
                    appUpdateManager.unregisterListener(installStateUpdatedListenerForFlexibleUpdate)
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
            ) { appUpdateManager.completeUpdate() }
            .setActionTextColor(resources.getColor(R.color.colorAccent_b, theme))
            .show()
    }

    private fun setUpdateCleanAndCallCheckingEssentialPermissions() {
        if (!updateClean.get())
            updateClean.set(true)
        checkEssentialPermissions()
    }

    private fun setEssentialPermissionsCleanAndStartMainActivity() {
        essentialPermissionClean.set(true)
        startMainCardActivity()
    }

    private fun startMainCardActivity() {
        if (essentialPermissionClean.get() && updateClean.get()) {
            startActivity(Intent(this@SplashActivity, MainCardActivity::class.java))
            finish()
        }
    }

    private fun checkUpdates() {
        val updateInfoTask: Task<AppUpdateInfo> = appUpdateManager.appUpdateInfo
        updateInfoTask.addOnSuccessListener { updateInfo ->
            if (updateInfo.updateAvailability() != UpdateAvailability.UPDATE_AVAILABLE
                && updateInfo.updateAvailability() != UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
            ) {
                setUpdateCleanAndCallCheckingEssentialPermissions()
                return@addOnSuccessListener
            }
            if (updateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                showSnackBarForCompleteFlexibleUpdate()
                return@addOnSuccessListener
            }
            var installType = -1
            if (updateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) installType =
                AppUpdateType.FLEXIBLE
            else if (updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) installType =
                AppUpdateType.IMMEDIATE
            if (installType == -1) {
                setUpdateCleanAndCallCheckingEssentialPermissions()
                return@addOnSuccessListener
            }
            if (installType == AppUpdateType.FLEXIBLE)
                appUpdateManager.registerListener(installStateUpdatedListenerForFlexibleUpdate)
            appUpdateManager.startUpdateFlowForResult(
                updateInfo,
                installType,
                this@SplashActivity,
                REQ_UPDATE
            );
        }
    }

    private fun checkManualPermissionIntented(): Boolean {
        if (manualPermissionIntented.get()
            && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
        ) {
            showEssentialPermissionDeniedDialog()
            return true
        } else if (manualPermissionIntented.get()
            && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        ) {
            manualPermissionIntented.set(false)
            setEssentialPermissionsCleanAndStartMainActivity()
            return true
        }
        if (manualPermissionIntented.get())
            manualPermissionIntented.set(false)
        return false
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_UPDATE && resultCode != RESULT_OK) {
            SingleToaster.makeTextLong(this, resources.getString(R.string.splashActivity_updateFailText)).show()
        }
    }

    /*
    * note : the method 'checkUpdates' chained to startMainCardActivity.
    * So, another checking code should be a priority.
    * */
    override fun onResume() {
        super.onResume()
        if (!checkManualPermissionIntented())
            checkUpdates()
    }

    override fun onStop() {
        super.onStop()
        SingleToastManager.clear()
        appUpdateManager.unregisterListener(installStateUpdatedListenerForFlexibleUpdate)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    companion object {
        const val TAG = "@@Splash"
    }

}