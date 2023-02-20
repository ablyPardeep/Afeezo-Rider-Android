package com.rider.afeezo

import android.annotation.SuppressLint
import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun getUTCDateTimeAsString(): String? {
    val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm aa")
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date())
}

@SuppressLint("SimpleDateFormat")
fun timeToTimestamp(str_date: String?, pattern: String?): Long {
    var timeStamp: Long = 0
    try {
        val formatter = SimpleDateFormat(pattern)
        //SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
//        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val date = formatter.parse(str_date!!) as Date
        timeStamp = date.time
    } catch (ex: ParseException) {
        ex.printStackTrace()
    } catch (ex: java.lang.Exception) {
        ex.printStackTrace()
    }
    timeStamp /= 1000
    Log.e("utc is", "time_to_timestamp:    $timeStamp")
    return timeStamp
}

