package com.razoan.appscheduler.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import android.widget.RelativeLayout
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.razoan.appscheduler.R
import com.razoan.appscheduler.adapter.ScheduledAppListAdapter
import com.razoan.appscheduler.handler.DatabaseHandler
import com.razoan.appscheduler.model.AppSelectionModel
import com.razoan.appscheduler.util.UtilClass
import com.razoan.appscheduler.util.ViewDialog

class MainViewModel : ViewModel() {
    fun deleteAll(context: Context) {
        val selectedAppList = getAppList(context)
        DatabaseHandler(context).deleteAll(selectedAppList)
    }

    fun deleteAllHistory(context: Context) {
        val appListHistory = DatabaseHandler(context).viewScheduledAppHistory()
        if(appListHistory.size==0) {
            UtilClass.showToast(context, context.getString(R.string.noHistoryFound))
            return
        }
        val status = DatabaseHandler(context).deleteAllHistory(appListHistory)
        if (status != null) {
            if(status > -1) UtilClass.showToast(context, context.getString(R.string.successfullyDeletedAllHistory))
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

    @SuppressLint("BatteryLife")
    fun checkBattery(context: Context) {
        val intent = Intent()
        val packageName: String = context.packageName
        val pm = context.getSystemService(POWER_SERVICE) as PowerManager?
        if (!pm!!.isIgnoringBatteryOptimizations(packageName)) {
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:$packageName")
            context.startActivity(intent)
        }
    }
}