package com.rider.afeezo.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.rider.afeezo.R
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.generic.Genric
import com.rider.afeezo.generic.MyClickableSpan
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.generic.Utility.Companion.isNetworkConnected
import com.rider.afeezo.interfaces.ResponseHandler
import com.rider.afeezo.model.GetConfiguration
import kotlinx.android.synthetic.main.activity_splash.*
import retrofit2.Response


class SplashActivity : BaseActivity() {
    private var timerThread: Thread? = null
    private var genric: Genric? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        } else {
            val decorView = window.decorView
            val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
            decorView.systemUiVisibility = uiOptions
        }
        setContentView(R.layout.activity_splash)
        instance.getStore(this).saveBoolean(Constant.oneTimeCall, true)
        if (instance.getStore(this).getString(Constant.PAYMENT_METHOD).isEmpty()) {
            instance.getStore(this).saveString(Constant.PAYMENT_METHOD, "CASH")
        }
        instance.getStore(this).saveBoolean(Constant.PROFILE_DOWNLOAD, true)
//        setLangRecreate(this, instance?.getStore(this)?.getString(Constant.LANGUAGE_CODE).toString())
        clickSpanText()

//        clickPolicySpanText()

        continueBtn.setOnClickListener {
            gotoLogin()
            finish()
        }
        genric = Genric(this)
        getConfigurations()
        checkToken()

    }

    fun goToHome() {
        instance.token = instance.getStore(this).getString(Constant.RIDER_TOKEN)
        Constant.currentOpen = Constant.HOME
        if (instance.getStore(this).getString(Constant.COMPLETE_RIDE).isNotEmpty()) startActivity(
            Intent(this@SplashActivity, RideCompleteActivity::class.java).putExtra(
                Constant.RIDE_ID, instance.getStore(this).getString(Constant.COMPLETE_RIDE)
            )
        ) else startActivity(Intent(this@SplashActivity, MapFrontActivity::class.java))
    }
    private fun addClickableText(
        ssb: SpannableStringBuilder,
        startPos: Int,
        clickableText: String,
        api: Int
    ) {
        ssb.append(clickableText)
        ssb.setSpan(
            MyClickableSpan(api,this),
            startPos,
            ssb.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    private fun clickSpanText(){
        val text = findViewById<TextView>(R.id.txtTerms)
        val ssb = SpannableStringBuilder(getString(R.string.terms_text))
        addClickableText(ssb, ssb.length, "Terms of Service", Constant.TERMS_COND)
        ssb.append(" and ")
        addClickableText(ssb, ssb.length, "Privacy Policy", Constant.PRIVACY_POLICY)
        text.movementMethod = LinkMovementMethod.getInstance() // make our spans selectable
        text.isClickable=true
        text.text = ssb
    }

    private fun checkToken() {
        if (instance.getStore(this@SplashActivity).getString(Constant.AUTHORIZE)
                .contentEquals("1")
        ) {
            if (instance.getStore(this@SplashActivity)
                    .getString(Constant.ASK_PROFILE_INFO).contentEquals("0")
            ) {
                timerThread?.start()
            } else {
                Thread.sleep(1000)
                continueLayout.visibility = View.VISIBLE
            }
        } else {
            Thread.sleep(1000)
            continueLayout.visibility = View.VISIBLE
        }
    }

    private fun gotoLogin() {
        startActivity(
            Intent(
                this@SplashActivity,
                LoginActivity::class.java
            )
        )
    }


    /** Function for buy subscription*/

    private fun getConfigurations() {

        instance.getConfigurations(Constant.GET_CONFIGURATION, instance.token, object :
            ResponseHandler {
            override fun onSuccess(tag: Int, response: Response<*>) {

                if (tag == Constant.GET_CONFIGURATION) {
                    val data = response.body() as GetConfiguration?
                    if (data != null ) {
                        if (data.status.contentEquals("1")) {
                        timerThread = object : Thread() {
                            override fun run() {
                                try {
                                    sleep(1500)
                                } catch (e: InterruptedException) {
                                    e.printStackTrace()
                                } finally {
                                    val currentAPIVersion = Build.VERSION.SDK_INT
                                    if (currentAPIVersion >= Build.VERSION_CODES.M) {
                                        if (instance.getStore(this@SplashActivity)
                                                .getBoolean(Constant.askedPermission, true)
                                        ) {
                                            startActivity(
                                                Intent(
                                                    this@SplashActivity,
                                                    GetPermissionsActivity::class.java
                                                )
                                            )
                                            finish()
                                        } else if (!isNetworkConnected(this@SplashActivity)) {
                                            startActivity(
                                                Intent(
                                                    this@SplashActivity,
                                                    ErrorActivity::class.java
                                                )
                                            )
                                            finish()
                                        } else {
                                            if (instance.getStore(this@SplashActivity)
                                                    .getString(Constant.AUTHORIZE)
                                                    .contentEquals("1")
                                            ) {
                                                if (instance.getStore(this@SplashActivity)
                                                        .getString(Constant.ASK_PROFILE_INFO)
                                                        .contentEquals("0")
                                                ) {
                                                    goToHome()
                                                } else {
                                                    gotoLogin()
                                                }
                                            } else {
                                                gotoLogin()
                                            }
                                            finish()
                                        }
                                    } else {
                                        if (!isNetworkConnected(this@SplashActivity)) {
                                            startActivity(
                                                Intent(
                                                    this@SplashActivity,
                                                    ErrorActivity::class.java
                                                )
                                            )
                                        } else {
                                            if (instance.getStore(this@SplashActivity)
                                                    .getString(Constant.AUTHORIZE)
                                                    .contentEquals("1")
                                            ) {
                                                if (instance.getStore(this@SplashActivity)
                                                        .getString(Constant.ASK_PROFILE_INFO)
                                                        .contentEquals("0")
                                                ) {
                                                    goToHome()
                                                } else {
                                                    gotoLogin()
                                                }
                                            } else {
                                                gotoLogin()
                                            }
                                        }
                                        finish()
                                    }
                                }
                            }
                        }
                        if (instance.getStore(this@SplashActivity).getString(Constant.AUTHORIZE)
                                .contentEquals("1")
                        ) {
                            timerThread?.start()
                        } else {
                            //Thread.sleep(1000)
                            continueLayout.visibility = View.VISIBLE
                        }

                        instance.getStore(this@SplashActivity).saveString(
                            Constant.WALLET_ENABLE,
                            data.data.wallet_module_enabled.toString()
                        )
                 /** save value for refer enable */
                            instance.getStore(this@SplashActivity).saveString(
                                Constant.REFERRAL_ENABLE,
                                data.data.referral.enabled
                            )
                            /** save value for schedule ride */
//                            instance.getStore(this@SplashActivity).saveString(
//                                Constant.SCHEDULE_RIDE_ENABLE,
//                                data.data.scheduled_ride_module_enabled
//                            )

                            /**save value for reward options */
                            instance.getStore(this@SplashActivity).saveString(
                                Constant.REWARD_ENABLE,
                                "1"
                            )

                    } else Utility(this@SplashActivity).showSnackBar(data.msg)
                    }  else Utility(this@SplashActivity).showSnackBar(getString(R.string.server_error))
                }
            }

            override fun onError(throwable: Throwable) {
                hideProgress()
                utility.showSnackBar(getString(R.string.server_error))
            }

        })
    }
}