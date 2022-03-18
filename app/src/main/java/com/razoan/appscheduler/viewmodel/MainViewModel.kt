package com.razoan.appscheduler.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.widget.RelativeLayout
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.razoan.appscheduler.adapter.ScheduledAppListAdapter
import com.razoan.appscheduler.handler.DatabaseHandler
import com.razoan.appscheduler.model.AppSelectionModel
import com.razoan.appscheduler.util.ViewDialog

class MainViewModel : ViewModel() {
    fun deleteAll(context: Context ,selectedAppList: ArrayList<AppSelectionModel>) {
        for(i in 0 until selectedAppList.size) {
            DatabaseHandler(context).deleteSchedule(selectedAppList[i].id)
        }
    }
    fun setOverlay(context: Context, packageName: String) {
        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(this)
        }
    }

    fun viewAppList(context: Context, rvAppList: RecyclerView, rlAppList: RelativeLayout ,rvEmptyView: RelativeLayout,
                    deletedApp: ViewDialog.DeletedApp) {
        val selectedAppList = getAppList(context)
        if(selectedAppList.size > 0) {
            rvAppList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            rvAppList.setHasFixedSize(true)
            rvAppList.isNestedScrollingEnabled = false
            val scheduledAppListAdapter = ScheduledAppListAdapter(selectedAppList, deletedApp)
            rvAppList.adapter = scheduledAppListAdapter
            rlAppList.visibility = View.VISIBLE
        } else {
            rlAppList.visibility = View.GONE
            rvEmptyView.visibility = View.VISIBLE
        }
    }

    private fun getAppList(context: Context): ArrayList<AppSelectionModel> {
        return DatabaseHandler(context).viewScheduledApp()
    }
}