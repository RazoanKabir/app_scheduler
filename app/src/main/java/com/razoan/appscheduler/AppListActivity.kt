package com.razoan.appscheduler

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.razoan.appscheduler.adapter.AppListAdapter
import com.razoan.appscheduler.model.PackageAppInfo
import kotlinx.android.synthetic.main.activity_app_list.*
import java.util.*
import android.content.pm.ApplicationInfo
import android.util.Log
import android.content.pm.PackageManager





class AppListActivity : AppCompatActivity() {
    private var appListAdapter: AppListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_list)
        val apps: ArrayList<PackageAppInfo> = getPackages()
        if(apps.size > 0) {
            rvAppList?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            rvAppList?.setHasFixedSize(true)
            rvAppList?.isNestedScrollingEnabled = false
            appListAdapter = AppListAdapter(apps)
            rvAppList?.adapter = appListAdapter
            for(i in 0 until apps.size)
            println("APPNAME: ${apps[i].appName}")
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
                val currAppName = pm.getApplicationLabel(packageInfo).toString()
                val newInfo = PackageAppInfo()
                newInfo.appName = pm.getApplicationLabel(packageInfo).toString()
                newInfo.packageName = packageInfo.packageName
                newInfo.icon = pm.getApplicationIcon(packageInfo)
                list.add(newInfo)
            }
        }
        return list
    }
}