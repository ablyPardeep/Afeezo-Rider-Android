package com.rider.afeezo.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rider.afeezo.R
import com.rider.afeezo.interfaces.onClickListItemListener
import kotlinx.android.synthetic.main.item_money_tab.view.*

class MoneyTabsAdapter(
    var activity: Activity,
    var tabData: ArrayList<String>,
    var itemListener: onClickListItemListener,
    var currencySymbol: String
) : RecyclerView.Adapter<MoneyTabsAdapter.MyHolder>() {
    var pos = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_money_tab, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.itemView.txtAmountVal.text = currencySymbol + tabData[position]
    }

    override fun getItemCount(): Int {
        return tabData.size
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        override fun onClick(view: View) {
            itemListener.onClickItem(adapterPosition)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }
}