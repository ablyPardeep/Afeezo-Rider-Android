package com.rider.afeezo.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RatingBar.OnRatingBarChangeListener
import androidx.core.content.ContextCompat
import com.rider.afeezo.R
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.generic.Utility.Companion.isNetworkConnected
import com.rider.afeezo.generic.Utility.Companion.showErrorDialog
import com.rider.afeezo.generic.Utility.Companion.showToast
import com.rider.afeezo.interfaces.ResponseHandler
import com.rider.afeezo.model.Common
import com.rider.afeezo.model.RideDetails
import com.rider.afeezo.service.MyFirebaseMessagingService
import kotlinx.android.synthetic.main.activity_ride_complete.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import retrofit2.Response

class RideCompleteActivity : BaseActivity(), View.OnClickListener, ResponseHandler {
    var rideId: String? = null
    var rating = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ride_complete)
        for (i in 0..4) {
            val view = layoutInflater.inflate(R.layout.star_image_layout, null)
            view.id = i
            ratingLt.addView(view)
            view.setOnClickListener { vi ->
                for (j in 0 until ratingLt.childCount) {
                    if (j <= vi.id) {
                        ratingLt.getChildAt(j)
                            .setBackgroundResource(R.drawable.ic_star_yellow_filled)
                    } else {
                        ratingLt.getChildAt(j).setBackgroundResource(R.drawable.ic_star_grey_filled)
                    }
                }
                rating = vi.id + 1
            }
        }
        submitBtn.setOnClickListener(this)
        driverRatingBar.onRatingBarChangeListener = OnRatingBarChangeListener { ratingBar, _, _ ->
            rating = Math.round(ratingBar.rating)
        }
        setToolbar(base_activity_toolbar, resources.getString(R.string.your_bill))
        rideId = intent.getStringExtra(Constant.RIDE_ID)
        rideData
    }


    override fun onBackPressed() {
        startActivity(
            Intent(
                this,
                MapFrontActivity::class.java
            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }

    /**
     * method used to get ride data from API.
     */
    private val rideData: Unit
        get() {
            if (!isNetworkConnected(this)) {
                showErrorDialog(this)
                return
            }
            showProgress(false)
            instance.rideDetail(Constant.RIDE_DETAIL, rideId!!, instance.token, this)
        }

    /**
     * method sed to submit ride rating
     */
    private fun submitRideRating() {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        showProgress(false)
        instance.rateRide(
            Constant.RIDE_RATE,
            rideId!!,
            rating.toString() + "",
            instance.token,
            this
        )
    }

    override fun onClick(view: View) {
        if (rating > 0) {
            submitRideRating()
        } else showToast(this, getString(R.string.rate_ride_pls))
    }

    override fun onSuccess(tag: Int, response: Response<*>) {
        hideProgress()
        if (tag == Constant.RIDE_DETAIL) {
            val rideDetails = response.body() as RideDetails?
            if (rideDetails != null) {
                when {
                    rideDetails.status.contentEquals("1") -> {
                        instance.rideId = ""
                        srcAddress.text = rideDetails.rideDetail?.ride_start_location
                        destAddress.text = rideDetails.rideDetail?.ride_end_location
                        tvValDuration.text =
                            rideDetails?.rideDetail?.ride_time_minutes + " " + this.resources.getString(
                                R.string.mins
                            )
                        tvValDistance.text =
                            rideDetails.rideDetail?.ride_distance_kms + " " + this.resources.getString(
                                R.string.kms
                            )

                        var check = 0
                        if (rideDetails.rideDetail?.payments != null) {
                            for (item in rideDetails.rideDetail.payments) {
                                if (item.rp_payment_mode.equals(Constant.CASH, true)) {
                                    check = 1
                                    tvPaidWith.text =
                                        resources.getString(R.string.pay_with) + " " + item.rp_payment_mode
                                }
                                billAmount.text =
                                    " " + rideDetails.currencySymbol + item.rp_amount

                            }

                            if (check == 1) {
                                status.text = this.resources.getString(R.string.payment_pending)
                                status.background.setTint(
                                    ContextCompat.getColor(
                                        this,
                                        R.color.wallet_red_color
                                    )
                                )
                            }
                        }
                        if (check == 0) {
                            status.text = rideDetails.rideDetail?.ride_payment_status
                            tvPaidWith.text =
                                resources.getString(R.string.paid_with) + " " + rideDetails.rideDetail?.ride_payment_mode
                        }
                        driverName.text =
                            resources.getString(R.string.how_was_your_ride) + " " + rideDetails.rideDetail?.ride_driver_name
                        Utility.showImage(this, driverImage, rideDetails.rideDetail?.driver_image)
                        if (rideDetails.rideDetail?.payments == null) {
                            tvPaidWith.text =
                                resources.getString(R.string.pay_with) + " " + Constant.CASH
                            status.text=resources.getString(R.string.paid)
                            billAmount.text = rideDetails.currencySymbol + "0.00"
                            status.background.setTint(
                                ContextCompat.getColor(
                                    this,
                                    R.color.green_color
                                )
                            )
                        }
                    }
                    rideDetails.status.contentEquals(Constant.SESSION_EXPIRED) -> {
                        Utility(this).sessionExpire(this)
                    }
                    else -> {
                        showToast(this, rideDetails.msg!!)
                    }
                }
            } else showToast(this, getString(R.string.error_something_wrong))
        } else if (tag == Constant.RIDE_RATE) {
            val common = response.body() as Common?
            if (common != null) {
                instance.getStore(this).saveBoolean(Constant.oneTimeCall, true)
                when {
                    common.status.contentEquals("1") -> {
                        instance.getStore(this).clear(Constant.COMPLETE_RIDE)
                        instance.getStore(this).clear(Constant.oneTimeCall)
                        instance.rideId = ""
                        if (MyFirebaseMessagingService.notificationManager != null) MyFirebaseMessagingService.notificationManager!!.cancelAll()
                        showToast(this, common.msg!!)
                        startActivity(
                            Intent(
                                this,
                                MapFrontActivity::class.java
                            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        )
                    }
                    common.status.contentEquals(Constant.SESSION_EXPIRED) -> {
                        Utility(this).sessionExpire(this)
                    }
                    else -> {
                        showToast(this, common.msg!!)
                        if (MyFirebaseMessagingService.notificationManager != null) MyFirebaseMessagingService.notificationManager!!.cancelAll()
                        instance.getStore(this).clear(Constant.COMPLETE_RIDE)
                        instance.getStore(this).clear(Constant.oneTimeCall)
                        instance.rideId = ""
                        startActivity(
                            Intent(
                                this,
                                MapFrontActivity::class.java
                            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        )
                    }

                }
                instance.getStore(this).saveBoolean(Constant.PROFILE_DOWNLOAD, true)
            } else showToast(this, getString(R.string.error_something_wrong))
        }
    }

    override fun onError(throwable: Throwable) {
        hideProgress()
        Utility(this).showSnackBar(getString(R.string.server_error))
    }
}