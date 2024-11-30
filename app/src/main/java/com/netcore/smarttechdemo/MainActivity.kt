package com.netcore.smarttechdemo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.netcore.android.Smartech
import com.netcore.android.smartechpush.SmartPush
import com.netcore.android.smartechpush.pnpermission.SMTNotificationPermissionCallback
import com.netcore.android.smartechpush.pnpermission.SMTPNPermissionConstants
import io.hansel.hanselsdk.Hansel
import io.hansel.hanselsdk.HanselDeepLinkListener
import io.hansel.ujmtracker.HanselInternalEventsListener
import io.hansel.ujmtracker.HanselTracker
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {

    private lateinit var btnAppIds: CardView
    private lateinit var btnCe: CardView
    private lateinit var btnpx: CardView
    private lateinit var btnSdkReleases: CardView

    //android 13 permissons code for android 13 and versions

    private val notificationPermissionCallback = object : SMTNotificationPermissionCallback {
        override fun notificationPermissionStatus(status: Int) {
            if (status == SMTPNPermissionConstants.SMT_PN_PERMISSION_GRANTED) {
                // Handle the status when permission is granted
            } else {
                // Handle the status when permission is denied
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//android 13 permissons code for android 13 and versions intializations
        SmartPush.getInstance(WeakReference(applicationContext))
            .requestNotificationPermission(notificationPermissionCallback)
        SmartPush.getInstance(WeakReference(applicationContext)).updateNotificationPermission()

        initUI() // Initialize UI elements


        // Set up button click listener navigate to app id update screen
        btnAppIds.setOnClickListener {
            val intent = Intent(this, ConfigActivity::class.java)
            startActivity(intent)
        }


        // Navigate ce dash board screen
        btnCe.setOnClickListener {
            val intent = Intent(this, DashBoardScreen::class.java)
            startActivity(intent)
        }

   // Naviagate to product experience screen
        btnpx.setOnClickListener {
            startActivity(Intent(this, ProductExperienceDashBoard::class.java))
        }

        btnSdkReleases.setOnClickListener {
            val url = "https://developer.netcorecloud.com/docs/android-release-notes"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            startActivity(intent)
        }

    }



    private fun initUI() {
        btnAppIds = findViewById(R.id.btn_appid)
        btnCe = findViewById(R.id.btn_ce)
        btnpx = findViewById(R.id.btn_px)
        btnSdkReleases = findViewById(R.id.sdk_releases)

    }
}




































































































