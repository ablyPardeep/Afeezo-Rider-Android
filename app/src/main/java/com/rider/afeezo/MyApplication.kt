package com.rider.afeezo

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder
import com.rider.afeezo.generic.LocaleHelper


class MyApplication : Application() {
    /*override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.setLocale(base, DataHolder.instance.getStore(this).getString(Constant.LANGUAGE_CODE)))
    }*/

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        (LocaleHelper.setLocale(this,DataHolder.instance.getStore(this).getString(Constant.LANGUAGE_CODE)))
    }
}
