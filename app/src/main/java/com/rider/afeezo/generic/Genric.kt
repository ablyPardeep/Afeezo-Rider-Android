package com.rider.afeezo.generic

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.rider.afeezo.R
import com.rider.afeezo.activity.MapFrontActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class Genric {
    var activity: Activity
    var mMap: GoogleMap? = null
    var should = false
    var showing = false
    constructor(activity: Activity, mMap: GoogleMap?) {
        this.activity = activity
        this.mMap = mMap
    }

    constructor(activity: Activity) {
        this.activity = activity
    }

    fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + (Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta))))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    fun getUrl(origin: LatLng, dest: LatLng): String {
        // Origin of route
        val str_origin = "origin=" + origin.latitude + "," + origin.longitude
        // Destination of route
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude
        // Sensor enabled
        val sensor = "sensor=false"
        val mode = "mode=driving"
        val alternatives = "alternatives=true"
        // Building the parameters to the web service
        val parameters = "$str_origin&$str_dest&$mode&$alternatives&$sensor"
        // Output format
        val output = "json"
        // Building the url to the web service
        val url = "https://maps.googleapis.com/maps/api/directions/$output?$parameters"
        println("url = $url")
        return url
    }

    fun createCustomMarker(_loc: String?, _name: String?, _time: String?): Bitmap {
        val marker =
            (activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.custom_marker_layout,
                null
            )
        val placeName = marker.findViewById<TextView>(R.id.placeName)
        val time = marker.findViewById<TextView>(R.id.time)
        placeName.text = _name
        time.text = _time
        time.visibility = View.GONE
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        marker.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        marker.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(
            marker.measuredWidth,
            marker.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        marker.draw(canvas)
        return bitmap
    }

    fun getUrl(latitude: Double, longitude: Double, nearbyPlace: String): String {
        val PROXIMITY_RADIUS = 5000
        val googlePlacesUrl =
            StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?")
        googlePlacesUrl.append("location=$latitude,$longitude")
        googlePlacesUrl.append("&radius=$PROXIMITY_RADIUS")
        googlePlacesUrl.append("&type=$nearbyPlace")
        googlePlacesUrl.append("&sensor=true")
        googlePlacesUrl.append("&key=" + "AIzaSyATuUiZUkEc_UgHuqsBJa1oqaODI-3mLs0")
        Log.d("getUrl", googlePlacesUrl.toString())
        return googlePlacesUrl.toString()
    }
    fun searchedLtlng(location: String?,context: Context): LatLng? {
        var latLng: LatLng? = null
        if (Geocoder.isPresent()) {
            try {
                val gc = Geocoder(context)
                val addresses = gc.getFromLocationName(location!!, 5) // get the found Address Objects
                val ll: MutableList<LatLng> =
                    ArrayList(addresses!!.size) // A list to save the coordinates if they are available
                for (a in addresses) {
                    if (a.hasLatitude() && a.hasLongitude()) {
                        ll.add(LatLng(a.latitude, a.longitude))
                        latLng = LatLng(a.latitude, a.longitude)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                // handle the exception
            }
        }
        println("latLng = $latLng")
        return latLng
    }
    val width: Int
        get() {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val height = displayMetrics.heightPixels
            val width = displayMetrics.widthPixels
            println("width = $width")
            return width
        }

    fun createLocationRequest(mLocationRequest: LocationRequest) {
        mLocationRequest.interval = Constant.UPDATE_INTERVAL.toLong()
        mLocationRequest.fastestInterval = Constant.FATEST_INTERVAL.toLong()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.smallestDisplacement = Constant.DISPLACEMENT.toFloat()
    }

    fun checkLocPermission(): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        if (currentAPIVersion <= Build.VERSION_CODES.LOLLIPOP) {
            return false
        }
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        val accessLocPermission =
            ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
        val fineLocPermission =
            ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
        if (accessLocPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (fineLocPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (listPermissionsNeeded.size > 0) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toTypedArray(), MapFrontActivity.REQUEST_LOCATION)
            return false
        }
        return true
    }

    fun checkSmsPermission(): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        if (currentAPIVersion <= Build.VERSION_CODES.LOLLIPOP) {
            return false
        }
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        val receiveSMS =
            ContextCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS)
        //        int readSMS = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS);
        /*if (readSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_SMS);
        }*/if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_SMS)
        }
        if (listPermissionsNeeded.size > 0) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toTypedArray(), 1)
            return false
        }
        return true
    }

    fun checkStoragePermission(): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        if (currentAPIVersion <= Build.VERSION_CODES.LOLLIPOP) {
            return false
        }
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        val readStoragePermission =
            ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
        val writeStoragePermission =
            ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (readStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (writeStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (listPermissionsNeeded.size > 0) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toTypedArray(), 3)
            return false
        }
        return true
    }

    fun checkContactPermission(): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        if (currentAPIVersion <= Build.VERSION_CODES.LOLLIPOP) {
            return false
        }
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        val contactPermission =
            ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS)
        if (contactPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS)
        }
        if (listPermissionsNeeded.size > 0) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toTypedArray(), 2)
            return false
        }
        return true
    }

    fun permissionAlert(type: String) {
        var accessPermission = ""
        var message = ""
        if (type.contentEquals(Constant.GPS)) {
            message = activity.getString(R.string.gps_permission_deny_msg)
            accessPermission = Manifest.permission.ACCESS_FINE_LOCATION
            should = ActivityCompat.shouldShowRequestPermissionRationale(activity, accessPermission)
        }
        showing = true
        if (should) {
            val builder = AlertDialog.Builder(activity, R.style.MaterialThemeDialog)
            builder.setTitle(activity.getString(R.string.permission_denied))
            builder.setMessage(message)
            builder.setCancelable(false)
            builder.setPositiveButton(R.string.exit) { dialog, which ->
                activity.finish()
                dialog.dismiss()
            }
            builder.setNegativeButton(R.string.reTry) { dialog, which ->
                dialog.dismiss()
                if (type.contentEquals(Constant.GPS)) checkLocPermission()
            }
            builder.show()
        } else {
            promptSettings(message, activity)
        }
    }

    fun permissionAlert(type: String, activity: MapFrontActivity) {
        var accessPermission = ""
        var message = ""
        if (type.contentEquals(Constant.GPS)) {
            message = activity.getString(R.string.gps_permission_deny_msg)
            accessPermission = Manifest.permission.ACCESS_FINE_LOCATION
            should = ActivityCompat.shouldShowRequestPermissionRationale(activity, accessPermission)
        }
        showing = true
        if (should) {
            val builder = AlertDialog.Builder(activity, R.style.MaterialThemeDialog)
            builder.setTitle(activity.getString(R.string.permission_denied))
            builder.setMessage(message)
            builder.setCancelable(false)
            builder.setPositiveButton(R.string.exit) { dialog, _ ->
                activity.finish()
                dialog.dismiss()
            }
            builder.setNegativeButton(R.string.reTry) { dialog, _ ->
                dialog.dismiss()
                activity.checkPermission()
            }
            builder.show()
        } else {
            promptSettings(message,activity)
        }
    }

    private fun goToSettings() {
        val myAppSettings = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:" + activity.packageName)
        )
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
        myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        activity.startActivity(myAppSettings)
    }

    private fun promptSettings(message: String, activity: Activity) {
        val builder = AlertDialog.Builder(activity, R.style.MaterialThemeDialog)
        builder.setTitle(activity.getString(R.string.permission_denied))
        builder.setMessage(message)
        builder.setCancelable(false)
        builder.setPositiveButton(activity.resources.getString(R.string.goto_settngs)) { dialog, _ ->
            dialog.dismiss()
            goToSettings()
        }
        builder.setNegativeButton(activity.resources.getString(R.string.cancel), null)
        builder.show()
    }

    val servicesAvailable: Boolean
        get() {
            val api = GoogleApiAvailability.getInstance()
            val isAvailable = api.isGooglePlayServicesAvailable(activity)
            if (isAvailable == ConnectionResult.SUCCESS) {
                return true
            } else if (api.isUserResolvableError(isAvailable)) {
                val dialog = api.getErrorDialog(activity, isAvailable, 0)
                dialog?.show()
            } else {
                Toast.makeText(activity, "Cannot Connect To Play Services", Toast.LENGTH_SHORT)
                    .show()
            }
            return false
        }

    /*fun getBitmapFromVectorDrawable(context: Context?, drawableId: Int): Bitmap {
        var bitmap:Bitmap?=null
        try {
            var drawable = ContextCompat.getDrawable(context!!, drawableId)
            val bitmap = Bitmap.createBitmap(
                drawable!!.intrinsicWidth,
                drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

        }catch (e:Exception){

        }

        return bitmap!!
    }*/

    @Throws(IOException::class)
    fun downloadUrl(strUrl: String?): String {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(strUrl)

            // Creating an http connection to communicate with url
            urlConnection = url.openConnection() as HttpURLConnection

            // Connecting to url
            urlConnection.connect()

            // Reading data from url
            iStream = urlConnection.inputStream
            val br = BufferedReader(InputStreamReader(iStream))
            val sb = StringBuffer()
            var line: String? = ""
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            data = sb.toString()
            Log.d("downloadUrl", data)
            br.close()
        } catch (e: Exception) {
            Log.d("Exception", e.toString())
        } finally {
            iStream!!.close()
            urlConnection!!.disconnect()
        }
        return data
    } // Fetches data from url passed
}