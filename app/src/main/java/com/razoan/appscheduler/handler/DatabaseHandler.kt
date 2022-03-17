package com.razoan.appscheduler.handler

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.razoan.appscheduler.model.AppSelectionModel
import com.razoan.appscheduler.util.Constants
import com.razoan.appscheduler.util.OpenAppReceiver
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.collections.ArrayList

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private var DATABASE_VERSION = 1
        private var DATABASE_NAME = "AppSchedulerDB"
        private var TABLE_SCHEDULER = "AppSchedulerTable"
        private var KEY_ID = "_id"
        private var KEY_NAME = "app_name"
        private var KEY_PACKAGE_NAME = "package_name"
        private var KEY_NOTE = "note"
        private var KEY_DATE_TIME = "date_time"
        private var KEY_YEAR = "year"
        private var KEY_MONTH = "month"
        private var KEY_DAY = "day"
        private var KEY_HOUR = "hour"
        private var KEY_MINUTE = "minute"
        private var KEY_IS_REPEATABLE = "is_repeatable"
        private var KEY_IS_EXECUTED = "is_executed"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        //creating table with fields
        val CREATE_APP_SCHEDULER_TABLE = ("CREATE TABLE " + TABLE_SCHEDULER + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " TEXT,"
                + KEY_PACKAGE_NAME + " TEXT,"
                + KEY_NOTE + " TEXT,"
                + KEY_DATE_TIME + " TEXT,"
                + KEY_YEAR + " TEXT,"
                + KEY_MONTH + " TEXT,"
                + KEY_DAY + " TEXT,"
                + KEY_HOUR + " TEXT,"
                + KEY_MINUTE + " TEXT,"
                + KEY_IS_REPEATABLE + " TEXT,"
                + KEY_IS_EXECUTED + " TEXT"
                + ")")
        db?.execSQL(CREATE_APP_SCHEDULER_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_SCHEDULER")
        onCreate(db)
    }

    fun addApp(app: AppSelectionModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(KEY_NAME, app.appName)
        contentValues.put(KEY_PACKAGE_NAME, app.appPackageName)
        contentValues.put(KEY_NOTE, app.note)
        contentValues.put(KEY_DATE_TIME, app.dateTime)
        contentValues.put(KEY_YEAR, app.year)
        contentValues.put(KEY_MONTH, app.month)
        contentValues.put(KEY_DAY, app.day)
        contentValues.put(KEY_HOUR, app.hour)
        contentValues.put(KEY_MINUTE, app.minute)
        contentValues.put(KEY_IS_REPEATABLE, app.isRepeatable)
        contentValues.put(KEY_IS_EXECUTED, app.isExecuted)

        val success = db.insert(TABLE_SCHEDULER, null, contentValues)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun viewScheduledApp(): ArrayList<AppSelectionModel> {

        val selectedAppList: ArrayList<AppSelectionModel> = ArrayList<AppSelectionModel>()

        // Query to select all the records from the table.
        val selectQuery = "SELECT  * FROM $TABLE_SCHEDULER"

        val db = this.readableDatabase
        // Cursor is used to read the record one by one. Add them to data model class.
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var appName: String
        var appPackageName: String
        var note: String
        var dateTime: String
        var year: String
        var month: String
        var day: String
        var hour: String
        var minute: String
        var isRepeatable: String
        var isExecuted: String

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                appName = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                appPackageName = cursor.getString(cursor.getColumnIndex(KEY_PACKAGE_NAME))
                note = cursor.getString(cursor.getColumnIndex(KEY_NOTE))
                dateTime = cursor.getString(cursor.getColumnIndex(KEY_DATE_TIME))
                year = cursor.getString(cursor.getColumnIndex(KEY_YEAR))
                month = cursor.getString(cursor.getColumnIndex(KEY_MONTH))
                day = cursor.getString(cursor.getColumnIndex(KEY_DAY))
                hour = cursor.getString(cursor.getColumnIndex(KEY_HOUR))
                minute = cursor.getString(cursor.getColumnIndex(KEY_MINUTE))
                isRepeatable = cursor.getString(cursor.getColumnIndex(KEY_IS_REPEATABLE))
                isExecuted = cursor.getString(cursor.getColumnIndex(KEY_IS_EXECUTED))

                val appSelected = AppSelectionModel(
                    id = id,
                    appName = appName,
                    appPackageName = appPackageName,
                    note = note,
                    dateTime = dateTime,
                    year = year,
                    month = month,
                    day = day,
                    hour = hour,
                    minute = minute,
                    isRepeatable = isRepeatable,
                    isExecuted = isExecuted
                )
                selectedAppList.add(appSelected)

            } while (cursor.moveToNext())
        }
        return selectedAppList
    }

    fun updateSchedule(app: AppSelectionModel): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, app.appName)
        contentValues.put(KEY_PACKAGE_NAME, app.appPackageName)
        contentValues.put(KEY_NOTE, app.note)
        contentValues.put(KEY_DATE_TIME, app.dateTime)
        contentValues.put(KEY_YEAR, app.year)
        contentValues.put(KEY_MONTH, app.month)
        contentValues.put(KEY_DAY, app.day)
        contentValues.put(KEY_HOUR, app.hour)
        contentValues.put(KEY_MINUTE, app.minute)
        contentValues.put(KEY_IS_REPEATABLE, app.isRepeatable)
        contentValues.put(KEY_IS_EXECUTED, app.isExecuted)

        val success = db.update(TABLE_SCHEDULER, contentValues, KEY_ID + "=" + app.id, null)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun setLatestScheduledApp(context: Context) {

        val latestAppToOpen = ArrayList<AppSelectionModel>()

        // Query to select all the records from the table.
        val selectQuery = "SELECT  * FROM $TABLE_SCHEDULER WHERE $KEY_IS_EXECUTED = '0' ORDER BY $KEY_DATE_TIME ASC LIMIT 1"

        val db = this.readableDatabase
        // Cursor is used to read the record one by one. Add them to data model class.
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
        }

        var id: Int
        var appName: String
        var appPackageName: String
        var note: String
        var dateTime: String
        var year: String
        var month: String
        var day: String
        var hour: String
        var minute: String
        var isRepeatable: String
        var isExecuted: String

        if (cursor?.moveToFirst() == true) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                appName = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                appPackageName = cursor.getString(cursor.getColumnIndex(KEY_PACKAGE_NAME))
                note = cursor.getString(cursor.getColumnIndex(KEY_NOTE))
                dateTime = cursor.getString(cursor.getColumnIndex(KEY_DATE_TIME))
                year = cursor.getString(cursor.getColumnIndex(KEY_YEAR))
                month = cursor.getString(cursor.getColumnIndex(KEY_MONTH))
                day = cursor.getString(cursor.getColumnIndex(KEY_DAY))
                hour = cursor.getString(cursor.getColumnIndex(KEY_HOUR))
                minute = cursor.getString(cursor.getColumnIndex(KEY_MINUTE))
                isRepeatable = cursor.getString(cursor.getColumnIndex(KEY_IS_REPEATABLE))
                isExecuted = cursor.getString(cursor.getColumnIndex(KEY_IS_EXECUTED))

                val appSelected = AppSelectionModel(
                    id = id,
                    appName = appName,
                    appPackageName = appPackageName,
                    note = note,
                    dateTime = dateTime,
                    year = year,
                    month = month,
                    day = day,
                    hour = hour,
                    minute = minute,
                    isRepeatable = isRepeatable,
                    isExecuted = isExecuted
                )
                latestAppToOpen.add(appSelected)

            } while (cursor.moveToNext())
        }
        /*Log.d("latestAppName", latestAppToOpen[0].appName!!)
        Log.d("latestPack", latestAppToOpen[0].appPackageName!!)
        Log.d("latestDate", latestAppToOpen[0].dateTime!!)
        Log.d("latestPack", latestAppToOpen.size.toString())*/
        if(latestAppToOpen.size > 0) {
            val appToJson = Json.encodeToString(AppSelectionModel.serializer(), latestAppToOpen[0])
            val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, OpenAppReceiver::class.java).let { intent ->
                intent.putExtra(Constants.appToJSON, appToJson)
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }
            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                latestAppToOpen[0].year?.let { it1 -> set(Calendar.YEAR, it1.toInt()) }
                latestAppToOpen[0].month?.let { it1 -> set(Calendar.MONTH, it1.toInt()) }
                latestAppToOpen[0].day?.let { it1 -> set(Calendar.DAY_OF_MONTH, it1.toInt()) }
                latestAppToOpen[0].hour?.toInt()?.let { it1 -> set(Calendar.HOUR_OF_DAY, it1) }
                latestAppToOpen[0].minute?.toInt()?.let { it1 -> set(Calendar.MINUTE, it1) }
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