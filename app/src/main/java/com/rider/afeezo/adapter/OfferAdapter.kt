package com.rider.afeezo.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rider.afeezo.R
import com.rider.afeezo.interfaces.OnCouponSelected
import com.rider.afeezo.model.Coupons
import kotlinx.android.synthetic.main.item_offer.view.*

class OfferAdapter(
    var activity: Activity, var coupons: ArrayList<Coupons>,
    var couponSelected: OnCouponSelected, var currencySymbol: String?, var couponCode: String?
) : RecyclerView.Adapter<OfferAdapter.MyHolder>() {
    var pos = -1
    private var lastPos = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_offer, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val offCoupons = coupons[position]
        if (couponCode == offCoupons.code) {
            pos = position
        }
        if (pos == position) {
            holder.itemView.childLt.setBackgroundColor(
                ContextCompat.getColor(
                    activity,
                    R.color.colorBlack50
                )
            )
            holder.itemView.tvApply.text = activity.resources.getString(R.string.coupon_applied)
        } else {
            holder.itemView.childLt.setBackgroundColor(
                ContextCompat.getColor(
                    activity,
                    R.color.white
                )
            )
        }
        if (offCoupons.terms!!.isEmpty()) holder.itemView.tvDescription.text =
            activity.getString(R.string.no_data_found) else holder.itemView.tvDescription.text =
            offCoupons.terms

        setExpandListener(holder, offCoupons, position)
        if (offCoupons.isExpand) {
            expand(holder)
            holder.itemView.termsConditions.text =
                activity.resources.getString(R.string.less_details)
        } else {
            collapse(holder)
            holder.itemView.termsConditions.text =
                activity.resources.getString(R.string.label_more_details)
        }

        if (offCoupons.coupon_discount_in_percent.equals(
                "Percentage",
                ignoreCase = true
            )
        ) holder.itemView.discountValue.text =
            offCoupons.discount_value + activity.getString(R.string.percent_sign) else holder.itemView.discountValue.text =
            currencySymbol + offCoupons.discount_value + " " + offCoupons.coupon_discount_in_percent
        holder.itemView.tvDate.text = offCoupons.end_date
        holder.itemView.codeValue.text = offCoupons.code
    }

    private fun setExpandListener(viewHolder: MyHolder, offCoupons: Coupons, pos: Int) {
        viewHolder.itemView.termsConditions.setOnClickListener {
            if (lastPos > -1) {
                coupons[lastPos].isExpand = false
                notifyItemChanged(lastPos)
            }
            offCoupons.isExpand = !viewHolder.itemView.expandableLayout.isExpanded
            notifyItemChanged(pos)
            lastPos = pos
        }
    }

    private fun expand(viewHolder: MyHolder) {
        val timerThread: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(50)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } finally {
                    activity.runOnUiThread {
                        viewHolder.itemView.expandableLayout.expand(true)
                    }
                }
            }
        }
        timerThread.start()
    }

    private fun collapse(viewHolder: MyHolder) {
        val timerThread: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(50)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } finally {
                    activity.runOnUiThread {
                        viewHolder.itemView.expandableLayout.collapse(true)
                    }
                }
            }
        }
        timerThread.start()
    }

    override fun getItemCount(): Int {
        return coupons.size
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        override fun onClick(view: View) {
            if (pos != adapterPosition) {
                couponSelected.onSelectCoupon(adapterPosition)
                pos = adapterPosition
                notifyDataSetChanged()
            }
        }

        init {
            itemView.tvApply.setOnClickListener(this)
        }
    }
}