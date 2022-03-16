package com.razoan.appscheduler

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.razoan.appscheduler.adapter.AppListAdapter
import com.razoan.appscheduler.model.PackageAppInfo
import com.razoan.appscheduler.util.Constants
import com.razoan.appscheduler.util.UtilClass
import kotlinx.android.synthetic.main.activity_app_list.*
import java.util.*


class AppListActivity : AppCompatActivity(), AppListAdapter.SelectedApp {
    private var appListAdapter: AppListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_list)
        val apps: ArrayList<PackageAppInfo> = getPackages()
        if(apps.size > 0) {
            rvAppList?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            rvAppList?.setHasFixedSize(true)
            rvAppList?.isNestedScrollingEnabled = false
            appListAdapter = AppListAdapter(apps, this)
            rvAppList?.adapter = appListAdapter
        }
    }

    private fun getPackages(): ArrayList<PackageAppInfo> {
        val apps: ArrayList<PackageAppInfo> = getInstalledApps()
        val max = apps.size
        for (i in 0 until max) {
            apps[i]
        }
        return apps
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun getInstalledApps(): ArrayList<PackageAppInfo> {

        val pm = packageManager
        val list: ArrayList<PackageAppInfo> = ArrayList<PackageAppInfo>()
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        for (packageInfo in packages) {
            if (pm.getLaunchIntentForPackage(packageInfo.packageName) != null) {
                val newInfo = PackageAppInfo()
                newInfo.appName = pm.getApplicationLabel(packageInfo).toString()
                newInfo.packageName = packageInfo.packageName
                newInfo.icon = pm.getApplicationIcon(packageInfo)
                list.add(newInfo)
            }
        }
        return list
    }

    override fun appSelected(selectedApp: PackageAppInfo) {
        val bundle = Bundle()
        bundle.putString(Constants.appName, selectedApp.appName)
        bundle.putString(Constants.appPackageName, selectedApp.packageName)
        UtilClass.goToPreviousActivityWithBundle(this, bundle, AddAppActivity::class.java)
    }
}