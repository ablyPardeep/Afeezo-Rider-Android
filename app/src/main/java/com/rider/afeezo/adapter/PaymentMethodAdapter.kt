package com.rider.afeezo.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rider.afeezo.databinding.ItemCardListLayoutBinding
import com.rider.afeezo.databinding.ItemOtherPaymentMethodBinding
import com.rider.afeezo.databinding.ItemWalletPaymentMethodBinding
import com.rider.afeezo.generic.Constant
import com.rider.afeezo.generic.DataHolder
import com.rider.afeezo.model.CardsItem
import com.rider.afeezo.model.PaymentModes
import kotlinx.android.synthetic.main.nav_header_main.view.*

class PaymentMethodAdapter(
    var activity: Activity,
    var paymentModelList: List<PaymentModes>,
    var itemListener: onClickMethodListener,
    val walletBalance: String,
    var paymentMode: String?

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        var WALLET = 0
        var CARDS = 1
        var OTHERS = 2
    }

    private lateinit var walletPaymentMethodBinding: ItemWalletPaymentMethodBinding
    private lateinit var cardPaymentMethodBinding: ItemCardListLayoutBinding
    private lateinit var otherPaymentMethodBinding: ItemOtherPaymentMethodBinding
    private var cardsItemList = mutableListOf<CardsItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            WALLET -> {
                walletPaymentMethodBinding = ItemWalletPaymentMethodBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return WalletPaymentViewHolder(walletPaymentMethodBinding)
            }
            CARDS -> {
                cardPaymentMethodBinding = ItemCardListLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return CardPaymentViewHolder(cardPaymentMethodBinding)
            }
            OTHERS -> {
                otherPaymentMethodBinding = ItemOtherPaymentMethodBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return OtherPaymentViewHolder(
                    otherPaymentMethodBinding
                )
            }
            else -> {
                walletPaymentMethodBinding = ItemWalletPaymentMethodBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return WalletPaymentViewHolder(walletPaymentMethodBinding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val paymentModes = paymentModelList[position]
        when (holder.itemViewType) {
            CARDS -> {
                (holder as CardPaymentViewHolder).onBind(paymentModes)
            }
            WALLET -> {
                (holder as WalletPaymentViewHolder).onBind(paymentModes, walletBalance)
            }
            OTHERS -> {
                (holder as OtherPaymentViewHolder).onBind(paymentModes)
            }
        }
        /*when (paymentModes.id) {
            CARDS -> {
                holder.itemView.tvAddCardValueMethod.text =
                    activity.getString(R.string.add_a_credit_debit_card)
                holder.itemView.tvCardMethodName.text =
                    activity.getString(R.string.saved_cards)
            }
            WALLET -> {
                holder.itemView.tvAddValueMethod.text =
                    activity.getString(R.string.add_money_to_wallet)
                holder.itemView.tvAvalBal.text = walletBalance
                holder.itemView.tvWalletMethodName.text =
                    activity.getString(R.string.my_wallet)
            }
            OTHERS -> {
                if (paymentModes.id != CARDS && paymentModes.id != WALLET)
                    holder.itemView.tvMethodLabel.text = paymentModes.name
                holder.itemView.tvMethodName.text =
                    activity.getString(R.string.other_pay_methods)
            }
        }*/
        /*if (paymentModes.key == paymentMode) {
            holder.itemView.parentLt.setBackgroundColor(activity.resources.getColor(R.color.colorBlackSelection))
        } else {
            holder.itemView.parentLt.setBackgroundColor(activity.resources.getColor(R.color.white))
        }*/
//        holder.itemView.tvMethodName.text = paymentModes.name
    }

    override fun getItemCount() = paymentModelList.size

    override fun getItemViewType(position: Int): Int {
        val item = paymentModelList[position]
        return when (item.key) {
            Constant.CARD -> CARDS
            Constant.WALLET -> WALLET
            Constant.CASH -> OTHERS
            else -> OTHERS
        }
    }

    interface onClickMethodListener {
        fun onClickPayMethod(id: Int? = 0, pos: Int)
        fun onAddMoneyToWallet() {}
        fun onAddNewCard() {}
        fun onCardClickItem(cardId: String?) {}
        fun onCardDelete(cardId: String?) {}
    }

    inner class OtherPaymentViewHolder(val binding: ItemOtherPaymentMethodBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        override fun onClick(view: View) {
            when (view.id) {
                binding.cardOtherPayment.id -> itemListener.onClickPayMethod(
                    view.id,
                    bindingAdapterPosition
                )
                binding.ivSelect.id -> {
                    if (binding.isCashSelected == false) {
                        paymentMode = Constant.CASH
                        notifyItemChanged(bindingAdapterPosition)
                        binding.cardOtherPayment.performClick()
                    } else {
                        paymentMode = ""
                        notifyItemChanged(bindingAdapterPosition)
//                        binding.ivSelect.isChecked = false
                    }
                }
            }
        }

        init {
            binding.cardOtherPayment.setOnClickListener(this)
            binding.ivSelect.setOnClickListener(this)
        }

        fun onBind(model: PaymentModes?) = with(binding) {
            binding.isCashSelected = paymentMode == Constant.CASH
            executePendingBindings()
        }
    }

    inner class WalletPaymentViewHolder(val binding: ItemWalletPaymentMethodBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                binding.tvAddValueMethod.id -> {
                    itemListener.onAddMoneyToWallet()
                }
                binding.layWallet.id -> {
                    itemListener.onClickPayMethod(pos = bindingAdapterPosition)
                }
                binding.ivSelectWallet.id -> {
                    if (binding.isWalletSelected == false) {
                        paymentMode = Constant.WALLET
                        notifyItemChanged(bindingAdapterPosition)
                        binding.layWallet.performClick()
                    } else {
                        paymentMode = ""
                        notifyItemChanged(bindingAdapterPosition)
                    }
                }
            }
        }

        init {
            binding.layWallet.setOnClickListener(this)
            binding.tvAddValueMethod.setOnClickListener(this)
            binding.ivSelectWallet.setOnClickListener(this)
        }

        fun onBind(model: PaymentModes?, walletBalance: String) = with(binding) {
            isWalletSelected = paymentMode == Constant.WALLET
            tvAvalBal.text = walletBalance
            executePendingBindings()
        }
    }

    inner class CardPaymentViewHolder(val binding: ItemCardListLayoutBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        override fun onClick(v: View?) {

        }

        init {
        }

        fun onBind(model: PaymentModes?) = with(binding) {
        }
    }
}