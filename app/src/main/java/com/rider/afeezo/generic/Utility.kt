package com.rider.afeezo.generic

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Environment
import android.provider.Settings
import android.text.Html
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.rider.afeezo.R
import com.rider.afeezo.activity.LoginActivity
import com.rider.afeezo.model.MenuItemCustom
import com.rider.afeezo.service.MyFirebaseMessagingService
import com.google.android.material.snackbar.Snackbar
import java.io.*
import java.net.InetAddress
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class Utility(var activity: Activity) {
    private var networkEnable = false

    fun hideSoftKeyboard(editText: View) {
        try {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(
                editText.windowToken, 0
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun hideKeyboard() {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
//        imm.hideSoftInputFromWindow(view.windowToken, 0)
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    fun showSoftKeyboard(editText: EditText?) {
        try {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(
                editText, 0
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun hideSoftKeyboard() {
        try {
            val inputMethodManager = activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun sessionExpire(activity: Activity) {
        if (MyFirebaseMessagingService.notificationManager != null) {
            MyFirebaseMessagingService.notificationManager?.cancelAll()
        }
        showToast(activity, activity.getString(R.string.session_expire))
        DataHolder.instance.getStore(activity).clear(Constant.AUTHORIZE)
        DataHolder.instance.getStore(activity).clear(Constant.ASK_PROFILE_INFO)
        DataHolder.instance.getStore(activity).clear(Constant.RIDER_ID)
        DataHolder.instance.getStore(activity).clear(Constant.RIDER_TOKEN)
        DataHolder.instance.getStore(activity).clear(Constant.PROFILE_DOWNLOAD)
        DataHolder.instance.token = ""
        activity.startActivity(Intent(activity, LoginActivity::class.java))
        activity.finish()
    }

    fun storeImage(image: Bitmap, imageName: String) {
        val pictureFile = getOutputMediaFile(imageName)
        if (pictureFile == null) {
            Log.d(
                "Log",
                "Error creating media file, check storage permissions: "
            ) // e.getMessage());
            return
        }
        try {
            val fos = FileOutputStream(pictureFile)
            image.compress(Bitmap.CompressFormat.JPEG, 40, fos)
            fos.close()
        } catch (e: FileNotFoundException) {
            Log.d("log", "File not found: " + e.message)
        } catch (e: IOException) {
            Log.d("log", "Error accessing file: " + e.message)
        }
    }

    fun getOutputMediaFile(imageName: String): File? {
        val mediaStorageDir = File(Constant.IMAGE_PATH)
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }
        return File(mediaStorageDir.path + File.separator + imageName)
    }

    // Variable to check if GPS and Network operator is enabled
    // notify user
    val isGpsEnabled: Boolean
        get() {
            var gpsEnabled = false
            var networkEnabled = false
            val lm = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            try {
                try {
                    gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
                } catch (ex: Exception) {
                }

                try {
                    networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                } catch (ex: Exception) {
                }
                // notify user
                if (!gpsEnabled && !networkEnabled) {
                    networkEnable = false
                    val dialog = AlertDialog.Builder(activity, R.style.MaterialThemeDialog)
                    dialog.setCancelable(false)
                    dialog.setMessage(activity.resources.getString(R.string.gps_network_not_enabled))
                    dialog.setPositiveButton(
                        activity.resources.getString(R.string.ok)
                    ) { _, _ ->
                        val myIntent =
                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        activity.startActivity(myIntent)
                        //get gps
                    }
                    dialog.setNegativeButton(
                        activity.getString(R.string.cancel)
                    ) { _, _ ->
                        networkEnable = false
                    }
                    dialog.show()
                } else networkEnable = true
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return networkEnable
        }

    fun showSnackBar(msg: String?) {
        if (msg != null && !msg.equals("", ignoreCase = true)) {
            if (msg.contentEquals(activity.getString(R.string.error_internet_connection))) {
                val snackbar = Snackbar
                    .make(activity.findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                    .setAction(
                        R.string.settings
                    ) {
                        activity.startActivityForResult(
                            Intent(Settings.ACTION_SETTINGS),
                            404
                        )
                    }
                snackbar.setActionTextColor(Color.RED)
                val sbView = snackbar.view
                val textView = sbView.findViewById<View>(R.id.snackbar_text) as TextView
                textView.setTextColor(Color.YELLOW)
                snackbar.show()
            } else {
                val snackbar=Snackbar.make(
                    activity.findViewById(android.R.id.content),
                    msg,
                    Snackbar.LENGTH_LONG
                )
                val sbView = snackbar.view
                val textView = sbView.findViewById<View>(R.id.snackbar_text) as TextView
                textView.setTextColor(Color.YELLOW)
                snackbar.show()
            }
        }
    }

    fun showAlert(ctx: Context?, msg: String?) {
        val builder = AlertDialog.Builder(
            ctx!!, R.style.MaterialThemeDialog
        )
        builder.setTitle(activity.getString(R.string.app_name))
        builder.setMessage(Html.fromHtml(msg))
        val positiveText = activity.getString(android.R.string.ok)
        builder.setPositiveButton(
            positiveText
        ) { dialog, _ -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }


    fun generateNoteOnSD(context: Context?, sFileName: String, sBody: String?) {
        var bw: BufferedWriter? = null
        var fw: FileWriter? = null
        try {
            val root = File(Environment.getExternalStorageDirectory(), "LogsRider")
            if (!root.exists()) {
                root.mkdirs()
            }
            val path =
                Environment.getExternalStorageDirectory().toString() + "/LogsRider/" + sFileName
            val file = File(path)
            if (file.exists()) {
                fw = FileWriter(file.absoluteFile, true)
                bw = BufferedWriter(fw)
                bw.write(sBody)
                if (bw != null) bw.close()
                if (fw != null) fw.close()
            } else {
                val gpxfile = File(root, sFileName)
                val writer = FileWriter(gpxfile)
                writer.append(sBody)
                writer.flush()
                writer.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    companion object {
        private const val DEBUG = true
        private const val TAG = "Yokart"

        private var toast: Toast? = null
        private var className: String? = null
        private var methodName: String? = null
        private var lineNumber = 0


        private fun createLog(log: String): String {
            val buffer = StringBuffer()
            buffer.append("[")
            buffer.append(className)
            buffer.append(":")
            buffer.append(methodName)
            buffer.append(":")
            buffer.append(lineNumber)
            buffer.append("]")
            buffer.append(log)
            return buffer.toString()
        }

        private fun getMethodNames(sElements: Array<StackTraceElement>) {
            className = sElements[1].fileName
            methodName = sElements[1].methodName
            lineNumber = sElements[1].lineNumber
        }

        fun logD(message: String?) {
            var msg = message
            if (msg == null) {
                msg = ""
            }
            getMethodNames(Throwable().stackTrace)
            if (DEBUG) Log.d(TAG, createLog(msg))
        }

        fun logE(message: String?) {
            var msg = message
            if (msg == null) {
                msg = ""
            }
            getMethodNames(Throwable().stackTrace)
            if (DEBUG) Log.e(TAG, createLog(msg))
        }

        fun logI(message: String?) {
            var msg = message
            if (msg == null) {
                msg = ""
            }
            getMethodNames(Throwable().stackTrace)
            if (DEBUG) Log.i(TAG, createLog(msg))
        }

        fun showErrorDialog(context: Context) {
            if (isNetworkConnected(context)) {
                showDialog(context, context.getString(R.string.error_something_wrong))
            } else {
                showDialog(context, context.getString(R.string.error_internet_connection))
            }
        }

        fun showDialog(context: Context, msg: String?) {
            val b = AlertDialog.Builder(context, R.style.MaterialThemeDialog)
            b.setTitle(context.getString(R.string.message))
            b.setMessage(Html.fromHtml(msg))
            b.setCancelable(false)
            b.setPositiveButton(
                context.getString(R.string.ok)
            ) { dialog, _ -> dialog.cancel() }
            val alert11 = b.create()
            alert11.show()
        }

        fun showMapScreenDialog(context: Context, mTitle: String?, msg: String?) {
            val b = AlertDialog.Builder(context, R.style.MaterialThemeDialog)
            b.setTitle(mTitle)
            b.setMessage(Html.fromHtml(msg))
            b.setCancelable(false)
            b.setPositiveButton(
                context.getString(R.string.ok)
            ) { dialog, _ -> dialog.cancel() }
            val alert11 = b.create()
            if (!msg.isNullOrEmpty())
                alert11.show()
        }

        fun isValidEmail(target: CharSequence?): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }

        fun showLongToast(context: Context, message: String, type: Boolean) {
            if (toast == null) {
                show(context, message, type, false)
            } else {
                toast!!.cancel()
                toast = null
                show(context, message, type, false)
            }
        }

        fun showToast(context: Context, message: String) {
            if (toast == null) {
                show(context, message, type = false, bottom = false)
            } else {
                toast!!.cancel()
                toast = null
                show(context, message, type = false, bottom = false)
            }
        }

        private fun show(context: Context, message: String, type: Boolean, bottom: Boolean) {
            toast = if (type) {
                Toast.makeText(context, message, Toast.LENGTH_LONG)
            } else {
                Toast.makeText(context, message, Toast.LENGTH_SHORT)
            }
            if (!bottom) {
                toast?.setGravity(Gravity.CENTER, 0, 0)
            }
            toast?.show()
        }

        fun isNetworkConnected(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo != null
        }


        //You can replace it with your name
        val isInternetAvailable: Boolean
            get() = try {
                val ipAddr = InetAddress.getByName("google.com") //You can replace it with your name
                logD("------------ipAddr-------------: $ipAddr")
                if (ipAddr.equals("")) {
                    true
                } else {
                    true
                }
            } catch (e: Exception) {
                logE("" + e.toString())
                true
            }

        fun showImage(context: Context, view: ImageView?, url: String?) {
            try {
                if (url == null || view == null) {
                    return
                }
                Glide.with(context)
                    .load(url)
                    .apply(
                        RequestOptions().override(150, 150)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .placeholder(R.drawable.image_placeholder).timeout(60000)
                    ).into(view)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        fun showImageMap(context: Context, view: ImageView, url: String?) {
            try {
                Glide.with(context)
                    .load(url)
                    .apply(
                        RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).timeout(60000)
                            .placeholder(R.drawable.image_placeholder)
                    ).into(view)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        fun getMenuList(context: Context): ArrayList<MenuItemCustom> {
            val list = ArrayList<MenuItemCustom>()
            var item = MenuItemCustom()
            item.name = context.resources.getString(R.string.ride_listing)
            item.id = R.id.nav_booking.toString()
            list.add(item)
            item = MenuItemCustom()
            item.name = context.resources.getString(R.string.emergency_contacts)
            item.id = R.id.nav_contacts.toString()
            list.add(item)
            item = MenuItemCustom()
            item.name = context.resources.getString(R.string.label_payments)
            item.id = R.id.nav_payments.toString()
            list.add(item)
            item = MenuItemCustom()
            item.name = context.resources.getString(R.string.saved_places)
            item.id = R.id.nav_saved_places.toString()
            list.add(item)
            /** check if referral is enabled or not*/
            if (DataHolder.instance.getStore(context).getString(Constant.REFERRAL_ENABLE) == "1") {
                item = MenuItemCustom()
                item.name = context.resources.getString(R.string.label_reffer_earn)
                item.id = R.id.nav_share.toString()
                list.add(item)
            }
           /* item = MenuItemCustom()
            item.name = context.resources.getString(R.string.change_language)
            item.id = R.id.nav_changeLanguage.toString()
            list.add(item)*/
            item = MenuItemCustom()
            item.name = context.resources.getString(R.string.label_support)
            item.id = R.id.nav_support.toString()
            list.add(item)
            item = MenuItemCustom()
            item.name = context.resources.getString(R.string.contact_us)
            item.id = R.id.nav_contactus.toString()
            list.add(item)

            item = MenuItemCustom()
            item.name = context.resources.getString(R.string.request_my_data)
            item.id = R.id.nav_request_data.toString()
            list.add(item)
            item = MenuItemCustom()
            item.name = context.resources.getString(R.string.delete_my_account)
            item.id = R.id.nav_delete_account.toString()
            list.add(item)
            /*item = MenuItemCustom()
            item.name = context.resources.getString(R.string.about_us)
            item.id = R.id.nav_aboutus.toString()
            list.add(item)
            item = MenuItemCustom()
            item.name = context.resources.getString(R.string.termsConditions)
            item.id = R.id.nav_terms.toString()
            list.add(item)*/
            return list
        }

        fun changeTimeFormat(date: String): String {
            var originalFormat: DateFormat? = null
            val targetFormat: DateFormat = SimpleDateFormat("EEE, MMM dd, yyyy, hh:mm aa")
            return if (date.length < 19) {
                originalFormat =
                    SimpleDateFormat("yyyy/MM/dd hh:mm", Locale.ENGLISH)
                val dateUpdate = originalFormat.parse(date)
                targetFormat.format(dateUpdate!!)
            } else {
                originalFormat =
                    SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH)
                val dateUpdate = originalFormat.parse(date)
                targetFormat.format(dateUpdate!!)
            }
        }

        fun setCustomDrawable(setupTxtPayment: TextView?, btIcCard: Int, endId: Int) {
            setupTxtPayment?.setCompoundDrawablesWithIntrinsicBounds(
                btIcCard,
                0,
                endId,
                0
            )
        }

        fun getTimeline(): String {
            val c = Calendar.getInstance()
            return when (c[Calendar.HOUR_OF_DAY]) {
                in 0..11 -> {
                    "Good Morning"
                }
                in 12..15 -> {
                    "Good Afternoon"
                }
                in 16..20 -> {
                    "Good Evening"
                }
                in 21..23 -> {
                    "Good Evening"
                }
                else -> {
                    "Good Morning"
                }
            }
        }

        private fun getWidth(activity: Context): Int {
            val metrics = activity.resources.displayMetrics
            return metrics.widthPixels
        }

        fun getItemWidth(activity: Context): Int {
            var width = 0
            var screenWidth = getWidth(activity)
            screenWidth -= (5 * activity.resources.getDimension(R.dimen.margin_10)
                .toInt())
            screenWidth -= (2 * activity.resources.getDimension(R.dimen.margin_20)
                .toInt())
            width = screenWidth / 6
            return width
        }


    }
}