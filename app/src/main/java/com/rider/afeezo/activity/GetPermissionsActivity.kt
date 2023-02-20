package com.rider.afeezo.activity

import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.generic.Utility.Companion.isNetworkConnected
import com.rider.afeezo.generic.Genric
import android.os.Bundle
import com.rider.afeezo.R
import android.content.pm.PackageManager
import android.content.Intent
import android.view.View
import com.rider.afeezo.generic.Constant

class GetPermissionsActivity : BaseActivity() {
    var genric: Genric? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_permissons)
        genric = Genric(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                genric!!.checkContactPermission()
            }
            2 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                genric!!.checkStoragePermission()
            }
            3 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                genric!!.checkLocPermission()
            }
            4 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                instance.getStore(this).saveBoolean(Constant.askedPermission, false)
                if (!isNetworkConnected(this)) {
                    startActivity(Intent(this, ErrorActivity::class.java))
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                finish()
            } else if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Genric(this).permissionAlert(Constant.GPS)
            }
            5 -> {
            }
        }
    }


    fun getPermissions(view: View?): Boolean {
        if (genric!!.checkLocPermission()) {
            instance.getStore(this).saveBoolean(Constant.askedPermission, false)
            if (!isNetworkConnected(this)) {
                startActivity(Intent(this, ErrorActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }
        /* int currentAPIVersion = android.os.Build.VERSION.SDK_INT;
        if (currentAPIVersion <= android.os.Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }

        int coarseLoc = ContextCompat.checkSelfPermission(this, Manifest.permission
                .ACCESS_COARSE_LOCATION);
        int fineLoc = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int contacts = ContextCompat.checkSelfPermission(this, Manifest.permission
                .READ_CONTACTS);
        int callPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int receiveSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        int readSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);

        if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_MMS);
        }
        if (readSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (coarseLoc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (fineLoc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (contacts != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        int readStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (readStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (writeStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (callPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
        }*/return true
    }
}