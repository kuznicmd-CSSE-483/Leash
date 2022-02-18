package edu.rosehulman.leash.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import edu.rosehulman.leash.Constants
import edu.rosehulman.leash.utils.NotificationUtils

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            Log.d(Constants.TAG, "Received Rebooted Message")
            NotificationUtils.createAndLaunch(context, intent?.getStringExtra(NotificationUtils.MESSAGE_KEY) ?: "")
        }
    }
}