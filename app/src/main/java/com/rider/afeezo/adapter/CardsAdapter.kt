package com.rider.afeezo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rider.afeezo.R
import com.rider.afeezo.databinding.ItemCardPaymentMethodBinding
import com.rider.afeezo.model.CardsItem

class CardsAdapter(
    var cardsItemList: List<CardsItem>,
    var itemListener: PaymentMethodAdapter.onClickMethodListener
) : RecyclerView.Adapter<CardsAdapter.MyHolder>() {

    private lateinit var binding: ItemCardPaymentMethodBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        binding =
            ItemCardPaymentMethodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyHolder(binding)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.onBind(cardsItemList[position])
    }

    override fun getItemCount(): Int {
        return cardsItemList.size
    }

    inner class MyHolder(val binding: ItemCardPaymentMethodBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        override fun onClick(view: View) {
            when (view.id) {
                binding.ivDeleteCard.id -> {
                    itemListener.onCardDelete(cardsItemList[bindingAdapterPosition].id)
                }
                binding.ivSelect.id -> {
                    if (binding.isCardSelected == false) {
                        cardsItemList[bindingAdapterPosition].isSelected = true
                        notifyItemChanged(bindingAdapterPosition)
                        binding.cardPaymentLayout.performClick()
                    } else {
                        cardsItemList[bindingAdapterPosition].isSelected = false
                        notifyItemChanged(bindingAdapterPosition)
                    }
                }
                binding.cardPaymentLayout.id -> {
                    itemListener.onCardClickItem(cardsItemList[bindingAdapterPosition].id)
                }
            }
        }

        init {
            binding.cardPaymentLayout.setOnClickListener(this)
            binding.ivSelect.setOnClickListener(this)
            binding.ivDeleteCard.setOnClickListener(this)
        }

        fun onBind(item: CardsItem?) = with(binding) {
            tvCardName.text = item?.bank.plus(" ").plus(item?.type)
            tvCardNumber.text = tvCardNumber.context.getString(R.string.empty_card_number).plus(" ")
                .plus(item?.last4)
            isCardSelected = item?.isSelected?:false
            executePendingBindings()
        }
    }
}