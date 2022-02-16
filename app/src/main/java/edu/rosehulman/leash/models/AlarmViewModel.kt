package edu.rosehulman.leash.models

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import edu.rosehulman.leash.Constants
import edu.rosehulman.leash.receivers.AlarmReceiver
import edu.rosehulman.leash.utils.NotificationUtils
import java.lang.Integer.parseInt
import java.util.*

class AlarmViewModel(private val app: Application) : AndroidViewModel(app) {

    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val REQUEST_CODE = 1
    val SECOND_IN_MILLIS: Long = 1_000L // timers work in milliseconds
    private var alarmYear: Int = 0
    private var alarmMonth: Int = 0
    private var alarmDay: Int = 0
    private var alarmHour: Int = 0
    private var alarmMinute: Int = 0
    private var currentYear: Int = 0
    private var currentMonth: Int = 0
    private var currentDay: Int = 0
    private var currentHour: Int = 0
    private var currentMinute: Int = 0

    enum class AlarmType {
        NOW,
        SOON,
        SCHEDULED,
        RECURRING,
    }

    fun setCurrentTime() {
        Calendar.getInstance().also {
            currentYear = it.get(Calendar.YEAR)
            currentMonth = it.get(Calendar.MONTH)
            currentDay = it.get(Calendar.DATE)
            currentHour = it.get(Calendar.HOUR_OF_DAY)
            currentMinute = it.get(Calendar.MINUTE)
        }
        Calendar.getInstance().also {
            it.set(Calendar.MINUTE, it.get(Calendar.MINUTE))
            alarmHour = it.get(Calendar.HOUR_OF_DAY)
            alarmMinute = it.get(Calendar.MINUTE)
        }
    }

    fun setAlarmTime(timestamp: Date, alert: Int, message: String) {
        alarmYear = java.lang.Integer.parseInt("${timestamp.toString().subSequence(24,28)}")
        alarmMonth = java.lang.Integer.parseInt("${timestamp.month}")
        alarmDay =  java.lang.Integer.parseInt("${timestamp.date}")
        alarmHour = java.lang.Integer.parseInt("${timestamp.hours}")
        alarmMinute = java.lang.Integer.parseInt("${timestamp.minutes}")

        if (alert < 60) {
            alarmHour =  parseInt("${timestamp.hours}")
            alarmMinute =   parseInt("${timestamp.minutes}") - alert
        }
        else if (alert == 60) {
            alarmHour = java.lang.Integer.parseInt("${timestamp.hours}") - 1
            alarmMinute = java.lang.Integer.parseInt("${timestamp.minutes}")
        }
        else if (alert == 120) {
            alarmHour = java.lang.Integer.parseInt("${timestamp.hours}") - 2
            alarmMinute = java.lang.Integer.parseInt("${timestamp.minutes}")
        }

        currentTimeString()
        alarmTimeString()

        setAlarmScheduled(message)
    }

    fun currentTimeString() = Log.d(
        Constants.TAG, "CURRENT: ${currentYear}, ${currentMonth}, ${currentDay}, ${currentHour}, ${currentMinute}")

    fun alarmTimeString() =
        Log.d(Constants.TAG, "ALARM: ${alarmYear}, ${alarmMonth}, ${alarmDay}, ${alarmHour}, ${alarmMinute}")

    // Alarm Receiver is our Broadcast Receiver
    private fun makePendingIntent(message: String): PendingIntent {
        val notifyIntent = Intent(app, AlarmReceiver::class.java).also {
            it.putExtra(NotificationUtils.MESSAGE_KEY, message)
        }
        return PendingIntent.getBroadcast(
            app,
            REQUEST_CODE,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
    fun setAlarmScheduled(message: String) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, alarmYear)
            set(Calendar.MONTH, alarmMonth)
            set(Calendar.DATE, alarmDay)
            set(Calendar.HOUR_OF_DAY, alarmHour)
            set(Calendar.MINUTE, alarmMinute)
        }

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            makePendingIntent(message)
        )
    }

    fun setAlarmRecurring() {
        // This is beyond this lesson. You can try it out if your app requires it.
    }

    fun cancelAlarm() {

    }

    fun cancelAllAlarms() {
        // TODO Uncomment so you can cancel alarms:
        //        alarmManager.cancel(makePendingIntent(AlarmType.RECURRING.toString().lowercase()))
    }
}