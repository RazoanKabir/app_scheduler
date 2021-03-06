package com.razoan.appscheduler.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.razoan.appscheduler.R
import com.razoan.appscheduler.adapter.AppListAdapter
import com.razoan.appscheduler.model.PackageAppInfo
import com.razoan.appscheduler.util.Constants
import com.razoan.appscheduler.util.UtilClass
import com.razoan.appscheduler.viewmodel.AppListViewModel
import kotlinx.android.synthetic.main.activity_app_list.*

class AppListActivity : AppCompatActivity(), AppListAdapter.SelectedApp {
    private var appListAdapter: AppListAdapter? = null
    private var appListVm: AppListViewModel? = null
    private var from: String? = ""
    private var appId: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_list)
        getIntentData()
        initView()
        setObserver()
        initListener()
    }

    private fun initView() {
        appListVm = ViewModelProvider(this)[AppListViewModel::class.java]
        appListVm?.execute(this)
    }

    private fun getIntentData() {
        if (intent != null) {
            if (intent.getStringExtra(Constants.from) != null) {
                from = intent.getStringExtra(Constants.from)
            }
            if (intent.getStringExtra(Constants.appId) != null) {
                appId = intent.getStringExtra(Constants.appId)
            }
        }
    }

    private fun initListener() {
        ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setObserver() {
        appListVm?.appsListLiveData?.observe(this, androidx.lifecycle.Observer {
            if (it.size > 0) {
                rvAppList?.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                rvAppList?.setHasFixedSize(true)
                rvAppList?.isNestedScrollingEnabled = false
                appListAdapter = AppListAdapter(it, this)
                rvAppList?.adapter = appListAdapter
                pBar.visibility = View.GONE
                rlAppList.visibility = View.VISIBLE
                appBarLayout.visibility = View.VISIBLE
            }
        })
    }

    override fun appSelected(selectedApp: PackageAppInfo) {
        val bundle = Bundle()
        bundle.putString(Constants.appName, selectedApp.appName)
        bundle.putString(Constants.appPackageName, selectedApp.packageName)
        bundle.putString(Constants.appPackageName, selectedApp.packageName)
        bundle.putString(Constants.from, from)
        bundle.putString(Constants.appId, appId)
        UtilClass.goToPreviousActivityWithBundle(this, bundle, AddAppActivity::class.java)
    }
}