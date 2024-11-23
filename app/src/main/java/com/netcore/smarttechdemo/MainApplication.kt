package com.netcore.smarttechdemo

import android.app.Application
import android.app.NotificationManager
import android.content.ContentValues
import android.content.IntentFilter
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.netcore.android.Smartech
import com.netcore.android.smartechpush.SmartPush
import com.netcore.android.smartechpush.notification.SMTNotificationOptions
import com.netcore.android.smartechpush.notification.channel.SMTNotificationChannel
import io.hansel.core.logger.HSLLogLevel
import io.hansel.hanselsdk.Hansel
import java.lang.ref.WeakReference


class MainApplication : Application() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        // Initialize the Smartech SDK
        val smartech = Smartech.getInstance(WeakReference(applicationContext))
        smartech.initializeSdk(this)
       // smartech.setDebugLevel(SMTDebugLevel.Level.VERBOSE)
        smartech.trackAppInstallUpdateBySmartech()
        Smartech.getInstance(WeakReference(applicationContext)).setDebugLevel(9)
        SmartPush.getInstance(WeakReference(this)).initiateNotificationDoubleOptIn()
        SmartPush.getInstance(WeakReference(this)).showInstantNotificationDoubleOptIn()

        HSLLogLevel.all.isEnabled = true
        HSLLogLevel.mid.isEnabled = true
        HSLLogLevel.debug.isEnabled = true
        Hansel.enableDebugLogs()


        // Fetch the FCM token
        fetchFcmToken()

        // Register the DeeplinkReceiver
        registerDeeplinkReceiver()

        // Set up notification options
        setupNotificationOptions()
        setupNotificationSound()
    }

    private fun setupNotificationSound() {
        // Create a notification channel group
        SmartPush.getInstance(WeakReference(applicationContext))
            .createNotificationChannelGroup("customSoundGroup", "Harish Group")

        // Build a notification channel
        val smtBuilder: SMTNotificationChannel.Builder = SMTNotificationChannel.Builder(
            "smartech", // Channel ID
            "Netcore Channel", // Channel Name
            NotificationManager.IMPORTANCE_MAX // Importance level
        )

        // Set channel description
        smtBuilder.setChannelDescription("This is Harish's notification channel")

        // Set channel group ID (make sure the group is created beforehand)
        smtBuilder.setChannelGroupId("customSoundGroup")

        // Set custom notification sound (sound file should be in `res/raw` folder without extension)
        smtBuilder.setNotificationSound("lau")

        // Build the notification channel
        val smtNotificationChannel: SMTNotificationChannel = smtBuilder.build()

        // Create the notification channel
        SmartPush.getInstance(WeakReference(applicationContext))
            .createNotificationChannel(smtNotificationChannel)
    }

    private fun registerDeeplinkReceiver() {
        val deeplinkReceiver = DeeplinkReceiver()
        val filter = IntentFilter("com.smartech.EVENT_PN_INBOX_CLICK")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            this.registerReceiver(deeplinkReceiver, filter, RECEIVER_EXPORTED)
        } else {
            this.registerReceiver(deeplinkReceiver, filter)
        }
    }

    private fun setupNotificationOptions() {
        val options = SMTNotificationOptions(this).apply {
            brandLogo = "pn_icon" // e.g. logo is a sample name for brand logo
            largeIcon = "pn_icon" // e.g. icon_notification is a sample name for large icon
            smallIcon = "pn_icon" // e.g. ic_action_play is a sample name for small icon
            smallIconTransparent = "pn_icon" // e.g. ic_action_play is a sample name for transparent small icon
            transparentIconBgColor = "#FF8000"
            placeHolderIcon = "pn_icon" // e.g. ic_notification is a sample name for placeholder icon
        }

        SmartPush.getInstance(WeakReference(this)).setNotificationOptions(options)
    }

    private fun fetchFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful && !TextUtils.isEmpty(task.result)) {
                task.result?.let { handleFcmToken(it) }
            } else {
                Log.e(ContentValues.TAG, "Fetch FCM token failed: Task unsuccessful or token is empty.")
            }
        })
    }

    private fun handleFcmToken(fcmToken: String) {
        val smartTech = SmartPush.getInstance(WeakReference(this))
        val currentToken = smartTech.getDevicePushToken()

        Log.i("TOKEN", "FCM Instance Id Token: $fcmToken")
        Log.i("TOKEN", "Current FCM Token: $currentToken")

        if (TextUtils.isEmpty(currentToken)) {
            smartTech.setDevicePushToken(fcmToken) // Set the FCM token if there is no current token
        } else if (currentToken != fcmToken) {
            smartTech.setDevicePushToken(fcmToken) // Update the token if it's different
            Log.i("TOKEN", "New token set: $fcmToken")
        } else {
            Log.i("TOKEN", "Both tokens are the same.")
        }
    }
}



































/*
package com.netcore.smarttechdemo

import android.app.Application
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.IntentFilter
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.netcore.android.Smartech
import com.netcore.android.logger.SMTDebugLevel
import com.netcore.android.smartechpush.SmartPush
import com.netcore.android.smartechpush.notification.SMTNotificationOptions
import java.lang.ref.WeakReference

class MainApplication:Application() {


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        //initialise the sdk
        Smartech.getInstance(WeakReference(applicationContext)).initializeSdk(this)
        //Enabling Smartech logs for testing.
        Smartech.getInstance(WeakReference(this)).setDebugLevel(9)
        //track app and install
        Smartech.getInstance(WeakReference(this)).trackAppInstallUpdateBySmartech()


        //firebase token fetch



        fetchFcmToken()

        val deeplinkReceiver = DeeplinkReceiver()
        val filter = IntentFilter("com.smartech.EVENT_PN_INBOX_CLICK")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            this.registerReceiver(deeplinkReceiver, filter, RECEIVER_EXPORTED)
        } else {
            this.registerReceiver(deeplinkReceiver, filter)
        }






        val options = SMTNotificationOptions(this)
        options.brandLogo = "logo"//e.g.logo is sample name for brand logo
        options.largeIcon = "icon_nofification"//e.g.ic_notification is sample name for large icon
        options.smallIcon = "ic_arrow_left"//e.g.ic_action_play is sample name for icon
        options.smallIconTransparent = "ic_arrow_left"//e.g.ic_action_play is sample name for transparent small icon
        options.transparentIconBgColor = "#FF0000"
        options.placeHolderIcon = "ic_notification"//e.g.ic_notification is sample name for placeholder icon
        SmartPush.getInstance(WeakReference(this)).setNotificationOptions(options)
    }

    private fun fetchFcmToken() {
        try {

            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful&&!TextUtils.isEmpty(task.getResult())) {
                    val smartTech = SmartPush.getInstance(WeakReference(this))
                    var fcmToken = task.getResult()
                    var currentToken = smartTech.getDevicePushToken()
                    Log.i("TOKEN", "FCM Instance Id Token: $fcmToken")

                    Log.i("TOKEN", "Current FCM Token:$currentToken")

                    if (TextUtils.isEmpty(currentToken)){
                        smartTech.setDevicePushToken(currentToken)
                    }else if (currentToken.equals(fcmToken)){
                        smartTech.setDevicePushToken(fcmToken)
                        Log.i("TOKEN", "New token set: $fcmToken")
                    }else {
                        Log.i("TOKEN", "Both tokens are same.")

                    }

                }
                else{
                    Log.e("TOKEN", "Fetch FCM token failed: Task unsuccessful.")
                }
            })
        }
        catch (e: Exception) {
            Log.e(ContentValues.TAG, "Fetching FCM token failed.")
        }
    }
}*/
