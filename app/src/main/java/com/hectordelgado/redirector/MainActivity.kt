package com.hectordelgado.redirector

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val CHANNEL_ID ="com.hectordelgado.redirector.updates"
    private val KEY_TEXT_REPLY = "key_text_reply"
    private val notificationId = 99

    private lateinit var notificationManager: NotificationManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        createNotificationChannel(CHANNEL_ID, "Re-Director Updates", "Here's some text to redirect")

        handleIntent()
    }

    fun sendNotification(view: View) {
        // Creates a RemoteInput object to collect user input
        val remoteInput = RemoteInput.Builder(KEY_TEXT_REPLY)
            .setLabel("Enter something to redirect")
            .build()

        val resultIntent = Intent(
            this,
            MainActivity::class.java)

        // Create a PendingIntent object to retrieve the correct activity
        val resultPendingIntent = PendingIntent.getActivity(
            this,
            0,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT)


        // Create a Notification action that will
        // handle user input and take users back to the
        // MainActivity when they reply
        val action = NotificationCompat.Action.Builder(
            android.R.drawable.ic_dialog_info,
            "Reply Now!",
            resultPendingIntent)
            .addRemoteInput(remoteInput)
            .build()


        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Lets Re-Direct IT")
            .setContentText("Wanna redirect this?")
            .addAction(action)
            .build()

        notificationManager.notify(notificationId, notificationBuilder)
    }

    private fun createNotificationChannel(id: String, name: String, description: String) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(id, name, importance).apply {
            this.description = description
            enableLights(true)
            lightColor = Color.GREEN
            enableVibration(true)
            vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        }

        notificationManager.createNotificationChannel(channel)
    }

    private fun handleIntent() {
        val callingIntent = intent
        val remoteInput = RemoteInput.getResultsFromIntent(callingIntent)

        remoteInput?.let {
            val inputString = remoteInput.getCharSequence(KEY_TEXT_REPLY).toString()
            resultTextView.text = inputString

            val repliedNotification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_close_clear_cancel)
                .setContentText("Message sent")
                .build()

            notificationManager.notify(notificationId, repliedNotification)
        }
    }
}
