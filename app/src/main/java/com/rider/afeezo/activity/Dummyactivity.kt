package com.rider.afeezo.activity

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.rider.afeezo.IBaseGpsListener
import com.rider.afeezo.R
import java.util.*
import kotlinx.android.synthetic.main.activity_dummy.*

 class Dummyactivity : Activity(), IBaseGpsListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy)
        val locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0f, this)
        updateSpeed(null)
        chkMetricUnits.setOnCheckedChangeListener { buttonView, isChecked -> // TODO Auto-generated method stub
            updateSpeed(null)
        }
    }

    override fun finish() {
        super.finish()
        System.exit(0)
    }

    private fun updateSpeed(location: CLocation?) {
        // TODO Auto-generated method stub
        var nCurrentSpeed = 0f
        if (location != null) {
            location.setUseMetricunits(useMetricUnits())
            nCurrentSpeed = location.getSpeed()
        }
        val fmt = Formatter(StringBuilder())
        fmt.format(Locale.US, "%5.1f", nCurrentSpeed)
        var strCurrentSpeed = fmt.toString()
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0')
        var strUnits = "miles/hour"
        if (useMetricUnits()) {
            strUnits = "meters/second"
        }
        var kmperhour= strCurrentSpeed.toFloat()*1.6093

        txtCurrentSpeed.text = "$strCurrentSpeed $strUnits  \n $kmperhour km per hour"
    }

    private fun useMetricUnits(): Boolean {
        // TODO Auto-generated method stub
        return chkMetricUnits.isChecked
    }


     override fun onLocationChanged(location: Location) {
         if (location != null) {
             val myLocation = CLocation(location, useMetricUnits())
             updateSpeed(myLocation)
         }
     }

     override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        // TODO Auto-generated method stub
    }

     override fun onProviderEnabled(provider: String) {
         TODO("Not yet implemented")
     }

     override fun onProviderDisabled(provider: String) {
         TODO("Not yet implemented")
     }

     override fun onGpsStatusChanged(event: Int) {
        // TODO Auto-generated method stub
    }
}