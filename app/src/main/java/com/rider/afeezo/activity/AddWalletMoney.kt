package com.rider.afeezo.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.rider.afeezo.R
import com.rider.afeezo.adapter.MoneyTabsAdapter
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.generic.Utility.Companion.isNetworkConnected
import com.rider.afeezo.generic.Utility.Companion.showErrorDialog
import com.rider.afeezo.generic.Utility.Companion.showToast
import com.rider.afeezo.interfaces.ResponseHandler
import com.rider.afeezo.interfaces.onClickListItemListener
import com.rider.afeezo.model.Common
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_nav_drawer.*
import kotlinx.android.synthetic.main.dialog_add_money.view.*
import retrofit2.Response

class AddWalletMoney : BaseActivity(), View.OnClickListener,
    ResponseHandler, onClickListItemListener {
    private var amount: String? = ""
    private var tabsAdapter: MoneyTabsAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    private var tabsList: ArrayList<String>? = null
    private var orderId: String? = null
    private var minLoadMoney: String? = "0"
    private var currencySymbol: String? = null
    var bottomSheetDialog: BottomSheetDialog? = null
    lateinit var view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        utility = Utility(this)
        layoutManager = LinearLayoutManager(this)
        view = layoutInflater.inflate(R.layout.dialog_add_money, null)
        bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog?.setContentView(view)
        view.nextBtn.setOnClickListener(this)
        view.tvViewTrans.setOnClickListener(this)
        moneyTabs
        bottomSheetDialog?.show()
        bottomSheetDialog?.setOnDismissListener {
            finish()
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.nextBtn -> {
                if (!isNetworkConnected(this)) {
                    showErrorDialog(this)
                    return
                }
                amount = this.view.etAmount.text.toString()
                when {
                    amount!!.isEmpty() -> {
                        showToast(this, getString(R.string.please_select_or_enter_the_value))
                    }
                    amount!!.toInt() < minLoadMoney!!.toInt() -> {
                        showToast(
                            this,
                            getString(R.string.min_load_money_error).replace(
                                "{currency}",
                                currencySymbol!!
                            ).replace("{0}", minLoadMoney!!)
                        )
                    }
                    else -> {
                        showProgress(false)
                        instance.setUpWalletRecharge(
                            Constant.SET_UP_WALLET,
                            amount!!,
                            instance.token,
                            this
                        )
                    }
                }
            }
            R.id.backBtn -> {
                utility.hideSoftKeyboard()
                onBackPressed()
            }
            R.id.tvViewTrans->{
                startActivity(Intent(this, WalletActivity::class.java))
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
                //drawer_layout.closeDrawers()
            }
        }
    }

    override fun onSuccess(tag: Int, response: Response<*>) {
        hideProgress()
        if (tag == Constant.SET_UP_WALLET) {
            val common = response.body() as Common?
            if (common != null && common.status.contentEquals("1")) {
                orderId = common.wallet_order_id
                showProgress(false)
                instance.getTempToken(Constant.GET_TEMP_TOKEN, instance.token, this)
            } else if (common != null && common.status.contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this)
            } else if (common != null) showToast(this, common.msg!!) else showToast(
                this,
                getString(R.string.error_something_wrong)
            )
        } else if (tag == Constant.GET_TEMP_TOKEN) {
            val common = response.body() as Common?
            if (common != null && common.status.contentEquals("1")) {
                disableTouch()
                startActivity(
                    Intent(this, AddMoneyWebView::class.java).putExtra("Type", "1").putExtra(
                        Constant.ORDER_ID, orderId
                    ).putExtra(Constant.TEMP_TOKEN, common.tempToken)
                )
                bottomSheetDialog?.dismiss()
            } else if (common != null && common.status.contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this)
            } else if (common != null) showToast(this, common.msg!!) else showToast(
                this,
                getString(R.string.error_something_wrong)
            )
        } else if (tag == Constant.GET_MONEY_TABS) {
            val common = response.body() as Common?
            if (common != null && common.status.contentEquals("1")) {
                minLoadMoney = common.minLoadMoney
                currencySymbol = common.currencySymbol
                if (common.tabs != null) {
                    tabsList = ArrayList()
                    for (tabName in common.tabs!!) {
                        tabsList!!.add(tabName)
                    }
                    tabsAdapter = MoneyTabsAdapter(this, tabsList!!, this, common.currencySymbol!!)
                    layoutManager?.orientation = LinearLayoutManager.HORIZONTAL
                    view.listMoneyTabs.layoutManager = layoutManager
                    view.listMoneyTabs.adapter = tabsAdapter
                }
            } else if (common != null && common.status.contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this)
            } else if (common != null) showToast(this, common.msg!!) else showToast(
                this,
                getString(R.string.error_something_wrong)
            )
        }
    }

    /**
     * method used to disable touch
     */
    private fun disableTouch() {
        val disableTimer = object : CountDownTimer(3 * 1000, 1000) {
            override fun onTick(l: Long) {
                this@AddWalletMoney.window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            }

            override fun onFinish() {
                hideProgress()
                this@AddWalletMoney.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
        disableTimer.start()
    }

    override fun onError(throwable: Throwable) {
        hideProgress()
        utility.showSnackBar(getString(R.string.server_error))
    }

    /**
     * method used to get money tabs
     */
    private val moneyTabs: Unit
        get() {
            if (!isNetworkConnected(this)) {
                showErrorDialog(this)
                return
            }
            showProgress(false)
            instance.quickLoadMoneyTabs(Constant.GET_MONEY_TABS, instance.token, this)
        }

    override fun onClickItem(pos: Int) {
        view.etAmount.setText(tabsList!![pos])
        amount = ""
        view.etAmount.setSelection(view.etAmount.text.length)
//        view.nextBtn.performClick()
    }
}