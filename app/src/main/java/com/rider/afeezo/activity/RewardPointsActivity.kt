package com.rider.afeezo.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.rider.afeezo.R
import com.rider.afeezo.adapter.RewardsAdapter
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.generic.Utility.Companion.isNetworkConnected
import com.rider.afeezo.generic.Utility.Companion.logD
import com.rider.afeezo.generic.Utility.Companion.showErrorDialog
import com.rider.afeezo.generic.Utility.Companion.showToast
import com.rider.afeezo.interfaces.ResponseHandler
import com.rider.afeezo.model.RewardPointsArr.RewardPoints
import com.rider.afeezo.model.Rewards
import com.rider.afeezo.service.MyFirebaseMessagingService
import kotlinx.android.synthetic.main.activity_wallet.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import retrofit2.Response

class RewardPointsActivity : BaseActivity(), ResponseHandler {
    private var rewardsAdapter: RewardsAdapter? = null
    private var pageSize = "10"
    private var layoutManager: LinearLayoutManager? = null
    private var isLoading = false
    private var TOTAL_PAGES = 3
    private var currentPage = PAGE_START
    private val rewardList = ArrayList<RewardPoints>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)
        walletText.setText(R.string.reward_points)
        btnAddMoney.visibility = View.GONE
        utility = Utility(this)
        setToolbar(base_activity_toolbar, "")
        base_activity_toolbar.setBackgroundColor(resources.getColor(android.R.color.transparent))
        lnHeader.setBackgroundColor(resources.getColor(R.color.light_yellow))
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        instance.token = instance
            .getStore(this).getString(Constant.RIDER_TOKEN)
        if (MyFirebaseMessagingService.notificationManager != null) MyFirebaseMessagingService.notificationManager!!.cancelAll()
        setScrollListener()
    }

    var loading = false

    /**
     * method used to set scroll listener
     */
    private fun setScrollListener() {
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            val view = scrollView.getChildAt(scrollView.childCount - 1)
            val diff = view.bottom - (scrollView.height + scrollView.scrollY)
            if (diff == 0) {
                if (loading) {
                    currentPage++
                    logD("onScrollChanged $currentPage")
                    getRewardPoints(currentPage)
                }
            }
        }
        getRewardPoints(currentPage)
    }

    private fun setList(newList: ArrayList<RewardPoints>?, currencySymbol: String?) {
        if (newList == null) {
            loading = false
            return
        }
        rewardList.addAll(newList)
        if (rewardList.size == 0) {
            loading = false
            setEmpty(true)
            return
        }
        setEmpty(false)
        if (rewardsAdapter == null) {
            rewardsAdapter = RewardsAdapter(this, rewardList, currencySymbol!!)
            listTransactions!!.adapter = rewardsAdapter
            listTransactions!!.layoutManager = layoutManager
        } else {
            rewardsAdapter!!.notifyDataSetChanged()
        }
        loading = true
    }

    /**
     * method used to set visibility of text view and recycler view
     * @param state
     */
    private fun setEmpty(state: Boolean) {
        if (state) {
            emptyTV.visibility = View.VISIBLE
            listTransactions.visibility = View.GONE
        } else {
            emptyTV.visibility = View.GONE
            listTransactions.visibility = View.VISIBLE
        }
    }

    /**
     * method used to get reward points from API
     */
    private fun getRewardPoints(page: Int) {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        showProgress(false)
        instance.rewardPoints(
            Constant.REWARD_POINTS, instance
                .token, page.toString(), pageSize, this
        )
    }


    override fun onSuccess(tag: Int, response: Response<*>) {
        hideProgress()
        if (tag == Constant.REWARD_POINTS) {
            val rewards = response.body() as Rewards?
            if (rewards != null && rewards.status.contentEquals("1")) {
                TOTAL_PAGES = rewards.rewardPointsArr!!.paging!!.pageCount!!.toInt()
                balanceTV.text = rewards.totPoints + " " + resources.getString(R.string.points)
                if (rewards.rewardPointsArr!!.rewardPoints != null) setList(
                    rewards.rewardPointsArr!!.rewardPoints,
                    rewards.currencySymbol
                )
            } else if (rewards != null && rewards.status.contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this)
            } else showToast(this, getString(R.string.error_something_wrong))
        }
    }

    override fun onError(throwable: Throwable) {
        hideProgress()
        Utility(this).showSnackBar(getString(R.string.server_error))
    }

    companion object {
        private const val PAGE_START = 1
    }
}