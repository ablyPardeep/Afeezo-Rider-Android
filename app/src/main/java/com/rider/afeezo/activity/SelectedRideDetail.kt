package com.rider.afeezo.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.rider.afeezo.R
import com.rider.afeezo.adapter.FinancialReceiptAdapter
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.generic.Utility.Companion.isNetworkConnected
import com.rider.afeezo.generic.Utility.Companion.showErrorDialog
import com.rider.afeezo.generic.Utility.Companion.showToast
import com.rider.afeezo.interfaces.ResponseHandler
import com.rider.afeezo.model.RideDetails
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_selected_ride_detail.*
import kotlinx.android.synthetic.main.dialog_ride_fare_detail.view.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import retrofit2.Response
import java.io.IOException

class SelectedRideDetail : BaseActivity(), ResponseHandler {
    private var rideId: String? = null
    private var bottomSheetDialog: BottomSheetDialog? = null
    var rideDetail: RideDetails? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selected_ride_detail)
        utility = Utility(this)
        rideId = intent.getStringExtra(Constant.RIDE_ID)
        val bookingId = intent.getStringExtra(Constant.BOOKING_ID)
        setToolbar(base_activity_toolbar, " #$bookingId")
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        showProgress(false)
        instance.rideDetail(Constant.RIDE_DETAIL, rideId!!, instance.token, this)
        imgRideDetails.setOnClickListener {
//            showDetailDialog()
            if (fareDetailLayout?.visibility==View.VISIBLE)
                fareDetailLayout?.visibility=View.GONE else fareDetailLayout?.visibility=View.VISIBLE
        }
    }
var carImageUrl=""
    private fun showDetailDialog() {
        val view: View = layoutInflater.inflate(R.layout.dialog_ride_fare_detail, null)
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog?.setContentView(view)
        if (rideDetail?.rideDetail?.financials != null) {
            view.recyclerDetails.adapter = FinancialReceiptAdapter(
                this, rideDetail?.rideDetail?.financials!!, rideDetail?.currencySymbol!!
            )
            view.recyclerDetails.layoutManager = LinearLayoutManager(this)
        }
        view.carType.text = rideDetail?.rideDetail?.ride_vehicle_type_name
        //view.carTypeDesc.text = rideDetail?.rideDetail?.ride_vehicle_type
        //Utility.showImage(this,view.carImage,rideDetail?.rideDetail?.)
        view.tvBaseFare.text = rideDetail?.currencySymbol + rideDetail?.rideDetail?.ride_total_fare
        Utility.showImage(this, view.carImage, carImageUrl)
        bottomSheetDialog?.show()
    }


    override fun onSuccess(tag: Int, response: Response<*>) {
        hideProgress()
        if (tag == Constant.RIDE_DETAIL) {
            rideDetail = response.body() as RideDetails?
            if (rideDetail != null) {
                when {
                    rideDetail?.status.contentEquals("1") -> {
                        parentLt.visibility = View.VISIBLE
                        setRideDetails(rideDetail!!)
                    }
                    rideDetail?.status.contentEquals(Constant.SESSION_EXPIRED) -> {
                        utility.sessionExpire(this)
                    }
                    rideDetail?.status.contentEquals("-1") -> {
                        showToast(this, rideDetail?.msg!!)
                    }
                }
            } else showToast(this, getString(R.string.error_something_wrong))
        }
    }

    /**
     * method used to set ride details
     * @param rideDetails
     */
    @SuppressLint("SetTextI18n")
    private fun setRideDetails(rideDetails: RideDetails) {
        tv_tripDateTime.text =
            Utility.changeTimeFormat(rideDetails.rideDetail!!.ride_booked_time!!)//rideDetails.rideDetail!!.ride_booked_time
        tv_duration.text = rideDetails.rideDetail.ride_time_minutes + " min"
        tv_startLocation.text = rideDetails.rideDetail.ride_start_location
        tv_endLocation.text = rideDetails.rideDetail.ride_end_location
        tv_tripFare.text = rideDetails.currencySymbol + rideDetails.rideDetail.ride_total_fare
        tv_vehicleName.text =
            rideDetails.rideDetail.ride_vehicle_make + " " + rideDetails.rideDetail.ride_vehicle_model
        tv_distance.text = rideDetails.rideDetail.ride_distance_kms + " km"
        tvTripStatus.text = rideDetails.rideDetail.ride_status_name
        if (rideDetails.rideDetail.payments != null && rideDetails.rideDetail.payments.size > 0) {
            tvPaymentMode.text = rideDetails.rideDetail.payments[0].rp_payment_mode
        }
        tvRatingName.text = rideDetails.rideDetail.driverRating
        tv_driverName.text = rideDetails.rideDetail.ride_driver_name
        Utility.showImage(this, imgDriver, rideDetails.rideDetail.driver_image)
        Utility.showImage(this, imgRecent, rideDetails.rideDetail.icon)
        carImageUrl= rideDetails.rideDetail.icon.toString()
        if (rideDetails.rideDetail.map != null) {
            Utility.showImageMap(this, mapImage, rideDetails.rideDetail.map)
        } else {
            noPathFound.visibility = View.VISIBLE
            mapImage.visibility = View.GONE
        }

        /** save value for base fare and total fare */
        if (rideDetails.rideDetail.financials!=null) {
            imgRideDetails.visibility = View.VISIBLE
            tv_baseAmount?.text =
                rideDetails.currencySymbol + rideDetails.rideDetail.financials!![0].value
            tv_totalAmount?.text =
                rideDetails.currencySymbol + rideDetails.rideDetail.financials[1].value
        }else imgRideDetails.visibility = View.GONE
    }

    override fun onError(throwable: Throwable) {
        hideProgress()
        Utility(this).showSnackBar(getString(R.string.server_error))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        base_activity_toolbar.inflateMenu(R.menu.ridedetailmenu)
        base_activity_toolbar.setOnMenuItemClickListener { item -> onOptionsItemSelected(item) }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_help -> startActivity(
                Intent(this, RideIssuesActivity::class.java).putExtra(
                    Constant.RIDE_ID, rideId
                )
            )
        }
        return true
    }

}