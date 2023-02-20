package com.rider.afeezo.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Paint
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.rider.afeezo.R
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.generic.Utility.Companion.isNetworkConnected
import com.rider.afeezo.generic.Utility.Companion.showDialog
import com.rider.afeezo.generic.Utility.Companion.showErrorDialog
import com.rider.afeezo.generic.Utility.Companion.showToast
import com.rider.afeezo.interfaces.ResponseHandler
import com.rider.afeezo.model.Common
import com.rider.afeezo.model.FavLocation
import com.rider.afeezo.model.Locations
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_verify_otp.*
import retrofit2.Response
import java.util.regex.Pattern

class VerifyOtp : BaseActivity(), TextWatcher, View.OnClickListener, ResponseHandler {
    private var phoneNumber: String? = null
    private var receiver: BroadcastReceiver? = null
    private var androidId: String? = null
    private var countryNameCode: String? = null
    private var timer: CountDownTimer? = null
    private var millisFinished: Long = 0

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otp)
        phoneNumber = intent.getStringExtra("Phone")
        countryNameCode = intent.getStringExtra("Country")
        otpSent?.text = otpSent?.text.toString()
            .replace("123", "***" + phoneNumber.toString().substring(phoneNumber?.length!! - 2))
        verifyBtn.setOnClickListener(this)
        changeNumber?.setOnClickListener(this)
        imageOtp.setOnClickListener(this)
        utility = Utility(this)
        androidId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val code = parseCode(intent.getStringExtra("message"))
                pinView.setText(code)
                verifyOtp()
            }
        }
        pinView.addTextChangedListener(this)
        pinView.itemWidth = Utility.getItemWidth(this)
        pinView.itemHeight = pinView.itemWidth
        millisFinished = Constant.TIMER
        //setTimer()
        verifyBtn.alpha = 0.4f
        verifyBtn.isEnabled = false
    }

    private fun setTimer() {
        timer = object : CountDownTimer(millisFinished, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                millisFinished = millisUntilFinished
                Log.e("time ", " " + millisUntilFinished / 1000)
                val sec = millisUntilFinished / 1000
                var text = resources.getString(R.string.otp_resend_timer) + " 00:"
                if (sec.toString().length == 1) {
                    text += "0$sec"
                } else {
                    text += sec
                }
                enterOtp?.text = text
                enterOtp?.setOnClickListener(null)
            }

            override fun onFinish() {
                enterOtp?.text = resources.getString(R.string.otp_resend_label)
                enterOtp.paintFlags = enterOtp.paintFlags or Paint.UNDERLINE_TEXT_FLAG

                enterOtp?.setTextColor(resources.getColor(R.color.black))
                enterOtp?.setOnClickListener(this@VerifyOtp)
            }
        }
        timer?.start()
    }

    private fun resendOtp() {
        showProgress(false)
        instance.sendOtp(Constant.SEND_OTP, phoneNumber!!, countryNameCode, this)
    }

    public override fun onResume() {
        setTimer()
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver!!, IntentFilter("otp"))
        super.onResume()
    }

    public override fun onPause() {
        if (timer != null) {
            timer?.cancel()
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver!!)
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (timer != null) {
            timer?.cancel()
        }
    }


    private fun parseCode(message: String?): String {
        val p = Pattern.compile("\\b\\d{6}\\b")
        val m = p.matcher(message)
        var code = ""
        while (m.find()) {
            code = m.group(0)
        }
        return code
    }

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        if (pinView.text.toString().length == 6) {
            verifyBtn.alpha = 1.0f
            verifyBtn.isEnabled = true
            verifyOtp()
        } else {
            verifyBtn.alpha = 0.4f
            verifyBtn.isEnabled = false
        }
    }

    override fun afterTextChanged(editable: Editable) {}

    override fun onClick(view: View) {
        utility.hideSoftKeyboard(view)
        when (view.id) {
            R.id.verifyBtn -> {
                if (pinView.text.toString().isEmpty()) {
                    showToast(this, getString(R.string.otp_verify_zero))
                    return
                }
                if (pinView.text.toString().length < 6) {
                    showToast(this, getString(R.string.otp_verify))
                    return
                }
                verifyOtp()
            }
            R.id.changeNumber -> finish()
            R.id.imageOtp -> finish()
            R.id.enterOtp -> {
                if (enterOtp?.text == resources.getString(R.string.otp_resend_label)) {
                    resendOtp()
                }
            }
        }
    }


    fun verifyOtp() {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        if (pinView.text.toString().isNotEmpty()) {
            verifyBtn.isEnabled = false
            showProgress(false)
            instance.loginWithOtp(
                Constant.LOGIN_WITH_OTP,
                phoneNumber!!,
                pinView.text.toString(),
                androidId!!,
                countryNameCode!!,
                this
            )
        }
    }

    override fun onSuccess(tag: Int, response: Response<*>) {
        if (tag == Constant.LOGIN_WITH_OTP) {
//            if (timer != null) {
//                timer?.cancel()
//            }
            val common = response.body() as Common?
            if (common != null) {
                if (common.status.contentEquals("1")) {
                    println("common.getRider_token() = " + common.rider_token)
                    val prefs = getSharedPreferences(
                        "demopref",
                        MODE_PRIVATE
                    )
                    val editor = prefs.edit()
                    editor.putString(Constant.COMMON_LOGIN, "RIDERAPP")
                    editor.apply()
                    instance.getStore(this).saveString(Constant.RIDER_TOKEN, common.rider_token!!)
                    instance.getStore(this).saveString(Constant.RIDER_ID, common.rider_id!!)
                    instance.getStore(this).saveString(Constant.AUTHORIZE, common.auto_login!!)
                    instance.getStore(this)
                        .saveString(Constant.ASK_PROFILE_INFO, common.ask_profile_info!!)
                    instance.token = common.rider_token!!
                    if (common.ask_profile_info.contentEquals("1")) {
                        hideProgress()
                        startActivity(
                            Intent(
                                this,
                                SignupActivity::class.java
                            ).putExtra(Constant.USER_PHONE, phoneNumber)
                        )
                        finish()
                    } else {
                        favAddress
                    }
                } else {
                    hideProgress()
                    showDialog(this, common.msg)
                }
            } else {
                showToast(this, getString(R.string.error_something_wrong))
            }
        } else if (tag == Constant.GET_FAV_LOCATION) {
            hideProgress()
            val favLocation = response.body() as FavLocation?
            if (favLocation != null && favLocation.status.contentEquals("1") && favLocation.locations != null) {
                val gson = Gson()
                val arrayList = ArrayList<Locations>()
                arrayList.addAll(favLocation.locations!!)
                val list = gson.toJson(arrayList)
                instance.getStore(this).saveString(Constant.FAV_LOCATION, list)
            }
            instance.getStore(this).saveBoolean(Constant.oneTimeCall, true)
            instance.getStore(this).saveString("number" ,phoneNumber.toString())
            startActivity(
                Intent(
                    this,
                    MapFrontActivity::class.java
                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        } else if (tag == Constant.SEND_OTP) {
            hideProgress()
            val common = response.body() as Common?
            if (common != null && common.status.contentEquals("1")) {
                showToast(this, common.msg!!)
                instance.smsSender = common.smsSender!!
                millisFinished = Constant.TIMER
                setTimer()
            } else if (common != null) utility.showSnackBar(common.msg) else utility.showSnackBar(
                getString(R.string.error_something_wrong)
            )
        }
    }

    private val favAddress: Unit
        get() {
            showProgress(false)
            instance.getUserFavoriteLocations(
                Constant.GET_FAV_LOCATION, instance.token,
                this
            )
        }

    override fun onError(throwable: Throwable) {
        hideProgress()
        utility.showSnackBar(getString(R.string.server_error))
    }
}