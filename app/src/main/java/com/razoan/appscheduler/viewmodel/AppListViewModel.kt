package com.razoan.appscheduler.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.razoan.appscheduler.model.PackageAppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppListViewModel : ViewModel() {
    val appsListLiveData = MutableLiveData<ArrayList<PackageAppInfo>>()
    fun execute(context: Context) = viewModelScope.launch {
        val result = doInBackground(context)
        onPostExecute(result)
    }

    private suspend fun doInBackground(context: Context): ArrayList<PackageAppInfo> =
        withContext(Dispatchers.IO) {
            val apps: ArrayList<PackageAppInfo> = getPackages(context)
            delay(1000)
            return@withContext apps
        }

    private fun onPostExecute(apps: ArrayList<PackageAppInfo>) {
        appsListLiveData.value = apps
    }

    private fun getPackages(context: Context): ArrayList<PackageAppInfo> {
        val apps: ArrayList<PackageAppInfo> = getInstalledApps(context)
        val max = apps.size
        for (i in 0 until max) {
            apps[i]
        }
        return apps
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun getInstalledApps(context: Context): ArrayList<PackageAppInfo> {
        val pm = context.packageManager
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
}