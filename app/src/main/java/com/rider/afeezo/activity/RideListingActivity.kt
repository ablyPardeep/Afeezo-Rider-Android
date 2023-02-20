package com.rider.afeezo.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.rider.afeezo.R
import com.rider.afeezo.adapter.OnClickEventListener
import com.rider.afeezo.adapter.RideListAdapter
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.generic.EndlessRecyclerOnScrollListener
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.generic.Utility.Companion.isNetworkConnected
import com.rider.afeezo.generic.Utility.Companion.logD
import com.rider.afeezo.generic.Utility.Companion.showErrorDialog
import com.rider.afeezo.generic.Utility.Companion.showToast
import com.rider.afeezo.interfaces.ResponseHandler
import com.rider.afeezo.model.MyRides
import com.rider.afeezo.model.Records.Rides
import kotlinx.android.synthetic.main.activity_common.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import retrofit2.Response

class RideListingActivity : BaseActivity(), ResponseHandler, OnClickEventListener {
    var listAdapter: RideListAdapter? = null
    var pageSize = "10"
    var layoutManager: LinearLayoutManager? = null
    private val rideList = ArrayList<Rides>()
    private var isLoading = false
    private var TOTAL_PAGES = 3
    private val isRecentRide: Boolean by lazy {
        intent.getBooleanExtra(
            Constant.IS_RECENT_RIDE,
            false
        )
    }

    private var currentPage = PAGE_START

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common)
        utility = Utility(this)
        if (isRecentRide)
            setToolbar(base_activity_toolbar, resources.getString(R.string.recent_travel_label))
        else
            setToolbar(base_activity_toolbar, resources.getString(R.string.ride_listing))

        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        setScrollListener()
    }

    fun getRideData(page: Int) {
        try {
            if (!isNetworkConnected(this)) {
                showErrorDialog(this)
                return
            }
            showProgress(false)
            if (isRecentRide) {
                instance.myRides(
                    Constant.MY_RIDES,
                    "1",
                    "5",
                    instance.token,
                    this
                )
            } else {
                instance.myRides(
                    Constant.MY_RIDES,
                    page.toString(),
                    pageSize,
                    instance.token,
                    this
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var loading = true

    private fun setScrollListener() {
        recycleView.setOnScrollListener(object :
            EndlessRecyclerOnScrollListener(layoutManager!!) {
            override fun onLoadMore(current_page: Int) {
                logD("current_page : $current_page")
                if (loading && !isRecentRide) {
                    currentPage = current_page
                    getRideData(currentPage)
                }
            }
        })
        getRideData(currentPage)
    }

    override fun onSuccess(tag: Int, response: Response<*>) {
        hideProgress()
        if (tag == Constant.MY_RIDES) {
            val myRides = response.body() as MyRides?
            if (myRides != null && myRides.status.contentEquals("1")) {
                TOTAL_PAGES = myRides.records!!.paging!!.pageCount!!.toInt()
                setList(myRides.records!!.rides, myRides.currencySymbol)
            } else if (myRides != null && myRides.status.contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this)
            } else showToast(this, getString(R.string.error_something_wrong))
        }
    }

    private fun setList(newList: ArrayList<Rides>?, currencySymbol: String?) {
        if (newList == null) {
            isLoading = if (rideList.size > 0) {
                false
            } else {
                setEmpty(true)
                false
            }
            return
        }
        rideList.addAll(newList)
        if (rideList.size == 0) {
            setEmpty(true)
            return
        }
        if (rideList.size > 0 && newList.size == 0) {
            logD("list empty")
            isLoading = false
        }
        setEmpty(false)
        if (listAdapter == null) {
            listAdapter = RideListAdapter(this, rideList, currencySymbol!!, this)
            recycleView.adapter = listAdapter
            recycleView.layoutManager = layoutManager
        } else {
            listAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onRideClick(rideId: String?, bookingId: String?) {
        super.onRideClick(rideId, bookingId)
        if (isRecentRide) {
            val intent =
                Intent().putExtra(Constant.RIDE_ID, rideId).putExtra(Constant.BOOKING_ID, bookingId)
            setResult(RESULT_OK, intent)
            finish()
        } else {
            startActivity(
                Intent(this, SelectedRideDetail::class.java).putExtra(
                    Constant.RIDE_ID, rideId
                ).putExtra(
                    Constant.BOOKING_ID, bookingId
                )
            )
        }
    }


    private fun setEmpty(state: Boolean) {
        if (state) {
            noDataFound.visibility = View.VISIBLE
            recycleView.visibility = View.GONE
        } else {
            noDataFound.visibility = View.GONE
            recycleView.visibility = View.VISIBLE
        }
    }

    override fun onError(throwable: Throwable) {
        hideProgress()
        utility.showSnackBar(getString(R.string.server_error))
    }

    companion object {
        private const val PAGE_START = 1
    }
}