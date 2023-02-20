package com.rider.afeezo.adapter

import android.app.Activity
import com.rider.afeezo.model.RideDetail.Payments
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.rider.afeezo.R
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_payment_mode.view.*
import java.util.ArrayList

class PaymentModeAdapter(
    var activity: Activity,
    var payments: ArrayList<Payments>,
    var currencySymbol: String
) : RecyclerView.Adapter<PaymentModeAdapter.MyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_payment_mode, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val pay = payments[position]
        holder.itemView.tv_paymentType.text = pay.rp_payment_mode
        holder.itemView.tv_amount.text = currencySymbol + pay.rp_amount
    }

    override fun getItemCount(): Int {
        return payments.size
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