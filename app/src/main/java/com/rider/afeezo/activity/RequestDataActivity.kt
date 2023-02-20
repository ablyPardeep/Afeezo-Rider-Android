package com.rider.afeezo.activity

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import com.rider.afeezo.R
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder
import com.rider.afeezo.generic.MyClickableSpan
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.interfaces.ResponseHandler
import com.rider.afeezo.model.Common
import kotlinx.android.synthetic.main.activity_request_data.*
import kotlinx.android.synthetic.main.activity_request_data.etEmail
import kotlinx.android.synthetic.main.activity_request_data.etName
import kotlinx.android.synthetic.main.activity_request_data.submitBtn
import kotlinx.android.synthetic.main.toolbar_layout.*
import retrofit2.Response

class RequestDataActivity : BaseActivity(),ResponseHandler {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_data)
        setToolbar(base_activity_toolbar, resources.getString(R.string.request_data))
        clickSpanText()
        submitBtn.setOnClickListener{
            if(validate())
            requestData
        }

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
        val text = findViewById<TextView>(R.id.tvGdprPolicy)
        val ssb = SpannableStringBuilder()
        addClickableText(ssb, ssb.length, getString(R.string.click_here_gdpr_policy)+" ", Constant.PRIVACY_POLICY)
        text.movementMethod = LinkMovementMethod.getInstance() // make our spans selectable
        text.isClickable=true
        text.text = ssb
    }
    private fun validate(): Boolean {
        var name = etName.text.toString()
        var email = etEmail.text.toString()
        var details = edtRequestdetails.text.toString()
        if (name.trim { it <= ' ' }.isEmpty()) {
            etName.error = getString(R.string.please_enter_name)
            etName.requestFocus()
            return false
        } else if (email.trim { it <= ' ' }.isEmpty()) {
            etEmail.error = getString(R.string.please_enter_email)
            etEmail.requestFocus()
            return false
        } else if (!Utility.isValidEmail(etEmail!!.text.toString())) {
            etEmail.error = getString(R.string.error_invalid_email)
            etEmail.requestFocus()
            return false
        } else if (details.trim { it <= ' ' }.isEmpty()) {
            edtRequestdetails.error = getString(R.string.enter_message)
            edtRequestdetails.requestFocus()
            return false
        }
        return true
    }

    private val requestData: Unit
        get() {
            showProgress(true)
            if (!Utility.isNetworkConnected(this)) {
                Utility.showErrorDialog(this)
                return
            }
            DataHolder.instance.requestData(
                Constant.REQUEST_DATA, DataHolder.instance.token,etName.text.toString(),etEmail.text.toString(),edtRequestdetails.text.toString(),
                this
            )
        }

    override fun onSuccess(tag: Int, response: Response<*>) {
        hideProgress()
        if (tag == Constant.REQUEST_DATA) {
            val common = response.body() as Common?
            if (common != null && common.status.contentEquals("1")) {
                Utility.showToast(this, common.msg.toString())
                finish()
            } else if (common != null && common.status.contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this)
            } else if (common != null) Utility.showToast(this, common.msg!!) else Utility.showToast(
                this,
                getString(R.string.error_something_wrong)
            )
        }
    }

    override fun onError(throwable: Throwable) {
        hideProgress()
        Utility(this).showSnackBar(getString(R.string.server_error))
    }
}