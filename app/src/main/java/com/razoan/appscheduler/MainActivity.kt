package com.razoan.appscheduler

import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.razoan.appscheduler.model.AppSelectionModel
import com.razoan.appscheduler.util.UtilClass
import com.razoan.appscheduler.util.ViewDialog
import com.razoan.appscheduler.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ViewDialog.DeletedApp {
    private var toolbarMenu: Menu? = null
    private var menuOption: MenuItem? = null
    private var selectedAppList: ArrayList<AppSelectionModel> = ArrayList()
    private var mainVM: MainViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)
        initActionBar()
        initView()
        checkOverlayPermission()
        initListener()
    }

    private fun initView() {
        mainVM = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private fun initActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setDisplayShowHomeEnabled(false)
    }

    override fun onResume() {
        super.onResume()
        checkOverlayPermission()
    }

    private fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(this))
            mainVM?.setOverlay(this, packageName)
        else
            mainVM?.viewAppList(this, rvAppList, rlAppList, rlEmptyView, this)

    }

    private fun initListener() {
        fabNewApp.setOnClickListener {
            UtilClass.goToNextActivity(this, AddAppActivity::class.java)
        }
        srDashboard.setOnRefreshListener {
            checkOverlayPermission()
            srDashboard.isRefreshing = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.app_scheduler_menu, menu)
        toolbarMenu = menu
        menuOption = menu.findItem(R.id.app_scheduler_menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteAll -> {
                deleteAllSchedules()
            }

            R.id.appHistory -> {
                UtilClass.goToNextActivity(this, AppHistoryActivity::class.java)
            }
        }
        return true
    }

    private fun deleteAllSchedules() {
        if (selectedAppList.size > 0) mainVM?.deleteAll(this, selectedAppList)
        checkOverlayPermission()
    }

    override fun appDeleted(isDeleted: Boolean) {
        if (isDeleted) checkOverlayPermission()
    }
}