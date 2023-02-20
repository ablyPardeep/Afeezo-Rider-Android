package com.rider.afeezo.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.rider.afeezo.R
import com.rider.afeezo.adapter.FaqDetailAdapter
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.generic.EndlessRecyclerOnScrollListener
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.generic.Utility.Companion.isNetworkConnected
import com.rider.afeezo.generic.Utility.Companion.logD
import com.rider.afeezo.generic.Utility.Companion.showErrorDialog
import com.rider.afeezo.generic.Utility.Companion.showToast
import com.rider.afeezo.interfaces.ResponseHandler
import com.rider.afeezo.model.FaqDetail
import com.rider.afeezo.model.FaqDetail.Records.Faqs
import kotlinx.android.synthetic.main.activity_common.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import retrofit2.Response


class FaqDetailActivity : BaseActivity(), ResponseHandler {
    private var detailAdapter: FaqDetailAdapter? = null
    private var faqCategory: String? = ""
    private var layoutManager: LinearLayoutManager? = null
    private var faqId: String? = null
    private var faqData = ArrayList<Faqs>()
    private var loading = true
    private var currentPage = PAGE_START

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common)
        faqId = intent.getStringExtra(Constant.FAQ_ID)
        faqCategory = intent.getStringExtra(Constant.FAQ_CATEGORY)
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        setToolbar(base_activity_toolbar, faqCategory!!)
        setScrollListener()
    }

    /**
     * method used to set scroll listener
     */
    private fun setScrollListener() {
        recycleView.setOnScrollListener(object :
            EndlessRecyclerOnScrollListener(layoutManager!!) {
            override fun onLoadMore(current_page: Int) {
                logD("current_page : $current_page")
                if (loading) {
                    currentPage = current_page
                    getFaqDetails(currentPage)
                }
            }
        })
        getFaqDetails(currentPage)
    }

    /**
     * method used to get support category detail from API
     */
    fun getFaqDetails(page: Int) {
        try {
            if (!isNetworkConnected(this)) {
                showErrorDialog(this)
                return
            }
            showProgress(false)
            instance.supportCategoryDetail(
                Constant.SUPPORT_CATEGORY_DETAIL,
                faqId,
                instance.token,
                this
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onSuccess(tag: Int, response: Response<*>) {
        hideProgress()
        val faqDetail = response.body() as FaqDetail?
        run {
            if (faqDetail != null && faqDetail.status.contentEquals("1")) {
                val TOTAL_PAGES = faqDetail.records!!.paging!!.pageCount!!.toInt()
                setList(faqDetail.records!!.faqs)
            } else if (faqDetail != null && faqDetail.status.contentEquals(Constant.SESSION_EXPIRED)) {
                Utility(this).sessionExpire(this)
            } else showToast(this, getString(R.string.error_something_wrong))
        }
    }

    override fun onError(throwable: Throwable) {
        Utility(this).showSnackBar(getString(R.string.server_error))
    }

    /**
     * method used to set faq list
     * @param newList
     */
    private fun setList(newList: ArrayList<Faqs>?) {
        if (newList == null) {
            loading = if (faqData.size > 0) {
                false
            } else {
                setEmpty(true)
                false
            }
            return
        }
        faqData.addAll(newList)
        if (faqData.size == 0) {
            setEmpty(true)
            return
        }
        if (faqData.size > 0 && newList.size == 0) {
            logD("list empty")
            loading = false
            // return;
        }
        setEmpty(false)
        if (detailAdapter == null) {
            detailAdapter = FaqDetailAdapter(this, faqData)
            recycleView.layoutManager = LinearLayoutManager(this)
            recycleView.adapter = detailAdapter
        } else {
            detailAdapter!!.notifyDataSetChanged()
        }
    }

    /**
     * method used to set visibility of recycler view
     * @param state
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

    companion object {
        private const val PAGE_START = 1
    }
}