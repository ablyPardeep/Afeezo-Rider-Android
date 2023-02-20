package com.rider.afeezo.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import com.rider.afeezo.R
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.generic.Utility.Companion.isNetworkConnected
import com.rider.afeezo.generic.Utility.Companion.showErrorDialog
import com.rider.afeezo.generic.Utility.Companion.showToast
import com.rider.afeezo.interfaces.ResponseHandler
import com.rider.afeezo.model.Common
import com.hbb20.CountryCodePicker.OnCountryChangeListener
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Response

class LoginActivity : BaseActivity(), OnCountryChangeListener, View.OnClickListener,
    ResponseHandler {
    private var phoneNumber= String()
    private var countryNameCode= String()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        utility = Utility(this)
        initView()

    }

    private fun initView() {
        startBtn.alpha = 0.4f
        startBtn.isEnabled = false
        codepicker.setOnCountryChangeListener(this)
        startBtn.setOnClickListener(this)
        etPhoneNo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().length < 15 && p0.toString().length > 5  ) {
                    startBtn.alpha = 1.0f
                    startBtn.isEnabled = true
                } else {
                    startBtn.alpha = 0.4f
                    startBtn.isEnabled = false
                }
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
       etPhoneNo?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                startBtn?.performClick()
            }
            false
        }
    }

    override fun onCountrySelected() {
        countryNameCode = codepicker.selectedCountryNameCode
        codepicker.invalidate()
    }

    override fun onClick(view: View) {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        when (view.id) {
            R.id.startBtn -> {
                utility.hideSoftKeyboard()
                instance.getStore(this).saveString(
                    Constant.RIDER_NUmber,
                    etPhoneNo.text.toString()
                )

                //temp basis
                if (etPhoneNo.text.toString().isNotEmpty()) {
                    if (!(etPhoneNo.text.length < 15 && etPhoneNo.text.length > 5)) {
                        showToast(this, resources.getString(R.string.invalid_number))
                        return
                    }
                    countryNameCode = codepicker.selectedCountryNameCode
                    phoneNumber = etPhoneNo.text.toString()
                    showProgress(false)
                    instance.getStore(this).saveString(Constant.COUNRTY_CODE, countryNameCode)
                    instance.getStore(this).saveString(Constant.PHONE_CODE, codepicker.selectedCountryCodeWithPlus)
                    instance.sendOtp(Constant.SEND_OTP, phoneNumber, countryNameCode, this)
                } else {
                    showToast(this, resources.getString(R.string.please_enter_phone_number))
                }
            }
        }
    }

    override fun onSuccess(tag: Int, response: Response<*>) {
        hideProgress()
        if (tag == Constant.SEND_OTP) {
            val common = response.body() as Common?
            if (common != null && common.status.contentEquals("1")) {
                showToast(this, common.msg!!)
                instance.smsSender = common.smsSender!!
                startActivity(
                    Intent(this, VerifyOtp::class.java).putExtra("Phone", phoneNumber)
                        .putExtra("Country", countryNameCode)
                )
            } else if (common != null) utility.showSnackBar(common.msg) else utility.showSnackBar(
                getString(R.string.error_something_wrong)
            )
        }
    }

    override fun onError(throwable: Throwable) {
        hideProgress()
        utility.showSnackBar(getString(R.string.server_error))
    }

}