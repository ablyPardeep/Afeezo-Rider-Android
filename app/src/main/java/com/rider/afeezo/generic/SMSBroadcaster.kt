package com.rider.afeezo.generic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class SMSBroadcaster : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        val bundle = intent.extras
        var smsm: Array<SmsMessage?>? = null
        var sms_str = ""
        if (bundle != null) {
            val pdus = bundle["pdus"] as Array<Any>?
            smsm = arrayOfNulls(pdus!!.size)
            for (i in smsm.indices) {
                smsm[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                sms_str = smsm[i]?.messageBody.toString()
                val Sender = smsm[i]?.originatingAddress
                if (Sender != null) {
                    if (Sender.contains(DataHolder.instance.smsSender)) {
                        val smsIntent = Intent("otp")
                        smsIntent.putExtra("message", sms_str)
                        LocalBroadcastManager.getInstance(context!!).sendBroadcast(smsIntent)
                    }
                }
            }
        }
    }
}
