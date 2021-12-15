package com.choco_tyranno.team_tree.ui.detail_page.view

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.choco_tyranno.team_tree.R
import com.choco_tyranno.team_tree.domain.card_data.CardDto
import com.choco_tyranno.team_tree.ui.SingleToaster
import com.choco_tyranno.team_tree.ui.card_rv.ContactCardViewHolder
import com.choco_tyranno.team_tree.ui.detail_page.DetailCardActivity
import com.choco_tyranno.team_tree.ui.detail_page.DetailPage
import com.choco_tyranno.team_tree.ui.main.MainCardActivity
import com.choco_tyranno.team_tree.ui.util.AccessDeniedAnimationProvider
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.scopes.ActivityScoped
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FloatingActionButton(context, attrs) {
    init {
        setOnClickListener(OnClickListenerForCameraBtn.getInstance())
    }
    private class OnClickListenerForCameraBtn private constructor() : OnClickListener {
        override fun onClick(v: View) = startCameraActivity(v)
        private fun createImageFile(context: Context, pageState: DetailPage): File {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "JPEG_" + timeStamp + "_"
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
            )
            pageState.photoPath = image.absolutePath
            return image
        }
        private fun startCameraActivity(v: View) {
            val context = v.context
            val resources = v.resources
            val detailActivity = context as DetailCardActivity
            if (!context.applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                v.startAnimation(AccessDeniedAnimationProvider.getInstance(context))
                SingleToaster.makeTextShort(
                    context,
                    resources.getString(R.string.detailActivity_cameraNotFoundMessage)
                ).show()
                return
            }
            val alertDialog =
                AlertDialog.Builder(context)
                    .setTitle(R.string.detailActivity_cameraAlertDialogTitle)
                    .setMessage(R.string.detailActivity_cameraAlertDialogMessage)
                    .setCancelable(true)
                    .setPositiveButton(R.string.default_positiveText) { dialog, id ->
                        val binding = detailActivity.binding
                        val detailPage = binding.detailPage ?: return@setPositiveButton
                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        if (takePictureIntent.resolveActivity(detailActivity.packageManager) != null) {
                            val imageFile = createImageFile(context, detailPage)
                            val photoUri = FileProvider.getUriForFile(
                                detailActivity.applicationContext,
                                detailActivity.packageName,
                                imageFile
                            )
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                            detailActivity.activityResultLauncherForIntentCamera.launch(
                                takePictureIntent
                            )
                        } else {
                            SingleToaster.makeTextShort(context, resources.getString(R.string.detailActivity_takePictureIntentErrorText)).show()
                        }
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.default_negativeText) { dialog, id ->
                        dialog.dismiss()
                    }.create()
            alertDialog.show()
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(resources.getColor(R.color.colorAccent_c, context.theme))
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(resources.getColor(R.color.defaultTextColor, context.theme))
            detailActivity.detailFab.animateFab(detailActivity.binding)
        }
        companion object {
            private val instance: OnClickListenerForCameraBtn = OnClickListenerForCameraBtn()
            @JvmStatic
            fun getInstance() = instance
        }
    }
}