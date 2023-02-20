package com.rider.afeezo.adapter

import android.app.Activity
import com.rider.afeezo.model.RideDetail.Financials
import android.view.ViewGroup
import android.view.LayoutInflater
import com.rider.afeezo.R
import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_payment_mode.view.*
import java.util.ArrayList

class FinancialReceiptAdapter(
    var activity: Activity,
    var financials: ArrayList<Financials>,
    var currencySymbol: String
) : RecyclerView.Adapter<FinancialReceiptAdapter.MyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_payment_mode, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val pay = financials[position]
        var sign: String? = ""
        if (pay.sign != null || !pay.sign!!.isEmpty()) {
            sign = pay.sign
        }
        if (pay.key!!.contains(activity.getString(R.string.total))) {
            holder.itemView.tv_paymentType.typeface = Typeface.DEFAULT_BOLD
            holder.itemView.tv_amount.typeface = Typeface.DEFAULT_BOLD
        } else {
            holder.itemView.tv_paymentType.typeface = Typeface.DEFAULT
            holder.itemView.tv_amount.typeface = Typeface.DEFAULT
        }
        holder.itemView.tv_paymentType.text = pay.key
        holder.itemView.tv_amount.text = sign + currencySymbol + pay.value
    }

    override fun getItemCount(): Int {
        return financials.size
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        override fun onClick(view: View) {
            /*activity.startActivity(new Intent(activity, FaqDetailActivity.class)
                    .putExtra(Constant.FAQ_ID, categories.get(getAdapterPosition()).getId())
                    .putExtra(Constant.FAQ_CATEGORY,categories.get(getAdapterPosition()).getName()));*/
        }

        init {
            itemView.setOnClickListener(this)
        }
    }
}