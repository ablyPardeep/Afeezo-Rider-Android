package com.rider.afeezo.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.rider.afeezo.R
import com.rider.afeezo.adapter.CardsAdapter
import com.rider.afeezo.adapter.PaymentMethodAdapter
import com.rider.afeezo.adapter.PaymentMethodAdapter.onClickMethodListener
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder.Companion.instance
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.generic.Utility.Companion.isNetworkConnected
import com.rider.afeezo.generic.Utility.Companion.showErrorDialog
import com.rider.afeezo.generic.Utility.Companion.showToast
import com.rider.afeezo.interfaces.ResponseHandler
import com.rider.afeezo.model.*
import kotlinx.android.synthetic.main.activity_payment_options.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import retrofit2.Response

class PaymentOptionActivity : BaseActivity(), View.OnClickListener, ResponseHandler,
    onClickMethodListener {
    var walletBalance: String? = ""
    var paymentMode: String? = ""
    var pageSize = "10"
    var paymentModesList: ArrayList<PaymentModes>? = ArrayList()
    var paymentOptionsCustomList: ArrayList<PaymentOptionsCustom>? = null
    var methodAdapter: PaymentMethodAdapter? = null
    var orderId: String? = null
    var cardsItemList: ArrayList<CardsItem>? = null
    var cardsAdapter: CardsAdapter? = null
    var cardPosition = 0
    var paymentod_method_position = 0
    var transaction: Transaction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_options)
        utility = Utility(this)
        custom_toolbar_title.text = resources.getString(R.string.label_payments)
        cardsItemList = ArrayList()
        setListeners()
        getWalletTransactions(1)
        paymentMode = instance.getStore(this).getString(Constant.PAYMENT_METHOD)
    }

    override fun onResume() {
        super.onResume()

    }
    private fun setListeners() {
        backBtn.setOnClickListener(this)
        tvAddCardValueMethod.setOnClickListener(this)
    }

    private fun getWalletTransactions(page: Int) {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        showProgress(false)
        instance.transactions(
            Constant.TRANSACTIONS,
            instance.token,
            page.toString(),
            pageSize,
            this
        )
    }

    private fun paymentMethods() {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        showProgress(false)
        instance.paymentMethods(Constant.PAYMENT_OPTIONS, instance.token, this)
    }

    private fun addNewCard() {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        showProgress(false)
        instance.addNewCard(Constant.ADD_CARD, instance.token, this)
    }

    private val savedCards: Unit
        get() {
            if (!isNetworkConnected(this)) {
                showErrorDialog(this)
                return
            }
            showProgress(false)
            instance.getSavedCards(Constant.GET_CARDS, instance.token, this)
        }

    private fun deleteSavedCard(cardId: String?) {
        if (!isNetworkConnected(this)) {
            showErrorDialog(this)
            return
        }
        showProgress(false)
        instance.deleteSavedCard(
            Constant.DELETE_CARD,
            instance.token,
            cardId,
            this
        )
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.backBtn -> onBackPressed()
            R.id.tvAddCardValueMethod -> addNewCard()
        }
    }

    override fun onSuccess(tag: Int, response: Response<*>) {
        hideProgress()
        if (tag == Constant.TRANSACTIONS) {
            paymentMethods()
            savedCards
            transaction = response.body() as Transaction?
            if (transaction != null) {
                if (transaction!!.status.contentEquals("1")) {
                    walletBalance = transaction!!.totUserBalance
                    instance.walletBalance = walletBalance!!
                } else if (transaction!!.status.contentEquals(Constant.SESSION_EXPIRED)) {
                    utility.sessionExpire(this)
                }
            } else showToast(this, getString(R.string.error_something_wrong))

        } else if (tag == Constant.PAYMENT_OPTIONS) {
            val common = response.body() as Common?
            if (common != null) {
                if (common.status.contentEquals("1")) {
                    paymentModesList = (common.paymentModes as ArrayList) ?: arrayListOf()
                    paymentModesList?.onEach {
                        if (it.key == Constant.CARD) paymentModesList?.remove(it)
                    }
            /** check if wallet is disable then remove the wallet option from list*/
                    if(instance.getStore(this@PaymentOptionActivity).getString(Constant.WALLET_ENABLE)=="0")
                        paymentModesList?.onEach {
                            if (it.key == Constant.WALLET ) paymentModesList?.remove(it)
                        }
                    paymentMethodList.layoutManager = LinearLayoutManager(this)

                    methodAdapter = PaymentMethodAdapter(
                        this,
                        paymentModesList ?: listOf(),
                        this,
                        transaction?.currencySymbol + walletBalance,
                        paymentMode
                    )
                    paymentMethodList.adapter = methodAdapter

                } else if (common.status.contentEquals(Constant.SESSION_EXPIRED)) {
                    utility.sessionExpire(this)
                } else showToast(this, common.msg!!)
            } else showToast(this, getString(R.string.error_something_wrong))

        } else if (tag == Constant.ADD_CARD) {
            val common = response.body() as Common?
            if (common != null) {
                when {
                    common.status.contentEquals("1") -> {
                        orderId = common.wallet_order_id
                        instance.getTempToken(Constant.GET_TEMP_TOKEN, instance.token, this)
                    }
                    common.status.contentEquals(Constant.SESSION_EXPIRED) -> {
                        utility.sessionExpire(this)
                    }
                    else -> showToast(this, common.msg!!)
                }
            } else showToast(this, getString(R.string.error_something_wrong))

        } else if (tag == Constant.GET_TEMP_TOKEN) {
            val common = response.body() as Common?
            if (common != null) {
                when {
                    common.status.contentEquals("1") -> {
                        startActivity(
                            Intent(this, AddMoneyWebView::class.java).putExtra("Type", "2")
                                .putExtra(Constant.ORDER_ID, orderId)
                                .putExtra(Constant.TEMP_TOKEN, common.tempToken)
                        )
                    }
                    common.status.contentEquals(Constant.SESSION_EXPIRED) -> {
                        utility.sessionExpire(this)
                    }
                    else -> showToast(this, common.msg!!)
                }
            } else showToast(this, getString(R.string.error_something_wrong))
        } else if (tag == Constant.GET_CARDS) {

            val userCards = response.body() as UserCards?
            if (userCards != null && userCards.status.contentEquals("1")) {
                cardsItemList = userCards.cards
                cardListLt.visibility = View.VISIBLE
                if (cardsItemList != null) {
                    viewFlipper.displayedChild = 0
                    setCardAdapter()
                } else viewFlipper.displayedChild = 1

            } else if (userCards != null && userCards.status.contentEquals(Constant.SESSION_EXPIRED)) {
                utility.sessionExpire(this)
            } else showToast(this, getString(R.string.error_something_wrong))
        } else if (tag == Constant.DELETE_CARD) {

            val common = response.body() as Common?
            if (common != null) {
                if (common.status.contentEquals("1")) {
                    cardsItemList?.removeAt(cardPosition)
                    cardsAdapter?.notifyItemRemoved(cardPosition)
                    if (cardsItemList?.isNullOrEmpty() == true) {
                        viewFlipper.displayedChild = 1

                        if (instance.getStore(this).getString(Constant.PAYMENT_METHOD).equals("CARD", ignoreCase = true)) {
                            instance.getStore(this).saveString(Constant.PAYMENT_METHOD, "CASH")
                            methodAdapter?.notifyDataSetChanged()
                        }
                    }
                } else if (common.status.contentEquals(Constant.SESSION_EXPIRED)) {
                    utility.sessionExpire(this)
                } else showToast(this, common.msg!!)
            } else showToast(
                this,
                getString(R.string.error_something_wrong)
            )
        }
    }

    override fun onError(throwable: Throwable) {
        hideProgress()
        utility.showSnackBar(getString(R.string.server_error))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == Constant.TRANSACTION_REQUEST_CODE) {
            getWalletTransactions(1)
        }
    }

    private fun setCardAdapter() {
        val paymentMethod = instance.getStore(this)
            .getString(Constant.PAYMENT_METHOD) //.equals(item?.id, ignoreCase = true)
        if (paymentMethod == Constant.CARD) {
            val paymentCardId = instance.getStore(this)
                .getString(Constant.PAYMENT_CARD)//.equals(item?.id, ignoreCase = true)

            val selectedItem = cardsItemList?.find { it.id == paymentCardId }
            selectedItem?.isSelected = true
        }

        cardsAdapter = CardsAdapter(cardsItemList ?: listOf(), this)
        savedCardList.layoutManager = LinearLayoutManager(this)
        savedCardList.adapter = cardsAdapter
    }

    override fun onCardDelete(cardId: String?) {
        super.onCardDelete(cardId)
        showDeleteCardDialog(this.resources.getString(R.string.are_you_sure_to_remove_card), cardId)
    }

    override fun onCardClickItem(cardId: String?) {
        super.onCardClickItem(cardId)
        showToast(
            this,
            getString(R.string.set_payment_method).replace(
                "{0}",
                Constant.CARD
            )
        )
        instance.getStore(this).saveString(Constant.PAYMENT_CARD, cardId ?: "")
        instance.getStore(this).saveString(
            Constant.PAYMENT_METHOD,
            Constant.CARD
        )
        finish()
    }

    override fun onClickPayMethod(id: Int?, pos: Int) {
        println("id = [${id}], pos = [${pos}]")
        when (paymentModesList!![pos].key) {
            "CASH" -> {
                showToast(
                    this,
                    getString(R.string.set_payment_method).replace(
                        "{0}",
                        paymentModesList!![pos].name!!
                    )
                )
                instance.getStore(this)
                    .saveString(Constant.PAYMENT_METHOD, paymentModesList!![pos].key!!)
                finish()
            }
            "CARD" -> if (id == R.id.tvAddCardValueMethod) {
                addNewCard()
            } else {
                if (cardsItemList == null || cardsItemList!!.isEmpty() || instance.getStore(this)
                        .getString(
                            Constant.PAYMENT_CARD
                        ).isEmpty()
                ) {
                    showToast(this, getString(R.string.select_card_error))
                    return
                }
                showToast(
                    this,
                    getString(R.string.set_payment_method).replace(
                        "{0}",
                        paymentModesList!![pos].name!!
                    )
                )
                instance.getStore(this)
                    .saveString(Constant.PAYMENT_METHOD, paymentModesList!![pos].key!!)
                finish()
            }
            "WALLET" -> if (id == R.id.tvAddValueMethod) {
                Constant.ACTIVITY = this@PaymentOptionActivity.javaClass.simpleName
                startActivityForResult(
                    Intent(this, AddWalletMoney::class.java),
                    Constant.TRANSACTION_REQUEST_CODE
                )
            } else {
                if (walletBalance!!.isEmpty() || walletBalance!!.toDouble() < 1) {
                    showToast(this, getString(R.string.please_add_money_in_your_wallet))
                    return
                }
                showToast(
                    this,
                    getString(R.string.set_payment_method).replace(
                        "{0}",
                        paymentModesList!![pos].name!!
                    )
                )
                instance.getStore(this)
                    .saveString(Constant.PAYMENT_METHOD, paymentModesList!![pos].key!!)
                finish()
            }
        }
        if (methodAdapter != null) {
            methodAdapter?.notifyDataSetChanged()
        }
    }

    override fun onAddMoneyToWallet() {
        super.onAddMoneyToWallet()
        startActivity(Intent(this, WalletActivity::class.java))

//        startActivityForResult(Intent(this, AddWalletMoney::class.java),10)
    }

    override fun onAddNewCard() {
        super.onAddNewCard()
        addNewCard()
    }

    private fun showDeleteCardDialog(msg: String, cardId: String?) {
        val b = AlertDialog.Builder(this, R.style.MaterialThemeDialog)
        b.setTitle(this.resources.getString(R.string.message))
        b.setMessage(msg)
        b.setCancelable(false)
        b.setPositiveButton(this.resources.getString(R.string.ok)) { dialog, _ ->
            deleteSavedCard(cardId)
            dialog.cancel()
        }
        b.setNegativeButton(this.resources.getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }
        val alert11 = b.create()
        alert11.show()
    }
}