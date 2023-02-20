package com.rider.afeezo.activity

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.rider.afeezo.R
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.interfaces.ResponseHandler
import com.google.gson.Gson
import kotlinx.android.synthetic.main.layout_progress.view.*
import kotlinx.android.synthetic.main.toolbar_layout.view.*
import retrofit2.Response
import java.sql.SQLOutput
import java.util.*

open class BaseActivity : AppCompatActivity(), ResponseHandler {
    lateinit var utility: Utility
    var gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        utility = Utility(this)
        instance.token = instance.getStore(this).getString(Constant.RIDER_TOKEN)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }
    fun setLangRecreate(activity: Activity, langval: String) {
        println("activity = [${activity}], langval = [${langval}]")
        val config = activity.baseContext.resources.configuration
        val locale = Locale(langval)
        Locale.setDefault(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale)
            config.setLayoutDirection(locale)
        } else config.locale = locale
        activity.baseContext.resources.updateConfiguration(config, activity.baseContext.resources.displayMetrics
        )

    }
    /**
     * Common loader used throughout the app
     */
    @Suppress("DEPRECATION")
    fun showProgress(isCancelable: Boolean) {
        try {
            if (loadingDialog != null)
                loadingDialog?.dismiss()
            val builder = AlertDialog.Builder(this, R.style.MaterialThemeDialog)
            val li = LayoutInflater.from(this)
            val promptsView = li.inflate(R.layout.layout_progress, null)
            builder.setView(promptsView)
            //promptsView.rotateloading.start()
            loadingDialog = builder.create()
            loadingDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            loadingDialog?.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED)
            loadingDialog?.setCancelable(isCancelable)
            loadingDialog?.setCanceledOnTouchOutside(false)
            loadingDialog?.show()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun hideProgress() {
        try {
            if (loadingDialog != null) {
                loadingDialog?.dismiss()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }



    /**
    * Common method to set toolbar and back press on custom back button
    */
    fun setToolbar(toolbar: Toolbar, title: String) {
        toolbar.backBtn.setOnClickListener { onBackPressed() }
        toolbar.custom_toolbar_title.text = title
        setSupportActionBar(toolbar)
    }

    companion object {
        private var loadingDialog: AlertDialog? = null
    }

    override fun onSuccess(tag: Int, response: Response<*>) {
        hideProgress()
    }

    override fun onError(throwable: Throwable) {
        hideProgress()
        utility.showSnackBar(getString(R.string.server_error))
    }

}