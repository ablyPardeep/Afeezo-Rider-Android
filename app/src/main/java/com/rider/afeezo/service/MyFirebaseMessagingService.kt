package com.rider.afeezo.service

import android.app.Notification
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.google.firebase.messaging.FirebaseMessagingService
import com.rider.afeezo.generic.NotificationHelper
import android.os.Bundle
import com.google.firebase.messaging.RemoteMessage
import android.content.Intent
import com.rider.afeezo.R
import com.rider.afeezo.activity.WalletActivity
import com.rider.afeezo.activity.MapFrontActivity
import android.app.NotificationManager
import android.app.PendingIntent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.app.NotificationChannel
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.Utility

class MyFirebaseMessagingService : FirebaseMessagingService() {
    var noti: NotificationHelper? = null
    var mBundle = Bundle()
    val type="type"
    val message="message"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        Utility.logI("------------ onMessageReceived -----------")
        for ((key, value) in data) {
            Utility.Companion.logD("$key : $value")
        }
        if (instance.token != null) {
            if (data.isNotEmpty() && (data[type].equals(
                    Constant.RIDE_REQUEST_COMPLETED,
                    ignoreCase = true
                ) || data[type].equals(
                    Constant.RIDE_READY_FOR_PICKUP, ignoreCase = true
                ) || data[type]
                    .equals(Constant.RIDE_COMPLETED, ignoreCase = true) || data[type]
                    .equals(Constant.RIDE_STARTED, ignoreCase = true) || data[type].equals(
                    Constant.RIDE_REQUEST_DECLINED_ALL_DRIVERS,
                    ignoreCase = true
                ) || data[type]
                    .equals(Constant.RIDE_CANCELLED, ignoreCase = true))
            ) {
                mBundle.putString("recordId", data["id"])
                mBundle.putString(type, data[type])
                mBundle.putString(message, data[message])
                if (data[type].equals(Constant.RIDE_COMPLETED, ignoreCase = true)) {
                    instance.getStore(this).saveString(Constant.COMPLETE_RIDE, data["id"]!!)
                }
                val intent = Intent(Constant.RIDE_DETAIL_INTENT)
                intent.putExtras(mBundle)
                this.sendBroadcast(intent)
            }
            sendNotification(this, getIntent(data), getString(R.string.app_name), data[message])
        }
    }

    private fun getIntent(type: Map<String, String>?): Intent? {
        var intent: Intent? = null
        if (type != null) {
            if (type[this.type].equals(Constant.TXN, ignoreCase = true)) {
                intent = Intent(this, WalletActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            if (type[this.type].equals(Constant.NOTIFICATION_TYPE, ignoreCase = true) || type[this.type].equals(Constant.NOTIFICATION_REFERRAL_REWARD_POINTS, ignoreCase = true)) {
                instance.getStore(this).saveBoolean(Constant.oneTimeCall, true)
                intent = Intent(this, MapFrontActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                mBundle.putString("Body", type["text"])
                mBundle.putString("Title", type["title"])
                intent.putExtras(mBundle)
            } else {
                instance.getStore(this).saveBoolean(Constant.oneTimeCall, true)
                intent = Intent(this, MapFrontActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                intent.putExtras(mBundle)
            }
        }
        return intent
    }

    private fun sendNotification(context: Context, intent: Intent?, title: String?, messageBody: String?) {
        notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val vib = longArrayOf(200, 200)
        val icon = BitmapFactory.decodeResource(
            context.resources,
            R.mipmap.ic_launcher
        )
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var notificationBuilder: NotificationCompat.Builder? = null
        var nb: Notification.Builder? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan1 = NotificationChannel(
                PRIMARY_CHANNEL,
                getString(R.string.noti_channel_default), NotificationManager.IMPORTANCE_DEFAULT
            )
            chan1.lightColor = Color.GREEN
            chan1.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            notificationManager!!.createNotificationChannel(chan1)
            noti = NotificationHelper(this)
            nb = Notification.Builder(applicationContext, PRIMARY_CHANNEL)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setSmallIcon(R.drawable.icon_car).setOngoing(false)
                .setStyle(Notification.BigTextStyle().bigText(messageBody))
                .setAutoCancel(true).setContentIntent(pendingIntent)
            noti!!.notify(1, nb)
        } else {
            notificationBuilder = NotificationCompat.Builder(context)
                .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
                .setSmallIcon(R.drawable.icon_car) //.setPriority((int)NotificationCompatPriority.Max)
                .setLargeIcon(icon)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri).setOngoing(false)
                .setVibrate(vib)
                .setLights(Color.GREEN, 2000, 2000).setStyle(
                    NotificationCompat.BigTextStyle().bigText(messageBody)
                ) //.setNumber(++number)
                .setContentIntent(pendingIntent)
            notificationManager!!.notify(1, notificationBuilder.build())
        }
    }

    companion object {
        const val PRIMARY_CHANNEL = "default"
        @JvmField
        var notificationManager: NotificationManager? = null
    }
}