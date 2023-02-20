package com.rider.afeezo.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.rider.afeezo.R
import com.rider.afeezo.adapter.OfferAdapter
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.generic.Utility.Companion.isNetworkConnected
import com.rider.afeezo.generic.Utility.Companion.showErrorDialog
import com.rider.afeezo.generic.Utility.Companion.showToast
import com.rider.afeezo.interfaces.OnCouponSelected
import com.rider.afeezo.interfaces.ResponseHandler
import com.rider.afeezo.model.CouponValidate
import com.rider.afeezo.model.Coupons
import com.rider.afeezo.model.Offers
import kotlinx.android.synthetic.main.activity_offers.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import retrofit2.Response


class OffersActivity : BaseActivity(), ResponseHandler, OnCouponSelected {
    private var offerAdapter: OfferAdapter? = null
    private var coupons: ArrayList<Coupons>? = null
    private var couponCode: String? = null
    private var vehicleType: String? = null
    private var srcLat: String? = null
    private var srcLng: String? = null
    private var destLat: String? = null
    private var destLng: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offers)
        noDataFound.text = getString(R.string.no_coupon_available)
        srcLat = intent.getStringExtra("SourceLat")
        srcLng = intent.getStringExtra("SourceLng")
        destLat = intent.getStringExtra("DestLat")
        destLng = intent.getStringExtra("DestLng")
        vehicleType = intent.getStringExtra("VehicleType")
        utility = Utility(this)
        couponCode = intent.getStringExtra("CouponCode")
        setToolbar(base_activity_toolbar, resources.getString(R.string.offers))
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        tvApplyCode.setOnClickListener {
            if (edtCustomCode.text.toString().isEmpty()) {
                showToast(this, getString(R.string.label_refferal_code))
            } else {
                couponCode = edtCustomCode.text.toString()
                validateCoupon()
            }
        }
        showProgress(false)
        instance.getUserCoupons(
            Constant.GET_USER_COUPONS,
            instance.token,
            srcLat,
            srcLng,
            destLat,
            destLng,
            vehicleType,
            this
        )
    }


    private fun validateCoupon() {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        showProgress(false)
        instance.validateCoupon(
            Constant.COUPON_VALIDATE_API,
            couponCode,
            srcLat,
            srcLng,
            destLat,
            destLng,
            vehicleType,
            instance.token,
            this
        )
    }

    override fun onSuccess(tag: Int, response: Response<*>) {
        hideProgress()
        if (tag == Constant.GET_USER_COUPONS) {
            val offers = response.body() as Offers?
            if (offers != null && offers.status.contentEquals("1")) {
                if (offers.records!!.coupons != null) {
                    coupons = offers.records!!.coupons
                    noDataFound.visibility = View.GONE
                    recycleView.visibility = View.VISIBLE
                    offerAdapter = OfferAdapter(
                        this, offers.records!!.coupons!!, this,
                        offers.currencySymbol,
                        couponCode
                    )
                    recycleView.layoutManager = LinearLayoutManager(this)
                    recycleView.adapter = offerAdapter
                } else {
                    recycleView?.visibility = View.GONE
                    noDataFound?.visibility = View.GONE
                }
            } else if (offers != null && offers.status.contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this)
            } else if (offers != null) showToast(this, offers.msg!!) else showToast(
                this,
                getString(R.string.error_something_wrong)
            )
        } else if (tag == Constant.COUPON_VALIDATE_API) {
            val couponValidate = response.body() as CouponValidate?
            if (couponValidate != null && couponValidate.status.contentEquals("1")) {
                val msg: String =
                    if (couponValidate.coupon!!.coupon_discount_in_percent.contentEquals("1")) couponValidate.coupon!!.coupon_discount_value + getString(
                        R.string.percent_sign
                    ) +
                            " " + getString(R.string.off) else couponValidate.currencySymbol + couponValidate.coupon!!
                        .coupon_discount_value + " " + getString(R.string.flat) + " " + getString(R.string.off)
                showToast(this, couponValidate.msg!!)
                val intentWallet = Intent()
                intentWallet.putExtra(Constant.COUPON_CODE_VALUE, couponCode)
                intentWallet.putExtra(Constant.COUPON_MSG, msg)
                setResult(RESULT_OK, intentWallet)
                finish()
            } else if (couponValidate != null && couponValidate.status.contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this)
            } else if (couponValidate != null) {
                showToast(this, couponValidate.msg!!)
            } else showToast(this, getString(R.string.error_something_wrong))
        }
    }

    override fun onError(throwable: Throwable) {
        hideProgress()
        Utility(this).showSnackBar(getString(R.string.server_error))
    }

    override fun onSelectCoupon(pos: Int) {
        if (callingActivity != null) {
            couponCode = coupons!![pos].code
            validateCoupon()
        }
    }
}