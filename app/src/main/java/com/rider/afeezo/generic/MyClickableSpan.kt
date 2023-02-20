package com.rider.afeezo.generic

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.rider.afeezo.R
import com.rider.afeezo.activity.WebViewActivity

internal class MyClickableSpan(var api: Int, var context: Context) : ClickableSpan() {

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = true // get rid of underlining
        ds.color = ContextCompat.getColor(context, R.color.black) // make links red
    }

    override fun onClick(view: View) {
        Constant.currentOpen = api
        startActivity(context,Intent(context, WebViewActivity::class.java),null)

    }
}