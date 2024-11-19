package com.netcore.smarttechdemo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.netcore.android.Smartech
import com.netcore.android.smartechappinbox.SmartechAppInbox
import com.netcore.android.smartechappinbox.network.listeners.SMTInboxCallback
import com.netcore.android.smartechappinbox.network.model.SMTInboxMessageData
import com.netcore.android.smartechappinbox.utility.SMTAppInboxMessageType
import com.netcore.android.smartechappinbox.utility.SMTAppInboxRequestBuilder
import com.netcore.android.smartechappinbox.utility.SMTInboxDataType
import com.netcore.android.smartechpush.SmartPush
import com.netcore.android.smartechpush.pnpermission.SMTNotificationPermissionCallback
import com.netcore.android.smartechpush.pnpermission.SMTPNPermissionConstants
import java.lang.ref.WeakReference

class CustomInbox : AppCompatActivity() {

    private lateinit var btnAppInbox: Button
    private lateinit var progressBar: ProgressBar

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
        setContentView(R.layout.custom_inbox)

        SmartPush.getInstance(WeakReference(applicationContext)).requestNotificationPermission(notificationPermissionCallback)
        SmartPush.getInstance(WeakReference(applicationContext)).updateNotificationPermission()
        Smartech.getInstance(WeakReference(applicationContext)).login("harish@gmail.com")


        initUI() // Initialize UI elements

        // Set up button click listeners
        btnAppInbox.setOnClickListener {
            fetchInboxMessages() // Handle APPINBOX button click
        }

    }

    private fun fetchInboxMessages() {
        val categoryList = arrayListOf("nuvama")
        Toast.makeText(applicationContext, "Fetching inbox messages...", Toast.LENGTH_SHORT).show()

        val smartechAppInbox = SmartechAppInbox.getInstance(WeakReference(applicationContext))
        val builder = SMTAppInboxRequestBuilder.Builder(SMTInboxDataType.ALL)
            .setCallback(object : SMTInboxCallback {

                override fun onInboxFail() {
                    hideProgressBar()
                    Toast.makeText(applicationContext, "Failed to fetch inbox messages.", Toast.LENGTH_SHORT).show()
                }

                override fun onInboxProgress() {
                    runOnUiThread {
                        progressBar.visibility = View.VISIBLE // Show progress bar while loading
                    }
                }

                override fun onInboxSuccess(messages: MutableList<SMTInboxMessageData>?) {
                    Log.i("INBOX Success", "Messages fetched: ${messages.toString()}")

                    val count = smartechAppInbox.getAppInboxMessageCount(SMTAppInboxMessageType.INBOX_MESSAGE)
                    Log.i("INBOX count", "Messages fetched: ${count.toInt()}")
                    hideProgressBar()

                    if (!messages.isNullOrEmpty()) {
                        val inboxMessages = messages.map { messageData ->
                            InboxMessage(
                                title = messageData.smtPayload.title ?: "No Title",
                                body = messageData.smtPayload.body ?: "No Body",
                                time = messageData.smtPayload.publishedDate ?: "No Date",
                                mediaUrl = messageData.smtPayload.mediaUrl ?: ""
                            )
                        }

                        // Update RecyclerView
                        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
                        recyclerView.layoutManager = LinearLayoutManager(this@CustomInbox)
                        recyclerView.adapter = InboxAdapter(inboxMessages)
                    } else {
                        Toast.makeText(applicationContext, "No messages found.", Toast.LENGTH_SHORT).show()
                    }
                }

            }).setCategory(categoryList)
            .setLimit(10)
            .build()

        smartechAppInbox.getAppInboxMessages(builder)
    }




    private fun hideProgressBar() {
        runOnUiThread {
            progressBar.visibility = View.GONE // Hide the progress bar
        }
    }

    private fun initUI() {
        btnAppInbox = findViewById(R.id.btn_appinbox)
        progressBar = findViewById(R.id.progressBar)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

}

