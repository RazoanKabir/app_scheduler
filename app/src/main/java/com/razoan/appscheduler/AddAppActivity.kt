package com.razoan.appscheduler

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.gson.Gson
import com.razoan.appscheduler.handler.DatabaseHandler
import com.razoan.appscheduler.model.AppSelectionModel
import com.razoan.appscheduler.util.Constants
import com.razoan.appscheduler.util.UtilClass
import kotlinx.android.synthetic.main.activity_add_app.*
import java.text.SimpleDateFormat
import java.util.*

class AddAppActivity : AppCompatActivity() {
    private var appName: String? = null
    private var appPackageName: String? = null
    private var appIcon: Drawable? = null
    private var cal = Calendar.getInstance()
    private var minSelected: Int? = 0
    private var hourSelected: Int? = 0
    private var daySelected: Int? = 0
    private var monthSelected: Int? = 0
    private var yearSelected: Int? = 0
    private var app: AppSelectionModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_app)
        setDateValues()
        checkIntent()
        initListener()
    }

    @SuppressLint("SimpleDateFormat")
    private fun setDateValues() {
        val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        tvDate.text = df.format(cal.time)
        tvTime.text = SimpleDateFormat("hh:mm aa").format(cal.time)
        yearSelected = cal.get(Calendar.YEAR)
        monthSelected = cal.get(Calendar.MONTH)
        daySelected = cal.get(Calendar.DAY_OF_MONTH)
        hourSelected = cal.get(Calendar.HOUR_OF_DAY)
        minSelected = cal.get(Calendar.MINUTE)
    }

    private fun checkIntent() {
        if (intent != null) {
            if (intent.getStringExtra(Constants.appName) != null && intent.getStringExtra(Constants.appPackageName) != null) {
                appName = intent.getStringExtra(Constants.appName)
                appPackageName = intent.getStringExtra(Constants.appPackageName)
                setAppInfoFromSelection(appName, appPackageName)
            }
            if(intent.getStringExtra(Constants.appToJSON) != null) {
                app = Gson().fromJson<AppSelectionModel>(intent?.getStringExtra(Constants.appToJSON), AppSelectionModel::class.java)
                setAppInfoForEdit(app)
            }
        }
    }

    private fun setAppInfoFromSelection(appName: String?, appPackageName: String?) {
        rlGoToAppList.visibility = View.GONE
        try {
            appIcon = this.packageManager.getApplicationIcon(appPackageName.toString())
            ivSelectedAppIcon.setImageDrawable(appIcon)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        tvAppName.text = appName
        tvPackageName.text = appPackageName
        ivScheduleIconStatus.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_baseline_alarm_on_24,
                null
            )
        )
        rlSelectApp.visibility = View.VISIBLE
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun setAppInfoForEdit(app: AppSelectionModel?) {
        setAppInfoFromSelection(app?.appName, app?.appPackageName)
        val aa = if (app?.hour?.toInt()!! > 11)  ("pm") else ("am")
        val hour = if (app.hour.toInt() > 12)  (app.hour.toInt()-12) else (app.hour)
        etNote.setText(app.note)
        tvDate.text = "${app.day}/${app.month}/${app.year}"
        tvTime.text = "${hour}:${app.minute} $aa"

        yearSelected = app.year?.toInt()
        monthSelected = app.month?.toInt()
        daySelected = app.day?.toInt()
        hourSelected = app.hour.toInt()
        minSelected = app.minute?.toInt()
    }

    @SuppressLint("SimpleDateFormat")
    private fun initListener() {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            tvTime.text = SimpleDateFormat("hh:mm:ss aa").format(cal.time)
            hourSelected = hour
            minSelected = minute
        }
        llSelectTime.setOnClickListener {
            TimePickerDialog(
                this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                false
            ).show()
        }

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "dd/MM/yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
                tvDate.text = sdf.format(cal.time)
                yearSelected = year
                monthSelected = monthOfYear
                daySelected = dayOfMonth
            }

        llSelectDate.setOnClickListener {
            cal = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this@AddAppActivity,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.minDate = cal.timeInMillis
            datePicker.show()
        }

        rlGoToAppList.setOnClickListener {
            UtilClass.goToNextActivity(this, AppListActivity::class.java)
        }

        rlSelectApp.setOnClickListener {
            UtilClass.goToNextActivity(this, AppListActivity::class.java)
        }

        btnScheduleApp.setOnClickListener {
            if(app != null) {
              updateApp()
           } else addApp()
        }

        ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun updateApp() {
        if(tvAppName.text.isNullOrEmpty() || tvPackageName.text.isNullOrEmpty()) {
            UtilClass.showToast(this, getString(R.string.pleaseSelectApp))
            return
        }
        val status = DatabaseHandler(this).updateSchedule(
            AppSelectionModel(
                app?.id,
                tvAppName.text.toString(),
                tvPackageName.text.toString(),
                etNote.text.toString(),
                "${tvDate.text} ${tvTime.text}",
                yearSelected.toString(),
                monthSelected.toString(),
                daySelected.toString(),
                hourSelected.toString(),
                minSelected.toString(),
                "0",
                "0"
            )
        )
        updateStatus(status)
    }

    private fun addApp() {
        if(tvAppName.text.isNullOrEmpty() || tvPackageName.text.isNullOrEmpty()) {
            UtilClass.showToast(this, getString(R.string.pleaseSelectApp))
            return
        }
        val status = DatabaseHandler(this).addApp(
            AppSelectionModel(
                0,
                tvAppName.text.toString(),
                tvPackageName.text.toString(),
                etNote.text.toString(),
                "${tvDate.text} ${tvTime.text}",
                yearSelected.toString(),
                monthSelected.toString(),
                daySelected.toString(),
                hourSelected.toString(),
                minSelected.toString(),
                "0",
                "0"
            )
        )
        updateStatus(status)
    }

    private fun updateStatus(status: Long) {
        if (status > -1) {
            UtilClass.showToast(this, getString(R.string.recordSaved))
            DatabaseHandler(this).setLatestScheduledApp(this)
            UtilClass.goToNextActivity(this, MainActivity::class.java)
            finish()
        } else
            UtilClass.showToast(this, getString(R.string.canNotSavedRecord))
    }

    private fun updateStatus(status: Int) {
        if (status > -1) {
            UtilClass.showToast(this, getString(R.string.recordUpdated))
            DatabaseHandler(this).setLatestScheduledApp(this)
            UtilClass.goToNextActivity(this, MainActivity::class.java)
            finish()
        } else
            UtilClass.showToast(this, getString(R.string.canNotUpdateRecord))
    }
}