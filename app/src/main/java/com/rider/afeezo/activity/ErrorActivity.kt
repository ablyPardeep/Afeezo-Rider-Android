package com.rider.afeezo.activity

import com.rider.afeezo.generic.Utility.Companion.isNetworkConnected
import com.rider.afeezo.generic.Utility.Companion.showErrorDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rider.afeezo.R
import android.widget.TextView
import android.content.Intent
import android.widget.LinearLayout
import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar

class ErrorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)
    }

    fun tryAgain(view: View?) {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        startActivity(Intent(this, MapFrontActivity::class.java))
        finish()
    }

}