package com.rider.afeezo.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.rider.afeezo.R
import com.rider.afeezo.adapter.TransactionAdapter
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.generic.Utility.Companion.isNetworkConnected
import com.rider.afeezo.generic.Utility.Companion.logD
import com.rider.afeezo.generic.Utility.Companion.showErrorDialog
import com.rider.afeezo.generic.Utility.Companion.showToast
import com.rider.afeezo.interfaces.ResponseHandler
import com.rider.afeezo.model.Transaction
import com.rider.afeezo.model.Transactions.Txns
import com.rider.afeezo.service.MyFirebaseMessagingService
import kotlinx.android.synthetic.main.activity_wallet.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import retrofit2.Response

class WalletActivity : BaseActivity(), View.OnClickListener, ResponseHandler {
    private var transactionAdapter: TransactionAdapter? = null
    private var pageSize = "10"
    private var layoutManager: LinearLayoutManager? = null
    private var loading = true
    private var isLoading = false
    private var TOTAL_PAGES = 3
    private var currentPage = PAGE_START
    private val txnsList = ArrayList<Txns>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)
        utility = Utility(this)
        lnHeader.setBackgroundColor(resources.getColor(R.color.wallet_toolbar_color))
        setToolbar(base_activity_toolbar, "")
        base_activity_toolbar.setBackgroundColor(resources.getColor(android.R.color.transparent))
        listTransactions.isNestedScrollingEnabled = false
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        btnRequestWithrawal.setOnClickListener(this)
        btnAddMoney.setOnClickListener(this)
        instance.token = instance
            .getStore(this).getString(Constant.RIDER_TOKEN)
        if (MyFirebaseMessagingService.notificationManager != null) MyFirebaseMessagingService.notificationManager!!.cancelAll()
        getWalletTransactions(currentPage)
        setScrollListener()
    }


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
                    getWalletTransactions(currentPage)
                }
            }
        }
    }

    /**
     * method used to set transaction list
     * @param newList
     * @param currencySymbol
     */
    private fun setList(newList: ArrayList<Txns>?, currencySymbol: String?) {
        if (newList == null) {
            isLoading = if (txnsList.size > 0) {
                false
            } else {
                setEmpty(true)
                false
            }
            return
        }
        txnsList.addAll(newList)
        if (txnsList.size == 0) {
            setEmpty(true)
            return
        }
        if (txnsList.size > 0 && newList.size == 0) {
            logD("list empty")
            isLoading = false
            // return;
        }
        setEmpty(false)
        if (transactionAdapter == null) {
            transactionAdapter = TransactionAdapter(this, txnsList, currencySymbol!!)
            listTransactions!!.adapter = transactionAdapter
            listTransactions!!.layoutManager = layoutManager
        } else {
            transactionAdapter!!.notifyDataSetChanged()
        }
    }

    /**
     * method used to set visibility of text and recycler view
     * @param state
     */
    private fun setEmpty(state: Boolean) {
        if (state) {
            emptyTV!!.visibility = View.VISIBLE
            listTransactions!!.visibility = View.GONE
        } else {
            emptyTV!!.visibility = View.GONE
            listTransactions!!.visibility = View.VISIBLE
        }
    }

    /**
     * method used to get wallet transactions from API
     */
    private fun getWalletTransactions(page: Int) {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        showProgress(false)
        instance.transactions(
            Constant.TRANSACTIONS, instance
                .token, page.toString(), pageSize, this
        )
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnAddMoney -> {
                Constant.ACTIVITY = this@WalletActivity.javaClass.simpleName
                startActivityForResult(
                    Intent(this, AddWalletMoney::class.java),
                    Constant.TRANSACTION_REQUEST_CODE
                )
            }
            R.id.btnRequestWithrawal -> startActivityForResult(
                Intent(
                    this,
                    WithdrawlRequestActivity::class.java
                ), Constant.TRANSACTION_REQUEST_CODE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == Constant.TRANSACTION_REQUEST_CODE) {
            getWalletTransactions(currentPage)
        }
    }

    override fun onSuccess(tag: Int, response: Response<*>) {
        hideProgress()
        if (tag == Constant.TRANSACTIONS) {
            val transaction = response.body() as Transaction?
            if (transaction != null && transaction.status.contentEquals("1")) {
                balanceTV!!.text = transaction.currencySymbol + transaction.totUserBalance
                TOTAL_PAGES = transaction.transactions!!.paging?.pageCount!!.toInt()
                setList(transaction.transactions!!.txns, transaction.currencySymbol)
            } else if (transaction != null && transaction.status.contentEquals(Constant.SESSION_EXPIRED)) {
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