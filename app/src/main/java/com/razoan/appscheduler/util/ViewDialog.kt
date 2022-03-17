package com.razoan.appscheduler.util

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.FrameLayout
import com.razoan.appscheduler.R
import com.razoan.appscheduler.handler.DatabaseHandler
import com.razoan.appscheduler.model.AppSelectionModel

class ViewDialog {
    interface DeletedApp {
        fun appDeleted(isDeleted: Boolean)
    }
    fun showDialog(activity: Activity?, app: AppSelectionModel?, deletedApp: DeletedApp) {
        val dialog = activity?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.custom_delete_dialog)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val mDialogNo: FrameLayout? = dialog?.findViewById(R.id.frmNo)
        val mDialogOk: FrameLayout? = dialog?.findViewById(R.id.frmOk)
        mDialogNo?.setOnClickListener {
           dialog.cancel()
        }
        mDialogOk?.setOnClickListener {
            DatabaseHandler(activity).deleteSchedule(app)
            dialog.cancel()
            deletedApp.appDeleted(true)
        }
        dialog?.show()
    }
}