package com.rider.afeezo.activity

import android.os.Bundle
import android.view.View
import com.rider.afeezo.R
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.generic.Utility.Companion.isNetworkConnected
import com.rider.afeezo.generic.Utility.Companion.showErrorDialog
import com.rider.afeezo.generic.Utility.Companion.showToast
import com.rider.afeezo.interfaces.ResponseHandler
import com.rider.afeezo.model.Common
import kotlinx.android.synthetic.main.activity_withdrawl_request.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import retrofit2.Response

class WithdrawlRequestActivity : BaseActivity(), View.OnClickListener, ResponseHandler {
    var amount: String? = null
    var bankName: String? = null
    var holderName: String? = null
    var accNo: String? = null
    var ifscCode: String? = null
    var bankAdd: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdrawl_request)
        sendRequestBT.setOnClickListener(this)
    }

    /**
     * method used to send withdrawal request from API
     */
    private fun sendWithdrawlRequest() {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        instance.requestWithdrawal(
            Constant.WITHDRAWL_REQUEST,
            instance.token,
            amount,
            bankName,
            holderName,
            accNo,
            ifscCode,
            bankAdd,
            this
        )
    }

    override fun onClick(view: View) {
        if (validate()) {
            sendWithdrawlRequest()
        }
    }

    override fun onSuccess(tag: Int, response: Response<*>) {
        if (tag == Constant.WITHDRAWL_REQUEST) {
            val common = response.body() as Common?
            if (common != null && common.status.contentEquals("1")) {
                showToast(this, common.msg!!)
                setResult(RESULT_OK)
                finish()
            } else if (common != null && common.status.contentEquals(Constant.SESSION_EXPIRED)) {
                Utility(this).sessionExpire(this)
            } else if (common != null) {
                showToast(this, common.msg!!)
            } else showToast(this, getString(R.string.error_something_wrong))
        }
    }

    /**
     * method used to validate all fields
     * @return
     */
    private fun validate(): Boolean {
        amount = amountET.text.toString()
        bankName = bankNameET.text.toString()
        bankAdd = bankaddressET.text.toString()
        ifscCode = ifscET.text.toString()
        accNo = acnoET.text.toString()
        holderName = holdernameET.text.toString()
        when {
            amount!!.isEmpty() -> {
                amountET.requestFocus()
                amountET.setError(getString(R.string.please_fill_amount), null)
                return false
            }
            bankName!!.isEmpty() -> {
                bankNameET.requestFocus()
                bankNameET.setError(getString(R.string.bank_name_mandatory), null)
                return false
            }
            holderName!!.isEmpty() -> {
                holdernameET.requestFocus()
                holdernameET.setError(getString(R.string.account_holder_name_mandatory), null)
                return false
            }
            accNo!!.isEmpty() -> {
                acnoET.requestFocus()
                acnoET.setError(getString(R.string.ac_no_mandatory), null)
                return false
            }
            ifscCode!!.isEmpty() -> {
                ifscET.requestFocus()
                ifscET.setError(getString(R.string.ifsc_code_mandatory), null)
                return false
            }
            bankAdd!!.isEmpty() -> {
                bankaddressET.requestFocus()
                bankaddressET.setError(getString(R.string.bank_address_mandatory), null)
                return false
            }
            else -> return true
        }
    }

    override fun onError(throwable: Throwable) {
        Utility(this).showSnackBar(getString(R.string.server_error))
    }
}