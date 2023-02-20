package com.rider.afeezo.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rider.afeezo.R
import com.rider.afeezo.generic.Utility
import com.rider.afeezo.model.Transactions.Txns
import kotlinx.android.synthetic.main.item_transaction_list.view.*

class TransactionAdapter(
    var activity: Activity,
    var txnsData: ArrayList<Txns>,
    var currencySymbol: String
) : RecyclerView.Adapter<TransactionAdapter.MyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction_list, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val txns = txnsData[position]
        holder.itemView.tvId.text = txns.id
        holder.itemView.tvCredit.text = "+" + currencySymbol + txns.credit
        holder.itemView.tvDebit.text = "-" + currencySymbol + txns.debit
        if (txns.credit != "0.00") {
            holder.itemView.lnCredit.visibility = View.VISIBLE
        } else if (txns.debit != "0.00") {
            holder.itemView.lnDebit.visibility = View.VISIBLE
        }

        holder.itemView.tvDesc.text = txns.comments
        holder.itemView.tvDate.text = /*Utility.changeTimeFormat(*/txns.date!!//)
    }

    override fun getItemCount(): Int {
        return txnsData.size
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        override fun onClick(view: View) {}

        init {
            itemView.setOnClickListener(this)
        }
    }
}