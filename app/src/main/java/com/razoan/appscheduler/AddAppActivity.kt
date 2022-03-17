package com.razoan.appscheduler

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.razoan.appscheduler.util.Constants
import com.razoan.appscheduler.util.OpenAppReceiver
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_app)
        checkIntent()
        setDateValues()
        initListener()
    }

    @SuppressLint("SimpleDateFormat")
    private fun setDateValues() {
        val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        tvDate.text = df.format(cal.time)
        tvTime.text = SimpleDateFormat("hh:mm aa").format(cal.time)
    }

    private fun checkIntent() {
        if (intent != null) {
            if (intent.getStringExtra(Constants.appName) != null && intent.getStringExtra(Constants.appPackageName) != null) {
                appName = intent.getStringExtra(Constants.appName)
                appPackageName = intent.getStringExtra(Constants.appPackageName)
                setAppInfo(appName, appPackageName)
            }
        }
    }

    private fun setAppInfo(appName: String?, appPackageName: String?) {
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

    @SuppressLint("SimpleDateFormat")
    private fun initListener() {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            tvTime.text = SimpleDateFormat("hh:mm aa").format(cal.time)
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
            val alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(this, OpenAppReceiver::class.java).let { intent ->
                intent.putExtra(Constants.appPackageName, tvPackageName.text)
                PendingIntent.getBroadcast(this, 0, intent, 0)
            }
            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.YEAR, yearSelected!!)
                set(Calendar.MONTH, monthSelected!!)
                set(Calendar.DAY_OF_MONTH, daySelected!!)
                set(Calendar.HOUR_OF_DAY, hourSelected!!)
                set(Calendar.MINUTE, minSelected!!)
                set(Calendar.SECOND, 0)
            }

            alarmMgr.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                alarmIntent
            )
            /*alarmMgr.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                1000 * 60 * 60 * 24,
                alarmIntent
            )*/
        }
    }
}