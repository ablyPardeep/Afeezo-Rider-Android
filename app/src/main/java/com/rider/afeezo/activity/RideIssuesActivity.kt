package com.rider.afeezo.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.rider.afeezo.R
import com.rider.afeezo.adapter.RideIssueAdapter
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.generic.Utility.Companion.isNetworkConnected
import com.rider.afeezo.generic.Utility.Companion.showErrorDialog
import com.rider.afeezo.generic.Utility.Companion.showToast
import com.rider.afeezo.interfaces.ResponseHandler
import com.rider.afeezo.interfaces.onClickListItemListener
import com.rider.afeezo.model.Common
import com.rider.afeezo.model.RideIssue
import com.rider.afeezo.model.RideIssue.RideIssues
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_issue_list.*
import kotlinx.android.synthetic.main.dialog_ride_comments.view.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import retrofit2.Response

class RideIssuesActivity : BaseActivity(), View.OnClickListener, ResponseHandler,
    onClickListItemListener {
    private val MAX_COMMENT_LENGHTH = 200
    private var rideId: String? = null
    private var issueComments = ""
    private var position = 0
    private var listAdapter: RideIssueAdapter? = null
    private var issuesList: ArrayList<RideIssues>? = null
    private var bottomSheetDialog: BottomSheetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue_list)
        setToolbar(base_activity_toolbar, getString(R.string.choose_issue))
        rideId = intent.getStringExtra(Constant.RIDE_ID)
        rideIssues
    }

    /**
     * method used to get ride issues from API
     */
    private val rideIssues: Unit
        get() {
            if (!isNetworkConnected(this)) {
                showErrorDialog(this)
                return
            }
            showProgress(false)
            instance.rideIssues(Constant.RIDE_ISSUES, instance.token, this)
        }

    /**
     * method used to report ride issue from API
     */
    private fun reportRideIssue() {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        showProgress(false)
        instance.reportIssueRide(
            Constant.REPORT_RIDE_ISSUE,
            instance.token,
            rideId,
            issuesList!![position].id,
            issueComments,
            this
        )
    }

    /**
     * method used to set adapter
     */
    private fun setList() {
        if (issuesList == null) {
            setEmpty(true)
            return
        }
        setEmpty(false)
        if (listAdapter == null) {
            listAdapter = RideIssueAdapter(this, issuesList!!, this)
            recycleView.adapter = listAdapter
            recycleView.layoutManager = LinearLayoutManager(this)
        } else {
            listAdapter!!.notifyDataSetChanged()
        }
    }

    /**
     * method used to set text and recyclwer view visibility
     */
    private fun setEmpty(state: Boolean) {
        if (state) {
            noDataFound.visibility = View.VISIBLE
            recycleView.visibility = View.GONE
        } else {
            noDataFound.visibility = View.GONE
            recycleView.visibility = View.VISIBLE
        }
    }

    override fun onClick(view: View) {}

    override fun onSuccess(tag: Int, response: Response<*>) {
        hideProgress()
        if (tag == Constant.RIDE_ISSUES) {
            val issue = response.body() as RideIssue?
            if (issue != null && issue.status.contentEquals("1")) {
                issuesList = issue.rideIssues
                setList()
            } else if (issue != null && issue.status.contentEquals(Constant.SESSION_EXPIRED)) {
                Utility(this).sessionExpire(this)
            } else showToast(this, getString(R.string.error_something_wrong))
        } else if (tag == Constant.REPORT_RIDE_ISSUE) {
            val common = response.body() as Common?
            if (common != null) {
                when {
                    common.status.contentEquals("1") -> {
                        bottomSheetDialog?.dismiss()
                        showToast(this, common.msg!!)
                        finish()
                    }
                    common.status.contentEquals(Constant.SESSION_EXPIRED) -> {
                        Utility(this).sessionExpire(this)
                    }
                    else -> {
                        bottomSheetDialog?.dismiss()
                        showToast(this, common.msg!!)
                    }
                }
            } else showToast(this, getString(R.string.error_something_wrong))
        }
    }

    override fun onError(throwable: Throwable) {
        hideProgress()
        Utility(this).showSnackBar(getString(R.string.server_error))
    }

    override fun onClickItem(pos: Int) {
        position = pos
        setCommentsDialog()
    }

    /**
     * method used to set comments dialog
     */
    private fun setCommentsDialog() {
        val view: View = layoutInflater.inflate(R.layout.dialog_ride_comments, null)
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog?.setContentView(view)
        view.txtCount.text =
            "$MAX_COMMENT_LENGHTH/$MAX_COMMENT_LENGHTH"
        view.txtIssue.text = issuesList!![position].name
        view.comments.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                view.txtCount.text =
                    (MAX_COMMENT_LENGHTH - editable.length).toString() + "/" + MAX_COMMENT_LENGHTH
            }
        })
        view.okBtn.setOnClickListener {
            issueComments = view.comments.text.toString()
            if (issueComments.isNotEmpty()) reportRideIssue() else showToast(
                this@RideIssuesActivity,
                getString(R.string.please_enter_comments)
            )
        }
        bottomSheetDialog?.show()
    }
}