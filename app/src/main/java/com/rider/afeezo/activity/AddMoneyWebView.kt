package com.rider.afeezo.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.rider.afeezo.BuildConfig
import com.rider.afeezo.R
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder.Companion.instance
import kotlinx.android.synthetic.main.activity_web_view.*
import kotlinx.android.synthetic.main.toolbar_layout.*

class AddMoneyWebView : BaseActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        setToolbar(
            base_activity_toolbar,
            if (intent.getStringExtra("Type")
                    .equals("1", ignoreCase = true)
            ) resources.getString(R.string.add_money_wallet)
            else resources.getString(R.string.add_a_credit_card)
        )
        webView.settings.loadsImagesAutomatically = true
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        showProgress(false)
        webView.webViewClient = CustomWebViewClient()
        val postData = "ttkn=" + intent.getStringExtra(Constant.TEMP_TOKEN) + "&type=" +
                intent.getStringExtra("Type") + "&order_id=" + intent.getStringExtra(Constant.ORDER_ID) +
                "&_user_token=" + instance.token
        println("postData = $postData")
        webView.postUrl(
            BuildConfig.BASE_URL + Constant.URL_SEND_TO_WEB,
            getBytes(postData, "BASE64")
        )
    }


    private fun getBytes(data: String, charset: String?): ByteArray {
        //requireNotNull(data) { "data may not be null" }
        //require(!(charset == null || charset.isEmpty())) { "charset may not be null or empty" }
        return try {
            data.toByteArray(charset(charset!!))
        } catch (e: Exception) {
            data.toByteArray()
        }
    }

    private inner class CustomWebViewClient : WebViewClient() {
        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            println(url)
            showProgress(false)
        }

        override fun onPageFinished(view: WebView, url: String) {
            println("view = [$view], url = [$url]")
            hideProgress()
            if (intent.getStringExtra("Type").equals("1", ignoreCase = true)) {
                if (url.contains("payment-success")) {
                    instance.getStore(this@AddMoneyWebView).saveBoolean(Constant.PROFILE_DOWNLOAD, true)
                    if (Constant.ACTIVITY == WalletActivity::class.java.simpleName) startActivity(
                        Intent(
                            this@AddMoneyWebView,
                            WalletActivity::class.java
                        ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    ) else startActivity(
                        Intent(
                            this@AddMoneyWebView,
                            PaymentOptionActivity::class.java
                        ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    )
                    this@AddMoneyWebView.finish()
                }

            } else {
                if (url.contains("card/success")) {
                    startActivity(
                        Intent(
                            this@AddMoneyWebView,
                            PaymentOptionActivity::class.java
                        ).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    )
                    this@AddMoneyWebView.finish()
                }
            }
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
    }
}