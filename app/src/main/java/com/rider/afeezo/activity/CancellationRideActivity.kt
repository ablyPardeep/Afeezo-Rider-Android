package com.rider.afeezo.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import com.rider.afeezo.R
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.generic.Utility.Companion.isNetworkConnected
import com.rider.afeezo.generic.Utility.Companion.showErrorDialog
import com.rider.afeezo.generic.Utility.Companion.showToast
import com.rider.afeezo.interfaces.ResponseHandler
import com.rider.afeezo.model.Common
import com.rider.afeezo.model.RideCancel
import com.rider.afeezo.model.RideCancel.Reasons
import com.rider.afeezo.service.MyFirebaseMessagingService
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_ride_cancellation.view.*
import retrofit2.Response


class CancellationRideActivity : BaseActivity(), View.OnClickListener, ResponseHandler,
    RadioGroup.OnCheckedChangeListener {
    private var reasonId = ""
    private var reasonsList: ArrayList<Reasons>? = null
    private var posForComments: Int? = null
    private var cancelText: String? = null
    var bottomSheetDialog: BottomSheetDialog? = null
    lateinit var view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reasonsList = ArrayList()
        utility = Utility(this)
        view = layoutInflater.inflate(R.layout.activity_ride_cancellation, null)
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog?.setContentView(view)
        bottomSheetDialog?.show()
        bottomSheetDialog?.setOnDismissListener {
            finish()
        }
        view.submitBtn.setOnClickListener(this)
        if (intent.getStringExtra(Constant.CANCEL_CHARGE)!!.toDouble() > 0) {
            view.cancelChargeLt.visibility = View.VISIBLE
            view.cancelCharge.text =
                Constant.CURRENCY + intent.getStringExtra(Constant.CANCEL_CHARGE)
            cancelText = intent.getStringExtra(Constant.CANCEL_TEXT)
            view.cancelChargeText.text = cancelText
        } else {
            view.cancelChargeLt.visibility = View.GONE
        }

        cancleReason


    }

    /**
     * method used to get ride cancel reason from API
     */
    private val cancleReason: Unit
        get() {
            if (!isNetworkConnected(this)) {
                showErrorDialog(this)
                return
            }
            showProgress(false)
            instance.rideCancelReasons(
                Constant.RIDE_CANCEL_REASON, instance.getStore(this).getString(
                    Constant.RIDER_TOKEN
                ), this
            )
        }

    override fun onClick(vi: View) {
        when (vi.id) {
            R.id.submitBtn -> {
                if (!isNetworkConnected(this)) {
                    showErrorDialog(this)
                    return
                }
                if (reasonsList != null) {
                    if (reasonId.isEmpty()) {
                        showToast(this, getString(R.string.please_select_reason_for_cancellation))
                    } else if (view.commentsLayout.visibility == View.VISIBLE && (view.edtCanceldetails.text.toString()
                            .isEmpty() || view.edtCanceldetails.text.length < Constant.MIN_CANCEL_WORDS)
                    ) {
                        showToast(
                            this, getString(R.string.atleast_min_words).replace(
                                "{0}", java.lang.String.valueOf(
                                    Constant.MIN_CANCEL_WORDS
                                )
                            )
                        )
                    } else {
                        showProgress(false)
                        instance.cancelRide(
                            Constant.CANCEL_RIDE,
                            instance.rideId,
                            reasonId,
                            view.edtCanceldetails.text.toString(),
                            instance.token,
                            this
                        )
                    }
                } else {
                    showToast(this, getString(R.string.cancel_reason))
                }
            }
            R.id.backBtn -> onBackPressed()
        }
    }

    override fun onSuccess(tag: Int, response: Response<*>) {
        hideProgress()
        if (tag == Constant.CANCEL_RIDE) {
            val common = response.body() as Common?
            if (common != null && common.status.contentEquals("1")) {
                instance.getStore(this).clear(Constant.oneTimeCall)
                instance.rideId = ""
                showToast(this, common.msg!!)
                bottomSheetDialog?.dismiss()
                if (MyFirebaseMessagingService.notificationManager != null) MyFirebaseMessagingService.notificationManager!!.cancelAll()
            setResult(Activity.RESULT_OK)
            finish()
            /*startActivity(
                    Intent(
                        this@CancellationRideActivity,
                        MapFrontActivity::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )*/
            } else if (common != null && common.status.contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this)
            } else if (common != null) {
                showToast(this, common.msg!!)
            } else showToast(this, getString(R.string.error_something_wrong))
        } else if (tag == Constant.RIDE_CANCEL_REASON) {
            val rideCancel = response.body() as RideCancel?
            if (rideCancel != null && rideCancel.status.contentEquals("1")) {
                reasonsList = rideCancel.reasons
                if (reasonsList != null) addRadioButtons(reasonsList) else showToast(
                    this,
                    getString(R.string.cancel_reason)
                )
            } else if (rideCancel != null && rideCancel.status.contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this)
            } else showToast(this, getString(R.string.error_something_wrong))
        }
    }

    /**
     * method used to add radio buttons
     * @param reasons
     */
    private fun addRadioButtons(reasons: ArrayList<Reasons>?) {
        if (reasons == null) return
        val ll = RadioGroup(this)
        ll.orientation = LinearLayout.VERTICAL
        for (i in reasons.indices) {
            val rdbtn = RadioButton(this)
            rdbtn.id = reasons[i].rcreason_id!!.toInt()
            rdbtn.text = reasons[i].rcreason_title
            rdbtn.setTextColor(resources.getColor(R.color.black))
            if (reasons[i].rcreason_display_comment_box == "1") {
                posForComments = i
            }
            ll.addView(rdbtn)
        }
        (view.radioGroupCancelReason as ViewGroup).addView(ll)
        ll.setOnCheckedChangeListener(this)
    }

    override fun onError(throwable: Throwable) {
        hideProgress()
        utility.showSnackBar(getString(R.string.server_error))
    }

    override fun onCheckedChanged(radioGroup: RadioGroup, i: Int) {
        val radioId = radioGroup.checkedRadioButtonId
        val rb = findViewById<RadioButton>(radioId)
        if (rb != null) {
            rb.isChecked = true
        }
        reasonId = radioId.toString()
        if (posForComments != null) {
            if (reasonId == reasonsList!![posForComments!!].rcreason_id) {
                view.commentsLayout.visibility = View.VISIBLE
                view.edtCanceldetails.setText("")
            } else {
                view.commentsLayout.visibility = View.GONE
            }
        }
    }
}