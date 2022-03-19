package com.razoan.appscheduler

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.razoan.appscheduler.util.UtilClass
import com.razoan.appscheduler.util.ViewDialog
import com.razoan.appscheduler.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ViewDialog.DeletedApp {
    private var toolbarMenu: Menu? = null
    private var menuOption: MenuItem? = null
    private var mainVM: MainViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initActionBar()
        initView()
        checkBatteryOptimization()
        updateUI()
        initListener()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun initActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setDisplayShowHomeEnabled(false)
    }

    private fun initView() {
        mainVM = ViewModelProvider(this)[MainViewModel::class.java]
    }

    private fun checkBatteryOptimization() {
        mainVM?.checkBattery(this)
    }

    private fun updateUI() {
        mainVM?.viewAppList(this, rvAppList, rlAppList, rlEmptyView, this)
    }

    private fun initListener() {
        fabNewApp.setOnClickListener {
            UtilClass.goToNextActivity(this, AddAppActivity::class.java)
        }
        srDashboard.setOnRefreshListener {
            updateUI()
            srDashboard.isRefreshing = false
        }
    }

    private fun deleteAllHistory() {
        mainVM?.deleteAllHistory(this)
    }

    private fun deleteAllSchedules() {
        mainVM?.deleteAll(this)
        updateUI()
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

            R.id.appHistoryDeleteAll -> {
                deleteAllHistory()
            }
        }
        return true
    }

    override fun appDeleted(isDeleted: Boolean) {
        if (isDeleted) updateUI()
    }
}