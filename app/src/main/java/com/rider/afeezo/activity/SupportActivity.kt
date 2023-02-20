package com.rider.afeezo.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.rider.afeezo.R
import com.rider.afeezo.adapter.FaqAdapter
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.generic.EndlessRecyclerOnScrollListener
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.generic.Utility.Companion.isNetworkConnected
import com.rider.afeezo.generic.Utility.Companion.logD
import com.rider.afeezo.generic.Utility.Companion.showErrorDialog
import com.rider.afeezo.generic.Utility.Companion.showToast
import com.rider.afeezo.interfaces.ResponseHandler
import com.rider.afeezo.model.FaqCategories
import kotlinx.android.synthetic.main.activity_common.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import retrofit2.Response


class SupportActivity : BaseActivity(), ResponseHandler {
    private var faqAdapter: FaqAdapter? = null
    private var currentPage = PAGE_START
    private var layoutManager: LinearLayoutManager? = null
    private var pageSize = "10"
    private var faqCatData = ArrayList<FaqCategories.Records.Categories>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common)
        setToolbar(base_activity_toolbar,resources.getString(R.string.support))
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        setScrollListener()
    }

    var loading = true

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
                    getFaqData(currentPage)
                }
            }
        })
        getFaqData(currentPage)
    }

    /**
     * method used to get support categories  from API
     */
    fun getFaqData(page: Int) {
        try {
            if (!isNetworkConnected(this)) {
                showErrorDialog(this)
                return
            }
            instance.supportCatgeories(
                Constant.SUPPORT_CATEGORIES,
                page.toString(),
                pageSize,
                instance.token,
                this
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onSuccess(tag: Int, response: Response<*>) {
        if (tag == Constant.SUPPORT_CATEGORIES) {
            val categories = response.body() as FaqCategories?
            if (categories != null && categories.status.contentEquals("1")) {
                val TOTAL_PAGES = categories.records!!.paging!!.pageCount!!.toInt()
                setList(categories.records!!.categories)
            } else if (categories != null && categories.status.contentEquals(Constant.SESSION_EXPIRED)) {
                Utility(this).sessionExpire(this)
            } else showToast(this, getString(R.string.error_something_wrong))
        }
    }

    override fun onError(throwable: Throwable) {
        Utility(this).showSnackBar(getString(R.string.server_error))
    }

    /**
     * method used to set faq cat list
     * @param newList
     */
    private fun setList(newList: ArrayList<FaqCategories.Records.Categories>?) {
        if (newList == null) {
            if (faqCatData.size > 0) {
                loading = false
            } else {
                setEmpty(true)
                loading = false
            }
            return
        }
        faqCatData.addAll(newList)
        if (faqCatData.size == 0) {
            setEmpty(true)
            return
        }
        if (faqCatData.size > 0 && newList.size == 0) {
            logD("list empty")
            loading = false
            // return;
        }
        setEmpty(false)
        if (faqAdapter == null) {
            faqAdapter = FaqAdapter(this, faqCatData)
            recycleView.layoutManager = layoutManager
            recycleView.adapter = faqAdapter
        } else {
            faqAdapter!!.notifyDataSetChanged()
        }
    }

    /**
     * method used to set text and recycler view
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