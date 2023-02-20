package com.rider.afeezo.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.rider.afeezo.R
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.generic.Utility.Companion.isNetworkConnected
import com.rider.afeezo.generic.Utility.Companion.showErrorDialog
import com.rider.afeezo.generic.Utility.Companion.showToast
import com.rider.afeezo.interfaces.ResponseHandler
import com.rider.afeezo.model.Common
import kotlinx.android.synthetic.main.activity_web_view.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import retrofit2.Response

/**
 * Single activity used to load 4 web url differently.
 */
class WebViewActivity : BaseActivity(), ResponseHandler {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        utility = Utility(this)
        webView.settings.loadsImagesAutomatically = true
        webView.settings.javaScriptEnabled = true
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        webView.webViewClient = CustomWebViewClient()
        setToolbar(base_activity_toolbar, "")
        when (Constant.currentOpen) {
            Constant.ABOUT_US -> {
                aboutUs()
                custom_toolbar_title!!.text = getString(R.string.about_us)
            }
            Constant.CONTACT_US -> {
                contactUs()
                custom_toolbar_title!!.text = getString(R.string.contact_us)
            }
            Constant.PRIVACY_POLICY -> {
                privacyPolicy()
                custom_toolbar_title!!.text = getString(R.string.privacy_policy)
            }
            else -> {
                termsAndConditions()
                custom_toolbar_title!!.text = getString(R.string.termsConditions)
            }
        }
    }

    /**
     * method used to get contact us call from API
     */
    private fun contactUs() {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        showProgress(false)
        instance.commonApiCall(Constant.CONTACT_US_API, instance.token, this)
    }

    /**
     * method used to get privacy policy from API
     */
    private fun privacyPolicy() {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        showProgress(false)
        instance.commonApiCall(Constant.PRIVACY_POLICY_API, instance.token, this)
    }

    /**
     * method used to get about us from API
     */
    private fun aboutUs() {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        showProgress(false)
        instance.commonApiCall(Constant.ABOUT_US_API, instance.token, this)
    }

    /**
     * method used to get terms and conditions from API
     */
    private fun termsAndConditions() {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        showProgress(false)
        instance.commonApiCall(Constant.TERMS_COND_API, instance.token, this)
    }

    override fun onSuccess(tag: Int, response: Response<*>) {
        if (tag == Constant.CONTACT_US_API || tag == Constant.ABOUT_US_API || tag == Constant.TERMS_COND_API || tag == Constant.PRIVACY_POLICY_API) {
            val common = response.body() as Common?
            if (common != null && common.status.contentEquals("1")) {
                webView.loadUrl(common.web_url!!)
            } else if (common != null && common.status.contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this)
            } else showToast(this, getString(R.string.error_something_wrong))
        }
    }

    override fun onError(throwable: Throwable) {
        hideProgress()
        Utility(this).showSnackBar(getString(R.string.server_error))
    }

    /**
     * custom web client
     */
    private inner class CustomWebViewClient : WebViewClient() {
        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            showProgress(false)
            println("url = $url")
        }

        override fun onPageFinished(view: WebView, url: String) {
            hideProgress()
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
    }
}