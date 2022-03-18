package com.razoan.appscheduler

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.razoan.appscheduler.util.UtilClass

class StartingActivity : AppCompatActivity() {
    private var dialog: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        installSplashScreen()
        checkOverlayPermission()
    }

    override fun onResume() {
        super.onResume()
        if (Settings.canDrawOverlays(this)) {
            proceed()
        } else if (dialog?.isShowing == false)
            checkOverlayPermission()
    }

    private fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this)) {
            showOverlayDialog()
        }
    }

    private fun proceed() {
        if (dialog?.isShowing == true)
            dialog?.dismiss()
        UtilClass.goToNextActivityByClearingHistory(this, MainActivity::class.java)
    }

    private fun showOverlayDialog() {
        dialog = Dialog(this)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.custom_delete_dialog)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val mDialogNo: FloatingActionButton? = dialog?.findViewById(R.id.fabNo)
        val mDialogOk: FloatingActionButton? = dialog?.findViewById(R.id.fabYes)
        val title: TextView? = dialog?.findViewById(R.id.tvDialogTitle)
        val subTitle: TextView? = dialog?.findViewById(R.id.tvDialogSubTitle)
        title?.text = getString(R.string.need_permission)
        subTitle?.text = getString(R.string.need_overlay_permission)
        mDialogNo?.setOnClickListener {
            UtilClass.showToast(this, getString(R.string.cantContinue))
        }
        mDialogOk?.setOnClickListener {
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            ).apply {
                addCategory(Intent.CATEGORY_DEFAULT)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(this)
            }
        }
        dialog?.show()
    }
}