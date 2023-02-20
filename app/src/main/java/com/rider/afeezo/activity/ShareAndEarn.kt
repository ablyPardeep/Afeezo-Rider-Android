package com.rider.afeezo.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.rider.afeezo.R
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.interfaces.ResponseHandler
import com.rider.afeezo.model.Common
import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import retrofit2.Response


class ShareAndEarn : BaseActivity(), View.OnClickListener, ResponseHandler {
    var refCode = ""
    var refText = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        refCode = instance.getStore(this).getString(Constant.USER_PHONE)
        referralCode.text = refCode
        shareBtn.setOnClickListener(this)
        setToolbar(base_activity_toolbar, resources.getString(R.string.label_reffer_earn))
        getReferText()
    }

    private fun getReferText() {
        if (!Utility.isNetworkConnected(this)) {
            Utility.showErrorDialog(this)
            return
        }
        instance.shareReferText(
            Constant.REFER_TEXT,
            instance.token,
            this
        )
    }

    override fun onClick(view: View) {
        shareReferCode()
    }

    fun shareReferCode() {
        try {
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            val appPackageName: String =
                this.packageName
            val sAux =
                refText.replace("#APP STORE LINK#", "")
            val uri = "https://play.google.com/store/apps/details?id=" + appPackageName
            i.putExtra(Intent.EXTRA_SUBJECT, "Refer Link")
            i.putExtra(Intent.EXTRA_TEXT, sAux + " " + uri)
            startActivity(Intent.createChooser(i, "choose one"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSuccess(tag: Int, response: Response<*>) {
        if (tag == Constant.REFER_TEXT) {
            val common = response.body() as Common?
            if (common != null) {
                when {
                    common.status.contentEquals("1") -> {
                        refText = common.referText.toString()
                    }
                    common.status.contentEquals(Constant.SESSION_EXPIRED) -> {
                        Utility(this).sessionExpire(this)
                    }
                    else -> {
                        Utility.showToast(this, common.msg.toString())

                    }
                }
            }
        }
    }

    override fun onError(throwable: Throwable) {
        Utility(this).showSnackBar(getString(R.string.server_error))
    }
}