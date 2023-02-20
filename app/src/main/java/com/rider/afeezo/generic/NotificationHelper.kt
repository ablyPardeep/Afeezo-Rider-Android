/*
* Copyright 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.rider.afeezo.generic

import android.app.Notification
import androidx.annotation.RequiresApi
import android.os.Build
import android.content.ContextWrapper
import android.app.NotificationManager
import com.rider.afeezo.generic.NotificationHelper
import android.media.RingtoneManager
import android.app.NotificationChannel
import android.content.Context
import android.graphics.Color
import android.net.Uri
import com.rider.afeezo.R

/**
 * Helper class to manage notification channels, and create notifications.
 */
class NotificationHelper @RequiresApi(api = Build.VERSION_CODES.O) constructor(ctx: Context?) :
    ContextWrapper(ctx) {
    /**
     * Get the notification manager.
     *
     * Utility method as this helper works with it a lot.
     *
     * @return The system service NotificationManager
     */
    private var manager: NotificationManager? = null
        private get() {
            if (field == null) {
                field = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            }
            return field
        }
    var vib = longArrayOf(200, 200)
    var defaultSoundUri: Uri

    /**
     * Get a notification of type 1
     *
     * Provide the builder rather than the notification it's self as useful for making notification
     * changes.
     *
     * @param title the title of the notification
     * @param body the body text for the notification
     * @return the builder as it keeps a reference to the notification (since API 24)
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun getNotification1(title: String?, body: String?): Notification.Builder {
        return Notification.Builder(applicationContext, PRIMARY_CHANNEL)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(smallIcon)
            .setAutoCancel(true)
    }

    /**
     * Build notification for secondary channel.
     *
     * @param title Title for notification.
     * @param body Message for notification.
     * @return A Notification.Builder configured with the selected channel and details
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun getNotification2(title: String?, body: String?): Notification.Builder {
        return Notification.Builder(applicationContext, SECONDARY_CHANNEL)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(smallIcon)
            .setAutoCancel(true)
    }

    /**
     * Send a notification.
     *
     * @param id The ID of the notification
     * @param notification The notification object
     */
    fun notify(id: Int, notification: Notification.Builder) {
        manager!!.notify(id, notification.build())
    }

    /**
     * Get the small icon for this app
     *
     * @return The small icon resource id
     */
    private val smallIcon: Int
        private get() = android.R.drawable.stat_notify_chat

    companion object {
        const val PRIMARY_CHANNEL = "default"
        const val SECONDARY_CHANNEL = "second"
    }

    /**
     * Registers notification channels, which can be used later by individual notifications.
     *
     * @param ctx The application context
     */
    init {
        defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val chan1 = NotificationChannel(
            PRIMARY_CHANNEL,
            getString(R.string.noti_channel_default), NotificationManager.IMPORTANCE_DEFAULT
        )
        chan1.lightColor = Color.GREEN
        chan1.vibrationPattern = vib
        chan1.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        manager!!.createNotificationChannel(chan1)
        val chan2 = NotificationChannel(
            SECONDARY_CHANNEL,
            getString(R.string.noti_channel_second), NotificationManager.IMPORTANCE_HIGH
        )
        chan2.lightColor = Color.BLUE
        chan2.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        manager!!.createNotificationChannel(chan2)
    }
}