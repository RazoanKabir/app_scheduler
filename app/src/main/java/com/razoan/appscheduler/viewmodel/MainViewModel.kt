package com.razoan.appscheduler.viewmodel

import android.content.Context
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
    fun deleteAll(context: Context) {
        val selectedAppList = getAppList(context)
        if(selectedAppList.size > 0) {
            for(i in 0 until selectedAppList.size) {
                DatabaseHandler(context).deleteSchedule(selectedAppList[i].id)
            }
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