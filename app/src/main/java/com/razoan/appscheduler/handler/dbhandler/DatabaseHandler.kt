package com.razoan.appscheduler.handler.dbhandler

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import com.razoan.appscheduler.handler.dbhandler.DataBaseBaseClass.Companion.KEY_DATE_TIME
import com.razoan.appscheduler.handler.dbhandler.DataBaseBaseClass.Companion.KEY_DAY
import com.razoan.appscheduler.handler.dbhandler.DataBaseBaseClass.Companion.KEY_HOUR
import com.razoan.appscheduler.handler.dbhandler.DataBaseBaseClass.Companion.KEY_ID
import com.razoan.appscheduler.handler.dbhandler.DataBaseBaseClass.Companion.KEY_IS_EXECUTED
import com.razoan.appscheduler.handler.dbhandler.DataBaseBaseClass.Companion.KEY_IS_REPEATABLE
import com.razoan.appscheduler.handler.dbhandler.DataBaseBaseClass.Companion.KEY_MINUTE
import com.razoan.appscheduler.handler.dbhandler.DataBaseBaseClass.Companion.KEY_MONTH
import com.razoan.appscheduler.handler.dbhandler.DataBaseBaseClass.Companion.KEY_NAME
import com.razoan.appscheduler.handler.dbhandler.DataBaseBaseClass.Companion.KEY_NOTE
import com.razoan.appscheduler.handler.dbhandler.DataBaseBaseClass.Companion.KEY_PACKAGE_NAME
import com.razoan.appscheduler.handler.dbhandler.DataBaseBaseClass.Companion.KEY_SECOND
import com.razoan.appscheduler.handler.dbhandler.DataBaseBaseClass.Companion.KEY_YEAR
import com.razoan.appscheduler.handler.dbhandler.DataBaseBaseClass.Companion.TABLE_SCHEDULER
import com.razoan.appscheduler.handler.dbhandler.DataBaseBaseClass.Companion.TABLE_SCHEDULER_HISTORY
import com.razoan.appscheduler.model.AppSelectionModel
import com.razoan.appscheduler.util.Constants
import com.razoan.appscheduler.handler.receiverhandler.OpenAppReceiver
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.collections.ArrayList

class DatabaseHandler(context: Context) {

    private var mDatabase: SQLiteDatabase? = null
    private var mDbHelper: DataBaseBaseClass? = DataBaseBaseClass(context)

    @Throws(SQLException::class)
    fun openWritable(): SQLiteDatabase? {
        mDatabase = mDbHelper?.writableDatabase
        return mDatabase
    }

    @Throws(SQLException::class)
    fun openReadable(): SQLiteDatabase? {
        mDatabase = mDbHelper?.readableDatabase
        return mDatabase
    }

    fun close() {
        mDbHelper?.close()
    }

    fun addApp(app: AppSelectionModel, context: Context): Long? {
        val db = openWritable()
        val contentValues = getContentValues(app)
        val success = db?.insert(TABLE_SCHEDULER, null, contentValues)
        db?.close()
        setLatestScheduledApp(context)
        return success
    }

    fun viewScheduledApp(): ArrayList<AppSelectionModel> {
        val selectQuery = "SELECT  * FROM $TABLE_SCHEDULER"
        return getList(selectQuery)
    }

    fun updateSchedule(app: AppSelectionModel, context: Context): Int? {
        val db = openWritable()
        val contentValues = getContentValues(app)
        val success = db?.update(TABLE_SCHEDULER, contentValues, KEY_ID + "=" + app.id, null)
        db?.close()
        setLatestScheduledApp(context)
        return success
    }

    fun deleteSchedule(id: Int?, context: Context): Int? {
        val db = openWritable()
        val success = db?.delete(TABLE_SCHEDULER, "$KEY_ID=$id", null)
        db?.close()
        setLatestScheduledApp(context)
        return success
    }

    fun deleteAll(apps: ArrayList<AppSelectionModel>?): Int? {
        val db = openWritable()
        var success: Int? = -1
        for(i in 0 until apps?.size!!) {
          success = db?.delete(TABLE_SCHEDULER, "$KEY_ID=${apps[i].id}", null)
        }
        db?.close()
        return success
    }

    fun addAppHistory(app: AppSelectionModel): Long? {
        val db = openWritable()
        val contentValues = getContentValues(app)
        val success = db?.insert(TABLE_SCHEDULER_HISTORY, null, contentValues)
        db?.close()
        return success
    }

    fun viewScheduledAppHistory(): ArrayList<AppSelectionModel> {
        val selectQuery = "SELECT  * FROM $TABLE_SCHEDULER_HISTORY"
        return getList(selectQuery)
    }

    fun deleteHistory(id: Int?): Int? {
        val db = openWritable()
        var success: Int? = -1
        success = db?.delete(TABLE_SCHEDULER_HISTORY, "$KEY_ID=${id}", null)
        db?.close()
        return success
    }

    fun deleteAllHistory(apps: ArrayList<AppSelectionModel>?): Int? {
        val db = openWritable()
        var success: Int? = -1
        for(i in 0 until apps?.size!!) {
            success = db?.delete(TABLE_SCHEDULER_HISTORY, "$KEY_ID=${apps[i].id}", null)
        }
        db?.close()
        return success
    }

    fun setLatestScheduledApp(context: Context) {
        val latestAppToOpen = getLatest()
        if (latestAppToOpen.size > 0) {
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
        }
    }

    fun getLatest(): ArrayList<AppSelectionModel> {
        val selectQuery =
            "SELECT  * FROM $TABLE_SCHEDULER WHERE $KEY_IS_EXECUTED = '0' ORDER BY $KEY_DATE_TIME ASC LIMIT 1"
        return getList(selectQuery)
    }



    @SuppressLint("Range")
    private fun getList(selectQuery: String): ArrayList<AppSelectionModel> {
        val app = ArrayList<AppSelectionModel>()
        var cursor: Cursor? = null
        val db = openReadable()
        try {
            cursor = db?.rawQuery(selectQuery, null)

        } catch (e: SQLiteException) {
            db?.execSQL(selectQuery)
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
        var second: String
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
                second = cursor.getString(cursor.getColumnIndex(KEY_SECOND))
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
                    second = second,
                    isRepeatable = isRepeatable,
                    isExecuted = isExecuted
                )
                app.add(appSelected)

            } while (cursor.moveToNext())
        }
        cursor?.close()
        return app

    }

    private fun getContentValues(app: AppSelectionModel): ContentValues {
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
        contentValues.put(KEY_SECOND, app.second)
        contentValues.put(KEY_IS_REPEATABLE, app.isRepeatable)
        contentValues.put(KEY_IS_EXECUTED, app.isExecuted)
        return contentValues
    }
}