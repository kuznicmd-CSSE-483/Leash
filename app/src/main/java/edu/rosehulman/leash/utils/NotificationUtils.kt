package edu.rosehulman.leash.utils

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import edu.rosehulman.leash.MainActivity
import edu.rosehulman.leash.R

object NotificationUtils {
    private const val NOTIFICATION_ID = 1
    private const val channelId = "AlarmNotifierChannelId"
    private const val channelName = "AlarmNotifierChannel"
    const val MESSAGE_KEY = "message"

    // TODO 1 Read the options.
    // This is standard required setup. Every notification must appear on some channel.
    // We allow lights and vibration.
    // Credit: https://developer.android.com/codelabs/advanced-android-kotlin-training-notifications#0
    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(context: Context) {
        // Required
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            // Options
            setShowBadge(false)
            lockscreenVisibility = Notification.VISIBILITY_SECRET
            enableLights(true)
            lightColor = Color.RED
            enableVibration(true)
            vibrationPattern = longArrayOf(50, 25, 100, 50)
            description = "Alarm notification channel"
        }

        // Required
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    @TargetApi(Build.VERSION_CODES.M)
    fun createAndLaunch(context: Context, data: String) {
        val notificationManager = context.getSystemService(NotificationManager::class.java)

        // TODO 4 Create an intent and pending intent and set it in the notification
        // so that it can launch the app.
        val contentIntent = Intent(context, MainActivity::class.java).also {
            it.putExtra(NotificationUtils.MESSAGE_KEY, data)
        }

        val contentPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // TODO 2 Create a basic notification with at least title, text,
        //  small icon, and high priority. Use the data passed in as part of the text.
        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle("WAKEY WAKEY!")
            .setContentText("Your ${data} alarm is going off!!!")
            .setSmallIcon(R.drawable.ic_baseline_pets_24)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)


        // TODO 3 Actually send the notification
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun getLongText() =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
}